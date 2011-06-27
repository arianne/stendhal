package games.stendhal.server.entity.npc.behaviour.impl;

import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.behaviour.impl.prices.PriceCalculationStrategy;
import games.stendhal.server.entity.player.Player;

import java.util.HashSet;
import java.util.List;

/**
 * Behaviour for NPCs repairing items
 * 
 * @author madmetzger
 */
public class RepairerBehaviour extends TransactionBehaviour {
	
	private final PriceCalculationStrategy priceCalculator;
	
	/**
	 * Create a new RepairerBehaviour with a given price calculation strategy
	 * 
	 * @param calculator the price calculator
	 */
	public RepairerBehaviour(PriceCalculationStrategy calculator) {
		super(new HashSet<String>());
		priceCalculator = calculator;
	}

	@Override
	public boolean transactAgreedDeal(ItemParserResult res, EventRaiser seller,
			Player player) {
		List<Item> equipped = player.getAllEquipped(res.getChosenItemName());
		if(!equipped.isEmpty()) {
			if(equipped.size() == 1) {
				Item item = equipped.iterator().next();
				if(item.getDeterioration() > 0) {
					int price = priceCalculator.calculatePrice(item, player);
					if (player.isEquipped("money", price)) {
						return doRepair(item);
					}
				}
			} else {
				//TODO think about reasonable behaviour here? repair twice and account twice? check how many items are to repair in there?
				return false;
			}
		}
		return false;
	}

	private boolean doRepair(Item item) {
		//TODO implement dynamic price calculation via strategy pattern and drop money here
		item.repair();
		return true;
	}

}
