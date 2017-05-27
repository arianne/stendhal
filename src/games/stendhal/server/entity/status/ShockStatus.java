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


/**
 * A status effect that causes the entity to stop moving after a set amount of
 * steps
 *
 * @author Jordan
 *
 */
public class ShockStatus extends Status {

	/** Entity is "shocked" after taking 5 steps */
	private final int STEPS_DELAY = 5;

	/**
	 * Create the status
	 */
	public ShockStatus() {
		super("shock");
	}

	/**
	 * returns the status type
	 *
	 * @return StatusType
	 */
	@Override
	public StatusType getStatusType() {
		return StatusType.SHOCKED;
	}

	/**
	 * gets the steps delay
	 *
	 * @return steps delay
	 */
	public int getStepsDelay() {
		return STEPS_DELAY;
	}
}
