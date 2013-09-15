/***************************************************************************
 *                      (C) Copyright 2013 - Stendhal                      *
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
 * A base class for status effects
 * 
 * @author AntumDeluge
 */
public abstract class Status {

	/** The name of the status effect */
	private String name;

	/**
	 * Status
	 *
	 * @param name name of status
	 */
	public Status(final String name) {
		this.name = name;
	}

	// TODO: move to StatusAttacker
	public void attemptToInfclict(final RPEntity target, final double probability, final RPEntity attacker) {

		// Roll dice between 1-100
		int roll = Rand.randUniform(1, 100);
		if (roll <= probability) {
			target.getStatusList().inflictStatus(this, attacker);
		}
	}

	/**
	 * @return The status's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * closes this PoisonStatus
	 */
	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * returns the status type
	 *
	 * @return StatusType
	 */
	public abstract StatusType getStatusType();
}
