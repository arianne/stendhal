package games.stendhal.server.entity.npc.behaviour.impl.prices;

import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;

import java.util.Set;
/**
 * Special calculation strategy for calculating repair prices
 * 
 * Repairing price is based on value determined by the StendhalEconomy, player
 * level (min level has an effect similar to affecting def or rate) 
 * and player's PK status
 * 
 * @author madmetzger
 */
public class RepairingPriceCalculationStrategy implements PriceCalculationStrategy {
	
	private Set<String> items;
	
	/**
	 * Create a new strategy object for the given items
	 * 
	 * @param repairableItems
	 */
	public RepairingPriceCalculationStrategy(Set<String> repairableItems) {
		items = repairableItems;
	}

	public int calculatePrice(Item i, Player p) {
		return calculatePrice(i.getName(), p);
	}

	public int calculatePrice(String item, Player p) {
		return 1;
	}

	public Set<String> dealtItems() {
		return items;
	}

	public boolean hasItem(String item) {
		return items.contains(item);
	}

	public void addCoveredItem(String item, int price) {
		items.add(item);
	}

}
