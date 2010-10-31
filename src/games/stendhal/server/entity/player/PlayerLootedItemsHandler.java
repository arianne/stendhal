package games.stendhal.server.entity.player;
/**
 * Handling for counting looted items of a player
 * 
 * @author madmetzger
 */
public class PlayerLootedItemsHandler {
	
	private static final String LOOTED_ITEMS = "looted_items";
	private final Player player;
	
	/**
	 * Create a new PlayerLootedItemsHandler for a player
	 * 
	 * @param player
	 */
	public PlayerLootedItemsHandler(Player player) {
		this.player = player;
	}
	
	/**
	 * Gets the how often this PlayerLootedItemsHandler's player has looted the given item
	 * @param item the item name
	 * @return the number of loots from corpses
	 */
	public int getNumberOfLootsForItem(String item) {
		if(!player.hasMap(LOOTED_ITEMS)) {
			return 0;
		}
		return player.getInt(LOOTED_ITEMS,item);
	}
	
	/**
	 * Increases the count of loots for the given item for this PlayerLootedItemsHandler's player
	 * @param item the item name
	 */
	public void incLootForItem(String item, int count) {
		if(!player.has(LOOTED_ITEMS,item)) {
			player.put(LOOTED_ITEMS, item, 0);
		}
		int current = player.getInt(LOOTED_ITEMS, item);
		int increased = current + count;
		player.put(LOOTED_ITEMS, item, increased);
	}

}
