/***************************************************************************
 *                   (C) Copyright 2016-2016 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.puzzle;

import groovy.lang.GroovyObjectSupport;

/**
 * an adapter to read property values from PuzzleBuildingBlocks
 *
 * @author hendrik
 */
public class GroovyPuzzleMapAdapter extends GroovyObjectSupport {

	private String lastZone;
	private String lastName;

	@Override
	public Object getProperty(String param) {
		if (lastName == null) {
			lastName = param;
			return this;
		}
		return PuzzleEventDispatcher.get().getValue(lastZone, lastName, param);
	}

	public void setLastZone(String lastZone) {
		this.lastZone = lastZone;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


}
