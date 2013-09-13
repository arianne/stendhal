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
 * 
 */
public abstract class Status {

	/** The name of the status effect */
	private String name;

	/** Number of turns that status is active (less than 0 for indefinate) */
	private int statusTimeout = 0;
	protected int timeoutCounter;

	/**
	 * Number of times the status can be inflicted on the entity at a single
	 * time (-1 for indefinite)
	 */
	private int maxOccurrences = -1;

	public Status(final String name) {
		this.name = name;
		timeoutCounter = statusTimeout;
	}

	/**
	 * Call this super method to use a timeout for sub-classes
	 * 
	 * @param entity
	 *            Entity to be affected by the status
	 */
	public void affect(final RPEntity entity) {
		if (timeoutCounter > 0) {
			timeoutCounter -= 1;
		}
	}

	public void attemptToInfclict(final RPEntity target,
			final double probability, final RPEntity attacker) {
		if (target.getStatusList().isResistantToStatus(this)) {
			// TODO: Affect probability with resistance
		}
		// Roll dice between 1-100
		int roll = Rand.randUniform(1, 100);
		if (roll <= probability) {
			target.getStatusList().inflictStatus(this, attacker);
		}
	}

	public int allowedOccurrences() {
		return maxOccurrences;
	}

	/**
	 * @return The status's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Condition to meet for status to be removed
	 * 
	 * @return Counter for timeout has reached 0
	 */
	public boolean removeConditionMet() {
		// Override this method to use different condition
		return timeoutCounter == 0;
	}

	public void setMaxOccurrences(final int max) {
		maxOccurrences = max;
	}

	/**
	 * Set the number of turns that the status should affect the entity
	 * 
	 * @param timeout
	 *            Turns before status is removed
	 */
	public void setTimeout(final int timeout) {
		statusTimeout = timeout;
		timeoutCounter = statusTimeout;
	}

	/**
	 * returns the status type
	 *
	 * @return StatusType
	 */
	public abstract StatusType getStatusType();
}
