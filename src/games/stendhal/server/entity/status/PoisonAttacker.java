/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.status;

import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.Rand;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.item.Item;

/**
 * a status attacker for poison
 */
public class PoisonAttacker extends StatusAttacker {
	/** The logger instance */
	private static final Logger logger = Logger.getLogger(PoisonAttacker.class);

	/**
	 * PoisonAttacker
	 *
	 * @param probability probability
	 * @param poison poison item
	 */
	public PoisonAttacker(final int probability, final ConsumableItem poison) {
		super(new PoisonStatus(poison.getAmount(), poison.getFrecuency(), poison.getRegen()), probability);
	}

	@Override
	public void onAttackAttempt(RPEntity target, RPEntity attacker) {

		// FIXME: antipoison is replaced with resist_<status>
		// Antipoison attributes
		double sumAll = 0.0;
		List<Item> defenderEquipment = target.getDefenseItems();
		if (target.hasRing()) {
			defenderEquipment.add(target.getRing());
		}

		for (final Item equipmentItem : defenderEquipment) {
			if (equipmentItem.has("antipoison")) {
				sumAll += equipmentItem.getDouble("antipoison");
			}
		}

		// Prevent antipoison attribute from surpassing 100%
		if (sumAll > 1) {
			sumAll = 1;
		}

		double myProbability = getProbability();
		if (sumAll > 0) {
			// invert the value for multiplying
			double myAntipoison = (1 - sumAll);
			myProbability *= myAntipoison;
		}

		// Create a temporary instance to adjust without affecting entity's
		// built-in probability.
		Double actualProbability = myProbability;
		
		String resistAttribute = "resist_poisoned";
		if (target.has(resistAttribute)) {
			Double probabilityAdjust = 1.0 - target.getDouble(resistAttribute);
			
			if (logger.isDebugEnabled()) {
				logger.debug("Adjusting POISONED status infliction resistance: "
						+ Double.toString(myProbability) + " * "
						+ Double.toString(probabilityAdjust) + " = "
						+ Double.toString(myProbability * probabilityAdjust));
			}
			
			actualProbability = myProbability * probabilityAdjust;
		}
		
		final int roll = Rand.roll1D100();
		if (roll <= actualProbability) {
			if (target.getStatusList().inflictStatus((Status) getStatus().clone(), attacker)) {
				new GameEvent(attacker.getName(), "poison", target.getName()).raise();
				target.sendPrivateText("You have been poisoned by " + Grammar.a_noun(attacker.getName()) + ".");
			}
		}
	}

	@Override
	public void onHit(RPEntity target, RPEntity attacker, int damage) {
		// do nothing, especially do not process the logic of the super class
	}

}
