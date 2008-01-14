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
package games.stendhal.server.actions.admin;

import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.WellKnownActionConstants;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

//TODO mf - enable compound words for all slash commands by processing them by ConversationParser

/**
 * Most /commands for admins are handled here.
 */
public abstract class AdministrationAction implements ActionListener {

	private static final String _TARGETID = "targetid";

	protected static final Logger logger = Logger.getLogger(AdministrationAction.class);

	public static final int REQUIRED_ADMIN_LEVEL_FOR_SUPPORT = 100;

	public static final int REQUIRED_ADMIN_LEVEL_FOR_SUPER = 5000;

	protected static final Map<String, Integer> REQUIRED_ADMIN_LEVELS = new HashMap<String, Integer>();

	public static void register() {
		InspectAction.register();
		DestroyAction.register();
		SupportAnswerAction.register();
		TellAllAction.register();
		TeleportAction.register();
		TeleportToAction.register();
		AdminLevelAction.register();
		AlterAction.register();
		AlterCreatureAction.register();
		SummonAction.register();
		SummonAtAction.register();
		InvisibleAction.register();
		GhostModeAction.register();
		TeleClickModeAction.register();
		JailAction.register();
		GagAction.register();
		AlterQuestAction.register();
		REQUIRED_ADMIN_LEVELS.put("support", 100);
		REQUIRED_ADMIN_LEVELS.put("super", 5000);
	}

	public static void registerCommandLevel(String command, int minLevel) {
		REQUIRED_ADMIN_LEVELS.put(command, minLevel);
	}

	public static Integer getLevelForCommand(String command) {
		Integer val = REQUIRED_ADMIN_LEVELS.get(command);
		if (val == null) {
			return -1;
		}

		return val;
	}

	public static boolean isPlayerAllowedToExecuteAdminCommand(Player player,
			String command, boolean verbose) {
		// get adminlevel of player and required adminlevel for this command
		int adminlevel = player.getAdminLevel();
		Integer required = REQUIRED_ADMIN_LEVELS.get(command);

		// check that we know this command
		if (required == null) {
			logger.error("Unknown command " + command);
			if (verbose) {
				player.sendPrivateText("Sorry, command \"" + command
						+ "\" is unknown.");
			}
			return false;
		}

		if (adminlevel < required.intValue()) {
			// not allowed
			logger.warn("Player " + player.getName() + " with admin level "
					+ adminlevel + " tried to run admin command " + command
					+ " which requires level " + required + ".");

			// Notify the player if verbose is set.
			if (verbose) {

				// is this player an admin at all?
				if (adminlevel == 0) {
					player.sendPrivateText("Sorry, you need to be an admin to run \""
							+ command + "\".");
				} else {
					player.sendPrivateText("Your admin level is only "
							+ adminlevel + ", but a level of " + required
							+ " is required to run \"" + command + "\".");
				}
			}
			return false;
		}

		// OK
		return true;
	}

	public final void onAction(Player player, RPAction action) {

		String type = action.get(WellKnownActionConstants.TYPE);
		logger.info(type);
		if (!isPlayerAllowedToExecuteAdminCommand(player, type, true)) {
			return;
		}

		perform(player, action);
	}

	protected abstract void perform(Player player, RPAction action);

	/**
	 * get the Entity-object of the specified target.
	 * 
	 * @param player
	 * @param action
	 * @return the Entity or null if it does not exist TODO merge with
	 *         EntityHelper.entityFromTargetName()
	 */
	protected final Entity getTarget(Player player, RPAction action) {

		String id = null;
		Entity target = null;

		// target contains a name unless it starts with #
		if (action.has(WellKnownActionConstants.TARGET)) {
			id = action.get(WellKnownActionConstants.TARGET);
		}
		if (id != null) {
			if (!id.startsWith("#")) {
				target = StendhalRPRuleProcessor.get().getPlayer(id);
				return target;
			} else {
				id = id.substring(1);
			}
		}

		// either target started with a # or it was not specified
		if (action.has(_TARGETID)) {
			id = action.get(_TARGETID);
		}

		// go for the id
		if (id != null) {
			StendhalRPZone zone = player.getZone();

			RPObject.ID oid = new RPObject.ID(Integer.parseInt(id),
					zone.getName());
			if (zone.has(oid)) {
				RPObject object = zone.get(oid);
				if (object instanceof Entity) {
					target = (Entity) object;
				}
			}
		}

		return target;
	}
}
