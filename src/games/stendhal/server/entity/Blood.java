/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity;

import java.awt.geom.Rectangle2D;
import games.stendhal.common.Rand;

import marauroa.common.game.*;

/**
 * Represents a blood puddle that is left on the ground after a Creature
 * was injured or killed.
 */
public class Blood extends Entity {
	/**
	 * After this many turns, blood will disappear.
	 */
	final public static int DEGRADATION_TIMEOUT = 300; // 30 minutes at 300 ms

	/**
	 * A countdown which counts how many turns are left until the
	 * blood disappears.
	 */
	private int degradation;
    
    /**
     * Remember which turn we were called last to compute the degradation
     */
    private int lastTurn = 0;

	public static void generateRPClass() {
		RPClass blood = new RPClass("blood");
		blood.isA("entity");
		blood.add("class", RPClass.BYTE);
	}

	public Blood(RPEntity entity) throws AttributeNotFoundException {
		super();
		put("type", "blood");
		put("class", Rand.rand(4));

		degradation = DEGRADATION_TIMEOUT;

		Rectangle2D rect = entity.getArea(entity.getx(), entity.gety());

		set((int) rect.getX(), (int) rect.getY());
	}

	@Override
	public void getArea(Rectangle2D rect, double x, double y) {
		rect.setRect(x, y, 1, 1);
	}

	@Override
	public boolean isCollisionable() {
		// can walk over blood
		return false;
	}

	private int decDegradation(int aktTurn) {
        degradation -= aktTurn - lastTurn;
 		return degradation;
	}

	public void logic(int aktTurn) {
        if(lastTurn == 0) {
            lastTurn = aktTurn - 1;
        }
		if (decDegradation(aktTurn) <= 0) {
			world.remove(getID());
			rp.removeBlood(this);
		}
        lastTurn = aktTurn;
	}

	@Override
	public String describe() {
		return ("You see a blood pool.");
	}
}
