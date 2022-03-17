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
 * A base class for status effects
 *
 * @author AntumDeluge
 */
public abstract class Status implements Cloneable {

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
