/***************************************************************************
 *                   (C) Copyright 2019 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc;

import games.stendhal.server.entity.CollisionAction;

public class PassiveNPC extends NPC {
	public PassiveNPC() {
		put("title_type", "npc");

		baseSpeed = 0.2;
		createPath();
		setSize(1, 1);

		// NOTE: sub-classes must call updateModifiedAttributes()
	}

	/**
	 * Create path for the NPC. Sub classes can implement this method.
	 */
	protected void createPath() {
	}

	@Override
	protected void handleObjectCollision() {
		CollisionAction action = getCollisionAction();

		if (usesRandomPath()) {
			setRandomPathFrom(getX(), getY(), getMovementRange() / 2);
		} else if (action == CollisionAction.REVERSE) {
			reversePath();
		} else if (action == CollisionAction.REROUTE) {
			reroute();
		} else {
			stop();
		}
	}

	@Override
	protected void handleSimpleCollision(final int nx, final int ny) {
		CollisionAction action = getCollisionAction();
		if (!ignoresCollision()) {
			if (usesRandomPath()) {
				setRandomPathFrom(getX(), getY(), getMovementRange() / 2);
			} else if (action == CollisionAction.REROUTE) {
				reroute();
			} else {
				stop();
			}
		}
	}
}
