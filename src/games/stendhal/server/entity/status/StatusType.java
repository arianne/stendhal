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

/**
 * types of statuses
 *
 * @author hendrik
 */
public enum StatusType {

	/** cannot walk streight */
	CONFUSED(new ConfuseStatusHandler()),

	/** is consuming food */
	EATING(new EatStatusHandler()),

	/** is consuming poison */
	POISONED(new PoisonStatusHandler()),

	/** cannot move */
	SHOCKED(new ShockStatusHandler()),

	/** drunk and not able to speak clearly */
	DRUNK(new DrunkStatusHandler()),
	
	/** reduced movement speed */
	ZOMBIE(new ZombieStatusHandler());

	/** the status handler for this StatusType */
	private final StatusHandler<? extends Status> statusHandler;

	/**
	 * creates a StatusType
	 *
	 * @param statusHandler StatusHandler for this type
	 */
	private StatusType(StatusHandler<?> statusHandler) {
		this.statusHandler = statusHandler;
	}

	/**
	 * gets the name of the status type
	 *
	 * @return name
	 */
	public String getName() {
		return this.name().toLowerCase();
	}

	/**
	 * get status handler
	 *
	 * @return StatusHandler
	 */
	@SuppressWarnings("unchecked")
	public <T extends Status> StatusHandler<T> getStatusHandler() {
		return (StatusHandler<T>) statusHandler;
	}
}
