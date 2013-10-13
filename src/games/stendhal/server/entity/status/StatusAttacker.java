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

import games.stendhal.common.Rand;
import games.stendhal.server.entity.RPEntity;

/**
 * a status attacker
 *
 * @author hendrik
 */
public class StatusAttacker {
	private final double probability;
	private final Status status;

	/**
	 * a Status attacker
	 *
	 * @param status status to attack with
	 * @param probability probability of an attack in this turn
	 */
	public StatusAttacker(Status status, double probability) {
		super();
		this.probability = probability;
		this.status = status;
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
	public void onHit(RPEntity target, RPEntity attacker, @SuppressWarnings("unused") int damage) {
		// Roll dice between 1-100
		int roll = Rand.randUniform(1, 100);
		if (roll <= probability) {
			target.getStatusList().inflictStatus(status, attacker);
		}
	}

}
