package games.stendhal.server.entity.player;

import java.util.HashMap;
import java.util.Map;

/**
 * Handling for counting looted items of a player
 * 
 * @author madmetzger
 */
public class PlayerLootedItemsHandler {
	
	private static final String LOOTED_ITEMS = "looted_items";
	private final Player player;
	
	private final Map<String, Integer> looted;
	
	private final Map<String, Integer> produced;
	
	private final Map<String, Integer> obtained;
	
	private final Map<String, Integer> mined;
	
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
		if(player.hasMap(LOOTED_ITEMS)) {
			for(String item : player.getMap(LOOTED_ITEMS).keySet()) {
				if(item.contains("produced.")) {
					produced.put(item.replace("produced.", ""), player.getInt(LOOTED_ITEMS, item));
				}
				if(item.contains("obtained.")) {
					obtained.put(item.replace("obtained.", ""), player.getInt(LOOTED_ITEMS, item));
				}
				if(item.contains("mined.")) {
					obtained.put(item.replace("mined.", ""), player.getInt(LOOTED_ITEMS, item));
				}
				if(!item.contains("produced.") && !item.contains("obtained.") && !item.contains("mined.")) {
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
		if(player.containsKey(LOOTED_ITEMS, item)) {
			return player.getInt(LOOTED_ITEMS, item);
		}
		return 0;
	}
	
	/**
	 * Increases the count of loots for the given item for this PlayerLootedItemsHandler's player
	 * @param item the item name
	 * @param count the amount to increase
	 */
	public void incLootForItem(String item, int count) {
		if(!player.has(LOOTED_ITEMS, item)) {
			player.put(LOOTED_ITEMS, item, 0);
		}
		if(!looted.containsKey(item)) {
			looted.put(item, 0);
		}
		int current = player.getInt(LOOTED_ITEMS, item);
		int increased = current + count;
		looted.put(item, increased);
		player.put(LOOTED_ITEMS, item, increased);
	}

	/**
	 * Increases the count of producing for the given item for this PlayerLootedItemsHandler's player
	 * 
	 * @param item the item name
	 * @param count the amount to increase
	 */
	public void incProducedForItem(String item, int count) {
		if(!player.has(LOOTED_ITEMS, "produced."+item)) {
			player.put(LOOTED_ITEMS, "produced."+item, 0);
		}
		if(!looted.containsKey(item)) {
			looted.put(item, 0);
		}
		int current = player.getInt(LOOTED_ITEMS, "produced."+item);
		int increased = current + count;
		produced.put(item, increased);
		player.put(LOOTED_ITEMS, "produced."+item, increased);
	}

	/**
	 * Increases the count of obtains for the given item for this PlayerLootedItemsHandler's player
	 * 
	 * @param item the item name
	 * @param count the amount to increase
	 */
	public void incObtainedForItem(String item, int count) {
		if(!player.has(LOOTED_ITEMS, "obtained."+item)) {
			player.put(LOOTED_ITEMS, "obtained."+item, 0);
		}
		if(!obtained.containsKey(item)) {
			obtained.put(item, 0);
		}
		int current = player.getInt(LOOTED_ITEMS, "obtained."+item);
		int increased = current + count;
		obtained.put(item, increased);
		player.put(LOOTED_ITEMS, "obtained."+item, increased);
	}
	
	/**
	 * Increases the quantity an item was mined/obtained from a source like fish, gold, coal
	 * 
	 * @param item
	 * @param count
	 */
	public void incMinedForItem(String item, int count) {
		if(!player.has(LOOTED_ITEMS, "mined."+item)) {
			player.put(LOOTED_ITEMS, "mined."+item, 0);
		}
		if(!mined.containsKey(item)) {
			mined.put(item, 0);
		}
		int current = player.getInt(LOOTED_ITEMS, "mined."+item);
		int increased = current + count;
		mined.put(item, increased);
		player.put(LOOTED_ITEMS, "mined."+item, increased);
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
