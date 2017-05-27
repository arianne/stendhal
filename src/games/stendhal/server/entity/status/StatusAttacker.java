/***************************************************************************
 *                   (C) Copyright 2013 - Faiumoni e. V.                   *
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
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;

/**
 * a status attacker
 *
 * @author hendrik
 */
public class StatusAttacker {
	/** The logger instance */
	private static final Logger logger = Logger.getLogger(StatusAttacker.class);

	private final double probability;
	private final Status status;

	/**
	 * a Status attacker
	 *
	 * @param status status to attack with
	 * @param probability probability of an attack in this turn
	 */
	public StatusAttacker(Status status, double probability) {
		this.probability = probability;
		this.status = status;
	}

	/**
	 * gets the probability
	 *
	 * @return probability
	 */
	public double getProbability() {
		return probability;
	}

	/**
	 * gets the status
	 *
	 * @return status
	 */
	protected Status getStatus() {
		return status;
	}

	/**
	 * an attempt to attack the target, it may succeed or not
	 *
	 * @param target   target   defender
	 * @param attacker attacker attacker
	 */
	@SuppressWarnings("unused")
	public void onAttackAttempt(RPEntity target, RPEntity attacker) {
		// stub
	}

	/**
	 * the target was hit, this may or may not have caused damage
	 *
	 * @param target   target   defender
	 * @param attacker attacker attacker
	 * @param damage   amount of damage
	 */
	public void onHit(RPEntity target, RPEntity attacker,
			@SuppressWarnings("unused") int damage) {
		Status inflictedStatus = (Status) status.clone();
		StatusType statusType = inflictedStatus.getStatusType();
		String resistAttribute = "resist_"
				+ statusType.toString().toLowerCase();

		// Create a temporary instance to adjust without affecting entity's
		// built-in probability.
		Double actualProbability = probability;

		if (target.has(resistAttribute)) {
			Double probabilityAdjust = 1.0 - target.getDouble(resistAttribute);

			if (logger.isDebugEnabled()) {
				logger.info("Adjusting " + statusType.toString()
						+ " status infliction resistance: "
						+ Double.toString(probability) + " * "
						+ Double.toString(probabilityAdjust) + " = "
						+ Double.toString(probability * probabilityAdjust));
			}

			actualProbability = probability * probabilityAdjust;
		}

		// DEBUG
		if (logger.isDebugEnabled() || Testing.DEBUG) {
			if (target.has(resistAttribute)) {
				double resistValue = target.getDouble(resistAttribute);
				String debugString1 = attacker.getName() + " "
						+ inflictedStatus.getName() + " probability: "
						+ Double.toString(probability);
				String debugString2 = target.getName() + statusType.getName()
						+ " resistance: "
						+ Double.toString(resistValue);
				String debugString3 = "New probability: "
						+ Double.toString(actualProbability)
						+ " (" + Double.toString(probability) + " * (1.0 - "
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

		// Roll dice between 1-100
		int roll = Rand.randUniform(1, 100);
		if (roll <= actualProbability) {
			target.getStatusList().inflictStatus(inflictedStatus, attacker);
		}
	}

	/**
	 *
	 * @return
	 *     Name of the status that this attacker can inflict
	 */
    public String getStatusName() {
        return status.getName();
    }

}
