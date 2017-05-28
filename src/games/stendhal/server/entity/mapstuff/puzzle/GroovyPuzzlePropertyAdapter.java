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

import games.stendhal.server.core.engine.StendhalRPWorld;
import groovy.lang.Script;

/**
 * provides property information to groovy scripts.
 *
 * @author hendrik
 */
public abstract class GroovyPuzzlePropertyAdapter extends Script {
	private GroovyPuzzleMapAdapter mapAdapter = new GroovyPuzzleMapAdapter();

	/**
	 * gets a property
	 *
	 * @param name of property
	 * @return value of property
	 */
	@Override
	public Object getProperty(String name) {
		if (name.equals("buildingBlock")) {
			return super.getProperty(name);
		}
		boolean isZone = StendhalRPWorld.get().getZone(name) != null;
		if (isZone) {
			mapAdapter.setLastZone(name);
			mapAdapter.setLastName(null);
		} else {
			mapAdapter.setLastZone(((PuzzleBuildingBlock) super.getProperty("buildingBlock")).getZoneName());
			mapAdapter.setLastName(name);
		}

		return mapAdapter;
	}
}
