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
package games.stendhal.server.entity.slot;

import com.google.common.collect.ImmutableList;

import games.stendhal.common.Constants;

/**
 * slot types
 *
 * @author hendrik
 */
public enum Slots {

	/**
	 * slots which may be carried by an entity (e. g. a bag)
	 */
	CARRYING(ImmutableList.copyOf(Constants.CARRYING_SLOTS));

	private ImmutableList<String> names;

	/**
	 * constructor
	 *
	 * @param names list of slot names
	 */
	Slots(ImmutableList<String> names) {
		this.names = names;
	}

	/**
	 * gets the list of slot names
	 *
	 * @return slot names
	 */
	public ImmutableList<String> getNames() {
		return names;
	}
}
