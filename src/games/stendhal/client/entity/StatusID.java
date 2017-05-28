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
package games.stendhal.client.entity;


/** Status IDs. */
public enum StatusID {
	// Jobs
	HEALER("job_healer"),
	MERCHANT("job_merchant"),

	// Statuses
	CONFUSE("status_confuse"),
	POISON("poisoned"),
	SHOCK("status_shock"),
	ZOMBIE("status_zombie"),
	HEAVY("status_heavy");

	/** Attribute corresponding to the status. */
	private final String attribute;

	/**
	 * Create a StatusID.
	 *
	 * @param attribute attribute corresponding to the status.
	 */
	private StatusID(String attribute) {
		this.attribute = attribute;
	}

	/**
	 * Get the attribute corresponding to the status.
	 *
	 * @return attribute name
	 */
	public String getAttribute() {
		return attribute;
	}

	/**
	 * Find the status ID using the status name.
	 *
	 * @param status
	 *      Name of status
	 * @return Status ID, or <code>null</code> if no status ID matches the name
	 */
	public static StatusID getStatusID(String status) {
		for (StatusID id : values()) {
			if (id.attribute.equals(status)) {
				return id;
			}
		}
		return null;
	}
}
