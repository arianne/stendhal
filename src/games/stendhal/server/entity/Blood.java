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
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;

import java.awt.geom.Rectangle2D;

import marauroa.common.game.RPClass;
import marauroa.common.game.Definition.Type;

/**
 * Represents a blood puddle that is left on the ground after an entity
 * was injured or killed.
 */
public class Blood extends PassiveEntity implements TurnListener {
	/**
	 * Blood will disappear after so many seconds.
	 */
	public static final int DEGRADATION_TIMEOUT = 30 * 60; // 30 minutes

	public static void generateRPClass() {
		RPClass blood = new RPClass("blood");
		blood.isA("entity");
		blood.addAttribute("class", Type.STRING);
		blood.addAttribute("amount", Type.BYTE);
	}

	public Blood(RPEntity entity) {
		put("type", "blood");
		put("class", "red");
		put("amount", Rand.rand(4));

		TurnNotifier.get().notifyInSeconds(DEGRADATION_TIMEOUT, this, null);

		Rectangle2D rect = entity.getArea(entity.getX(), entity.getY());

		set((int) rect.getX(), (int) rect.getY());
	}


	@Override
	public String describe() {
		return ("You see a pool of blood.");
	}

	public void onTurnReached(int currentTurn, String message) {
		StendhalRPWorld.get().remove(getID());
		StendhalRPRuleProcessor.get().removeBlood(this);
	}
}
