/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package org.stendhalgame.client;


/**
 * Logger verbosity levels.
 */
public enum LogLevel {

	// available verbosity levels
	INFO("INFO"),
	WARN("WARN"),
	ERROR("ERROR"),
	DEBUG("DEBUG");

	/** String identifier. */
	public final String label;


	/**
	 * Creates a new logging verbosity level.
	 *
	 * @param label
	 *   String identifier.
	 */
	private LogLevel(final String label) {
		this.label = label;
	}
}
