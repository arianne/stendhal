package games.stendhal.server.entity.npc.behaviour.impl.prices;

import java.util.Set;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
/**
 * Special calculation strategy for calculating repair prices
 *
 * Repairing price is based on value determined by the player
 * level (min level has an effect similar to affecting def or rate)
 * and player's PK status
 *
 * @author madmetzger
 */
public class RepairingPriceCalculationStrategy implements PriceCalculationStrategy {

	private static final double REPAIR_PRICE_FACTOR = 0.05d;

	private static final double PLAYER_KILLER_MALUS = 2d;

	private final Set<String> items;

	private Item itemToRepair;

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
		itemToRepair = i;
		return calculatePrice(itemToRepair.getName(), p);
	}

	@Override
	public int calculatePrice(String item, Player p) {
		double itemvalue = 0d;
		double adjustedFactor = adjustFactorBasedOnMinLevel(item);
		double repairPrice =  itemvalue * adjustedFactor;
		// consider bad boy flag for price calculation
		if (p.isBadBoy()) {
			repairPrice = repairPrice * PLAYER_KILLER_MALUS;
		}
		return Double.valueOf(repairPrice).intValue();
	}

	private double adjustFactorBasedOnMinLevel(String item) {
		if(itemToRepair == null) {
			itemToRepair = SingletonRepository.getEntityManager().getItem(item);
		}
		//reset again
		itemToRepair = null;
		//TODO adjust similar to min level adjustment
		return REPAIR_PRICE_FACTOR;
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
