package games.stendhal.server.entity.npc.behaviour.impl.prices;

import java.util.Map;
import java.util.Set;

import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
/**
 * Calculates prices based on a map containing item name and the fixed price
 * 
 * @author madmetzger
 */
public class FixedPricePriceCalculationStrategy implements
		PriceCalculationStrategy {
	
	public FixedPricePriceCalculationStrategy(Map<String, Integer> prices) {
		this.prices = prices;
	}

	private final Map<String, Integer> prices;

	public int calculatePrice(Item i, Player p) {
		return calculatePrice(i.getName(), p);
	}

	public int calculatePrice(String item, Player p) {
		if(this.prices.containsKey(item)) {
			return this.prices.get(item);
		}
		return -1;
	}

	public Set<String> dealtItems() {
		return this.prices.keySet();
	}

	public boolean hasItem(String item) {
		return prices.containsKey(item);
	}

	public void addCoveredItem(String item, int price) {
		this.prices.put(item, price);
	}
	
}
