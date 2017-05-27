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

	@Override
	public int calculatePrice(Item i, Player p) {
		return calculatePrice(i.getName(), p);
	}

	@Override
	public int calculatePrice(String item, Player p) {
		if(this.prices.containsKey(item)) {
			return this.prices.get(item);
		}
		return -1;
	}

	@Override
	public Set<String> dealtItems() {
		return this.prices.keySet();
	}

	@Override
	public boolean hasItem(String item) {
		return prices.containsKey(item);
	}

	@Override
	public void addCoveredItem(String item, int price) {
		this.prices.put(item, price);
	}

}
