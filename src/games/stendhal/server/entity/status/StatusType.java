/***************************************************************************
 *                   (C) Copyright 2014 - Faiumoni e. V.                   *
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

import java.util.Locale;

import org.apache.log4j.Logger;

/**
 * types of statuses
 *
 * @author hendrik
 */
public enum StatusType {

	/** cannot walk straight */
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
	ZOMBIE(new ZombieStatusHandler()),

	/** reduced movement speed */
	HEAVY(new HeavyStatusHandler());

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

	/**
	 * Retrieve StatusType for creating status resistant items
	 *
	 * @param status Name of the status. e.g. PoisonStatus
	 * @return StatusType
	 */
	public static StatusType parse(String status) {
		try {
			return StatusType.valueOf(status.toUpperCase(Locale.ENGLISH));
		} catch (RuntimeException e) {
			Logger.getLogger(StatusType.class).error("Unknown status type: " + status, e);
			return null;
		}
	}
}
