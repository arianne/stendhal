package games.stendhal.server.entity.npc.behaviour.impl;

import java.util.Set;

import org.apache.log4j.Logger;

import games.stendhal.common.grammar.ItemParserResult;

/**
 * Result of parsing an user request in Behaviour classes.
 */
public final class MerchantBehaviourResult extends ItemParserResult {

	private static Logger logger = Logger.getLogger(MerchantBehaviourResult.class);

	public MerchantBehaviourResult(boolean found, String chosenItemName, int amount, Set<String> mayBeItems) {
		super(found, chosenItemName, amount, mayBeItems);
	}

	/**
	 * Sets the amount that the player wants to buy from the NPC.
	 *
	 * @param amount
	 *            amount
	 */
	@Override
	public void setAmount(final int amount) {
		if (amount < 1) {
			this.amount = 1;
			logger.warn("Increasing very low amount of " + amount + " to 1.");
		}

		if (amount > 1000) {
			logger.warn("Decreasing very large amount of " + amount + " to 1.");
			this.amount = 1;
		}

		this.amount = amount;
	}

}
