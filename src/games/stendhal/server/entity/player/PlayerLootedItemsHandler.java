package games.stendhal.server.entity.player;

import java.util.HashMap;
import java.util.Map;

/**
 * Handling for counting looted items of a player
 *
 * @author madmetzger
 */
public class PlayerLootedItemsHandler {

	/** name of the map where the items and the corresponding numbers are stored */
	public static final String LOOTED_ITEMS = "looted_items";

	private final Player player;

	private final Map<String, Integer> looted;

	private final Map<String, Integer> produced;

	private final Map<String, Integer> obtained;

	private final Map<String, Integer> mined;

	private final Map<String, Integer> harvested;

	private final Map<String, Integer> bought;

	private final Map<String, Integer> sold;

	/**
	 * Create a new PlayerLootedItemsHandler for a player
	 *
	 * @param player
	 */
	public PlayerLootedItemsHandler(Player player) {
		this.player = player;
		looted = new HashMap<String, Integer>();
		produced = new HashMap<String, Integer>();
		obtained = new HashMap<String, Integer>();
		mined = new HashMap<String, Integer>();
		harvested = new HashMap<String, Integer>();
		bought = new HashMap<String, Integer>();
		sold = new HashMap<String, Integer>();
		if(player.hasMap(LOOTED_ITEMS)) {
			for(String item : player.getMap(LOOTED_ITEMS).keySet()) {
				if(item.contains("produced.")) {
					produced.put(item.replace("produced.", ""), player.getInt(LOOTED_ITEMS, item));
				}
				if(item.contains("obtained.")) {
					obtained.put(item.replace("obtained.", ""), player.getInt(LOOTED_ITEMS, item));
				}
				if(item.contains("mined.")) {
					mined.put(item.replace("mined.", ""), player.getInt(LOOTED_ITEMS, item));
				}
				if(item.contains("harvested.")) {
					harvested.put(item.replace("harvested.", ""), player.getInt(LOOTED_ITEMS, item));
				}
				if(item.contains("bought.")) {
					bought.put(item.replace("bought.", ""), player.getInt(LOOTED_ITEMS, item));
				}
				if(item.contains("sold.")) {
					bought.put(item.replace("sold.", ""), player.getInt(LOOTED_ITEMS, item));
				}
				if(!item.contains("produced.") && !item.contains("obtained.") && !item.contains("mined.")
						&& !item.contains("harvested.") && !item.contains("bought.") && !item.contains("sold.")) {
					looted.put(item, player.getInt(LOOTED_ITEMS, item));
				}
			}
		}
	}

	/**
	 * Gets the how often this PlayerLootedItemsHandler's player has looted the given item
	 * @param item the item name
	 * @return the number of loots from corpses
	 */
	public int getNumberOfLootsForItem(String item) {
		Integer lootedNumber = looted.get(item);
		if(lootedNumber != null) {
			return lootedNumber.intValue();
		}
		return 0;
	}

	/**
	 * Retrieve the amount of much an item was produced by a player
	 *
	 * @param item
	 * @return the produced quantity
	 */
	public int getQuantityOfProducedItems(String item) {
		if(produced.containsKey(item)) {
			return produced.get(item);
		}
		return 0;
	}

	/**
	 * Retrieve the amount of much an item was harvested by a player
	 *
	 * @param item
	 * @return the harvested quantity
	 */
	public int getQuantityOfHarvestedItems(String item) {
		if(harvested.containsKey(item)) {
			return harvested.get(item);
		}
		return 0;
	}

	/**
	 * Retrieve the amount of much an item was bought by a player
	 *
	 * @param item
	 * @return the harvested quantity
	 */
	public int getQuantityOfBoughtItems(final String item) {
		if (bought.containsKey(item)) {
			return bought.get(item);
		}

		return 0;
	}

	/**
	 *
	 * @param item
	 * @return
	 */
	public int getQuantityOfSoldItems(final String item) {
		if (sold.containsKey(item)) {
			return sold.get(item);
		}

		return 0;
	}

	/**
	 * Retrieve the amount of much an item was mined by a player
	 *
	 * @param item
	 * @return the mined quantity
	 */
	public int getQuantityOfMinedItems(String item) {
		if(mined.containsKey(item)) {
			return mined.get(item);
		}
		return 0;
	}

	/**
	 * Increases the count of loots for the given item for this PlayerLootedItemsHandler's player
	 * @param item the item name
	 * @param count the amount to increase
	 */
	public void incLootForItem(String item, int count) {
		handlePrefixedCounting(item, count, "", looted);
	}

	/**
	 * Increases the count of producing for the given item for this PlayerLootedItemsHandler's player
	 *
	 * @param item the item name
	 * @param count the amount to increase
	 */
	public void incProducedForItem(String item, int count) {
		handlePrefixedCounting(item, count, "produced.", produced);
	}

	/**
	 * Increases the count of obtains for the given item for this PlayerLootedItemsHandler's player
	 *
	 * @param item the item name
	 * @param count the amount to increase
	 */
	public void incObtainedForItem(String item, int count) {
		handlePrefixedCounting(item, count, "obtained.", obtained);
	}

	/**
	 * Increases the quantity an item was mined from a source like gold, coal
	 *
	 * @param item
	 * @param count
	 */
	public void incMinedForItem(String item, int count) {
		handlePrefixedCounting(item, count, "mined.", mined);
	}

	/**
	 * Increases the quantity an item was harvested
	 *
	 * @param item
	 * @param count
	 */
	public void incHarvestedForItem(String item, int count) {
		handlePrefixedCounting(item, count, "harvested.", harvested);
	}

	/**
	 * Increases the quantity an item was bought
	 *
	 * @param item
	 * @param count
	 */
	public void incBoughtForItem(String item, int count) {
		handlePrefixedCounting(item, count, "bought.", bought);
	}

	/**
	 * Increases the quantity an item was sold
	 *
	 * @param item
	 * @param count
	 */
	public void incSoldForItem(String item, int count) {
		handlePrefixedCounting(item, count, "sold.", sold);
	}

	/**
	 * handles redundant storage of counted items in player object and a separate map
	 *
	 * @param item the item to count
	 * @param count how much to increment
	 * @param prefix the prefix to use for the map withing the player object
	 * @param redundantMap additional map for storing
	 */
	private void handlePrefixedCounting(String item, int count, String prefix, Map<String, Integer> redundantMap) {
		StringBuilder key = new StringBuilder(prefix);
		key.append(item);
		if(!player.has(LOOTED_ITEMS, key.toString())) {
			player.put(LOOTED_ITEMS, key.toString(), 0);
		}
		if(!redundantMap.containsKey(item)) {
			redundantMap.put(item, 0);
		}
		int current = player.getInt(LOOTED_ITEMS, key.toString());
		int increased = current + count;
		redundantMap.put(item, increased);
		player.put(LOOTED_ITEMS, key.toString(), increased);
	}

	/**
	 * @return the whole number of items a player has obtained from the well
	 */
	public int getQuantityOfObtainedItems() {
		int sum = 0;
		for(Integer count : obtained.values()) {
			sum = sum + count.intValue();
		}
		return sum;
	}
}
