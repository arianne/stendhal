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
package games.stendhal.server.entity.slot;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import marauroa.common.game.RPObject;

/**
 * a pseudo slot which represents a location on the ground
 *
 * @author hendrik
 */
public class GroundSlot extends EntitySlot {
	private StendhalRPZone zone;
	private int x;
	private int y;

	/**
	 * creates a new GroundSlot
	 *
	 * @param zone zone
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	public GroundSlot(StendhalRPZone zone, int x, int y) {
		this.zone = zone;
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean isReachableForTakingThingsOutOfBy(Entity entity) {
		// TODO Auto-generated method stub
		return super.isReachableForTakingThingsOutOfBy(entity);
	}

	@Override
	public boolean isReachableForThrowingThingsIntoBy(Entity entity) {
		// TODO Auto-generated method stub
		return super.isReachableForThrowingThingsIntoBy(entity);
	}


	@Override
	protected int add(RPObject object, boolean assignId) {
		// TODO Auto-generated method stub
		return -1;
	}

	
}
