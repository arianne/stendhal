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


	public void attemptToInfclict(final RPEntity target, final RPEntity attacker) {

		// Roll dice between 1-100
		int roll = Rand.randUniform(1, 100);
		if (roll <= probability) {
			target.getStatusList().inflictStatus(status, attacker);
		}
	}

}
