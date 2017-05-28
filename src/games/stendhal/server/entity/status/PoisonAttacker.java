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

import org.apache.log4j.Logger;

import games.stendhal.common.NotificationType;
import games.stendhal.common.Rand;
import games.stendhal.common.constants.Testing;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.player.Player;

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

		double myProbability = getProbability();

		// Create a temporary instance to adjust without affecting entity's
		// built-in probability.
		Double actualProbability = myProbability;

		String resistAttribute = "resist_poisoned";
		if (target.has(resistAttribute)) {
			Double probabilityAdjust = 1.0 - target.getDouble(resistAttribute);

			if (logger.isDebugEnabled()) {
				logger.info("Adjusting POISONED status infliction resistance: "
						+ Double.toString(myProbability) + " * "
						+ Double.toString(probabilityAdjust) + " = "
						+ Double.toString(myProbability * probabilityAdjust));
			}

			actualProbability = myProbability * probabilityAdjust;
		}

		// DEBUG
		if (logger.isDebugEnabled() || Testing.DEBUG) {
			if (target.has(resistAttribute)) {
				double resistValue = target.getDouble(resistAttribute);
				String debugString1 = attacker.getName() + " "
						+ "poison probability: "
						+ Double.toString(myProbability);
				String debugString2 = target.getName() + "poison resistance: "
						+ Double.toString(resistValue);
				String debugString3 = "New probability: "
						+ Double.toString(actualProbability)
						+ " (" + Double.toString(myProbability) + " * (1.0 - "
						+ Double.toString(resistValue) + "))";
				logger.info(debugString1);
				logger.info(debugString2);
				logger.info(debugString3);
				if (target instanceof Player) {
					Player player = (Player)target;
					player.sendPrivateText(NotificationType.SERVER,
							debugString1 + "\n" + debugString2 + "\n"
							+ debugString3);
				}
			}
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
