/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.buddies;

/**
 * Represents a buddy and her online status.
 */
class Buddy implements Comparable<Buddy> {
	private final String name;
	private boolean online;

	/**
	 * Create a new buddy.
	 *
	 * @param name name of the buddy
	 */
	public Buddy(String name) {
		this.name = name;
	}

	/**
	 * Get the name of the buddy.
	 *
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Check if the buddy is online.
	 *
	 * @return <code>true</code> if the buddy is online, <code>false</code>
	 * 	otherwise
	 */
	public boolean isOnline() {
		return online;
	}

	/**
	 * Set the online status of the buddy.
	 *
	 * @param status the new online status
	 * @return <code>true</code>, if the status changed from previous,
	 * 	<code>false</code> otherwise
	 */
	public boolean setOnline(boolean status) {
		boolean changed = status != online;
		online = status;
		return changed;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Buddy) {
			return name.equals(((Buddy) obj).name);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public int compareTo(Buddy buddy) {
		if (online != buddy.online) {
			return (online) ? -1 : 1;
		}

		return name.compareToIgnoreCase(buddy.name);
	}
}
