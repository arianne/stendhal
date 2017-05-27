package games.stendhal.server.entity.npc.behaviour.impl.prices;

import java.util.Set;

import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
/**
 * Price calculation based on a given item and a player
 *
 * @author madmetzger
 */
public interface PriceCalculationStrategy {

	/**
	 * Calculate a price
	 * @param i the item to consider
	 * @param p the player to consider for the calculation
	 * @return the calculated price
	 */
	public int calculatePrice(Item i, Player p);

	/**
	 * calculate a price based on the item's name
	 * @param item the item name
	 * @param p the player to consider for the calculation
	 * @return the calculated price
	 */
	public int calculatePrice(String item, Player p);

	/**
	 * Get all item names that are covered by this calculator
	 * @return a set of item names
	 */
	public Set<String> dealtItems();

	/**
	 * Check if the given item is covered by this calculator
	 * @param item the item name
	 * @return true iff the item name is covered
	 */
	public boolean hasItem(final String item);

	/**
	 * Add a item to the price list
	 * @param item the item name
	 * @param price the item's price
	 */
	public void addCoveredItem(String item, int price);

}
