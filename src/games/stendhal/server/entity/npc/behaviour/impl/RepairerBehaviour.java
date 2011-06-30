package games.stendhal.server.entity.npc.behaviour.impl;

import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.action.SayTextWithPlayerNameAction;
import games.stendhal.server.entity.npc.behaviour.impl.prices.PriceCalculationStrategy;
import games.stendhal.server.entity.player.Player;

import java.util.List;
import java.util.Set;

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
	 * @param items the items that can be repaired
	 */
	public RepairerBehaviour(PriceCalculationStrategy calculator, Set<String> items) {
		super(items);
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
						item.repair();
						player.drop("money", price);
						return true;
					} else {
						seller.say("You cannot afford to repair your "+res.getChosenItemName());
						return false;
					}
				} else {
					seller.say("Your "+res.getChosenItemName()+" is not damaged.");
					return false;
				}
			} else {
				Item toRepair = equipped.iterator().next();
				for(Item i : equipped) {
					if(i.getDeterioration() > toRepair.getDeterioration()) {
						toRepair = i;
					}
				}
				if(toRepair.getDeterioration() > 0) {
					int price = priceCalculator.calculatePrice(toRepair, player);
					if (player.isEquipped("money", price)) {
						toRepair.repair();
						player.drop("money", price);
						seller.say("You do carry more than one "+res.getChosenItemName()+" with you. So I repaired the most damaged one.");
						return true;
					}
					seller.say("You cannot afford to repair your "+res.getChosenItemName());
					return false;
				} else {
					seller.say("Your "+res.getChosenItemName()+" is not damaged.");
					return false;
				}
			}
		}
		seller.say("You do not carry a "+res.getChosenItemName()+" with you.");
		return false;
	}
	
	@Override
	public ChatCondition getTransactionCondition() {
		return new ChatCondition() {
			public boolean fire(Player player, Sentence sentence, Entity npc) {
				ItemParserResult res = parse(sentence);
				return canDealWith(res.getChosenItemName());
			}
		};
	}

	@Override
	public ChatAction getRejectedTransactionAction() {
		return new SayTextWithPlayerNameAction("I am sorry, [name], but I cannot repair your item.");
	}

	/**
	 * Calculate the price for the given item
	 * @param item the item to repair
	 * @param player the player wanting to repair
	 * @return the price for the player
	 */
	public int getPrice(String item, Player player) {
		return priceCalculator.calculatePrice(item, player);
	}

	/**
	 * Check if this NPC can repair this item
	 * 
	 * @param chosen the item to repair
	 * @return true iff this NPC is able to repair the item
	 */
	public boolean canDealWith(String chosen) {
		if(!getItemNames().isEmpty()) {
			return getItemNames().contains(chosen);
		}
		return true;
	}

}
