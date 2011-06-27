package games.stendhal.server.entity.npc.behaviour.impl.prices;

import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;

import java.util.Set;
/**
 * Special calculation strategy for calculating repair prices
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

	@Override
	public int calculatePrice(Item i, Player p) {
		return calculatePrice(i.getName(), p);
	}

	@Override
	public int calculatePrice(String item, Player p) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Set<String> dealtItems() {
		return items;
	}

	@Override
	public boolean hasItem(String item) {
		return items.contains(item);
	}

	@Override
	public void addCoveredItem(String item, int price) {
		items.add(item);
	}

}
