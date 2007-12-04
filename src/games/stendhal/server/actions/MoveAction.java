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
package games.stendhal.server.actions;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TutorialNotifier;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;
import games.stendhal.server.pathfinder.Path;

import java.util.List;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

public class MoveAction implements ActionListener {

	public static void register() {
		MoveAction move = new MoveAction();
		CommandCentre.register("move", move);
		CommandCentre.register("moveto", move);
		CommandCentre.register("push", move);
	}

	public void onAction(Player player, RPAction action) {

		String type = action.get("type");

		if (type.equals("move")) {
			move(player, action);
		} else if (type.equals("moveto")) {
			moveTo(player, action);
		} else if (type.equals("push")) {
			push(player, action);
		}
	}

	static class StopPushAction implements TurnListener {
		RPEntity tostop;

		StopPushAction(RPEntity entity) {
			tostop = entity;
		}

		public void onTurnReached(int currentTurn) {
			tostop.stop();
			tostop.notifyWorldAboutChanges();
		}
	}

	private void push(Player player, RPAction action) {
		if (action.has("target")) {
			int targetObject = action.getInt("target");

			StendhalRPZone zone = player.getZone();
			RPObject.ID targetid = new RPObject.ID(targetObject, zone.getID());
			if (zone.has(targetid)) {
				RPObject object = zone.get(targetid);
				/*
				 * If object is a NPC we ignore the push action.
				 */
				if (object instanceof SpeakerNPC) {
					return;
				}

				if (object instanceof RPEntity) {
					RPEntity entity = (RPEntity) object;
					if (player.canPush(entity) && player.nextTo(entity)) {
						Direction dir = player.getDirectionToward(entity);

						int x = entity.getX() + dir.getdx();
						int y = entity.getY() + dir.getdy();

						if (!zone.collides(entity, x, y)) {
							entity.setPosition(x, y);
							entity.notifyWorldAboutChanges();
							player.onPush(entity);
						}
					}
				}
			}
		}
	}

	private void move(Player player, RPAction action) {

		if (action.has("dir")) {
			int dirval = action.getInt("dir");

			if (dirval < 0) {
				player.removeClientDirection(Direction.build(-dirval));
			} else {
				player.addClientDirection(Direction.build(dirval));
			}

			player.applyClientDirection(true);
		}

		TutorialNotifier.move(player);
		player.notifyWorldAboutChanges();

	}

	private void moveTo(Player player, RPAction action) {

		if (!player.getZone().isMoveToAllowed()) {
			player
					.sendPrivateText("Mouse movement is not possible here. Use you keyboard");
			return;
		}

		if (player.hasPath()) {
			player.clearPath();
		}

		if (action.has("x") && action.has("y")) {
			int x = action.getInt("x");
			int y = action.getInt("y");
			if (!player.has("teleclickmode")) {
				// Walk
				List<Node> path = Path.searchPath(player, x, y);
				player.setPath(new FixedPath(path, false));
			} else {
				// Teleport
				StendhalRPZone zone = player.getZone();
				player.teleport(zone, x, y, null, null);
			}
		}

		player.applyClientDirection(false);
		player.notifyWorldAboutChanges();

	}
}
