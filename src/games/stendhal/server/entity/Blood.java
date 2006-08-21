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

import games.stendhal.common.Rand;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;

import java.awt.geom.Rectangle2D;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPClass;

/**
 * Represents a blood puddle that is left on the ground after a Creature
 * was injured or killed.
 */
public class Blood extends Entity implements TurnListener {
	/**
	 * After this many turns, blood will disappear.
	 */
	public static final int DEGRADATION_TIMEOUT = 300; // 30 minutes at 300 ms

	public static void generateRPClass() {
		RPClass blood = new RPClass("blood");
		blood.isA("entity");
		blood.add("class", RPClass.BYTE);
	}

	public Blood(RPEntity entity) throws AttributeNotFoundException {
		super();
		put("type", "blood");
		put("class", Rand.rand(4));

		TurnNotifier.get().notifyInTurns(DEGRADATION_TIMEOUT, this, null);

		Rectangle2D rect = entity.getArea(entity.getx(), entity.gety());

		set((int) rect.getX(), (int) rect.getY());
	}

	@Override
	public void getArea(Rectangle2D rect, double x, double y) {
		rect.setRect(x, y, 1, 1);
	}

	@Override
	public boolean isObstacle() {
		// can walk over blood
		return false;
	}

	@Override
	public String describe() {
		return ("You see a blood pool.");
	}

	public void onTurnReached(int currentTurn, String message) {
		world.remove(getID());
		rp.removeBlood(this);
	}
}
