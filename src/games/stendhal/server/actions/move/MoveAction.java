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
package games.stendhal.server.actions.move;

import games.stendhal.common.Direction;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.actions.WellKnownActionConstants;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TutorialNotifier;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.core.pathfinder.Path;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.EntityHelper;

import java.util.List;

import marauroa.common.game.RPAction;
import static games.stendhal.server.actions.WellKnownActionConstants.TARGET;
import static games.stendhal.server.actions.WellKnownActionConstants.TYPE;

public class MoveAction implements ActionListener {

	private static final String _TELECLICKMODE = "teleclickmode";
	private static final String _DIR = "dir";

	private static final String _PUSH = "push";
	private static final String _MOVETO = "moveto";
	private static final String _MOVE = "move";

	public static void register() {
		MoveAction move = new MoveAction();
		CommandCenter.register(_MOVE, move);
		CommandCenter.register(_MOVETO, move);
		CommandCenter.register(_PUSH, move);
	}

	public void onAction(Player player, RPAction action) {

		String type = action.get(TYPE);

		if (type.equals(_MOVE)) {
			move(player, action);
		} else if (type.equals(_MOVETO)) {
			moveTo(player, action);
		} else if (type.equals(_PUSH)) {
			push(player, action);
		}
	}

	static class StopPushAction implements TurnListener {
		private RPEntity tostop;

		StopPushAction(RPEntity entity) {
			tostop = entity;
		}

		public void onTurnReached(int currentTurn) {
			tostop.stop();
			tostop.notifyWorldAboutChanges();
		}
	}

	private void push(Player player, RPAction action) {
		if (!action.has(TARGET)) {
			return;
		}

		// evaluate the target parameter
		StendhalRPZone zone = player.getZone();
		Entity entity = EntityHelper.entityFromTargetName(
			action.get(TARGET), player);

		if ((entity == null) || !(entity instanceof RPEntity)) {
			return;
		}
		
		RPEntity rpEntity = (RPEntity) entity;

		// If object is a NPC we ignore the push action.
		if (rpEntity instanceof SpeakerNPC) {
			return;
		}

		if (player.canPush(rpEntity) && player.nextTo(rpEntity)) {
			Direction dir = player.getDirectionToward(rpEntity);

			int x = rpEntity.getX() + dir.getdx();
			int y = rpEntity.getY() + dir.getdy();

			if (!zone.collides(rpEntity, x, y)) {
				StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
					"push", rpEntity.getName(), rpEntity.getZone().getName(), 
					rpEntity.getX() + " " + rpEntity.getY() + " --> " + x + " " + y);
				rpEntity.setPosition(x, y);
				rpEntity.notifyWorldAboutChanges();
				player.onPush(rpEntity);
			}
		}
	}

	private void move(Player player, RPAction action) {

		if (action.has(_DIR)) {
			int dirval = action.getInt(_DIR);

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
			player.sendPrivateText("Mouse movement is not possible here. Use your keyboard.");
			return;
		}

		if (player.hasPath()) {
			player.clearPath();
		}

		if (action.has(WellKnownActionConstants.X)
				&& action.has(WellKnownActionConstants.Y)) {
			int x = action.getInt(WellKnownActionConstants.X);
			int y = action.getInt(WellKnownActionConstants.Y);
			if (!player.has(_TELECLICKMODE)) {
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
