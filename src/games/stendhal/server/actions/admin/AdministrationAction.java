/***************************************************************************
 *                   (C) Copyright 2003-2016 - Marauroa                    *
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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.constants.Actions;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.EntityHelper;
import marauroa.common.game.RPAction;

/**
 * Most /commands for admins are handled here.
 */
public abstract class AdministrationAction implements ActionListener {

	public static final int REQUIRED_ADMIN_LEVEL_FOR_SUPPORT = 100;
	public static final int REQUIRED_ADMIN_LEVEL_FOR_SUPER = 5000;

	protected static final Logger logger = Logger.getLogger(AdministrationAction.class);

	private static final Map<String, Integer> REQUIRED_ADMIN_LEVELS = new HashMap<String, Integer>();

	public static void registerActions() {
		AdminLevelAction.register();
		AdminNoteAction.register();
		AlterAction.register();
		AlterCreatureAction.register();
		AlterKillAction.register();
		AlterQuestAction.register();
		CIDListAction.register();
		DestroyAction.register();
		GagAction.register();
		GhostModeAction.register();
		InspectAction.register();
		InspectKillAction.register();
		InspectQuestAction.register();
		InvisibleAction.register();
		JailAction.register();
		JailReportAction.register();
		RemoteViewAction.register();
		SummonAction.register();
		SummonAtAction.register();
		SupportAnswerAction.register();
		TeleClickModeAction.register();
		TeleportAction.register();
		TeleportToAction.register();
		TellAllAction.register();
		WrapAction.register();
		StoreMessageOnBehalfOfPlayerAction.register();
		REQUIRED_ADMIN_LEVELS.put("super", 5000);
	}

	public static void registerCommandLevel(final String command, final int minLevel) {
		REQUIRED_ADMIN_LEVELS.put(command, minLevel);
	}

	public static Integer getLevelForCommand(final String command) {
		final Integer val = REQUIRED_ADMIN_LEVELS.get(command);
		if (val == null) {
			return -1;
		}

		return val;
	}

	public static boolean isPlayerAllowedToExecuteAdminCommand(final Player player,
			final String command, final boolean verbose) {
		// get adminlevel of player and required adminlevel for this command
		final int adminlevel = player.getAdminLevel();
		final Integer required = REQUIRED_ADMIN_LEVELS.get(command);

		// check that we know this command
		if (required == null) {
			return true;
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

	@Override
	public final void onAction(final Player player, final RPAction action) {
		perform(player, action);
	}

	protected abstract void perform(Player player, RPAction action);

	protected final Entity getTargetAnyZone(final Player player, final RPAction action) {
		Entity entity;
		if (action.has(Actions.TARGET_PATH)) {
			entity = EntityHelper.getEntityFromPath(player, action.getList(Actions.TARGET_PATH));
		} else {
			entity = EntityHelper.entityFromSlot(player, action);
		}

		if (entity == null) {
			entity = EntityHelper.entityFromTargetNameAnyZone(action.get(Actions.TARGET), player);
		}

		return entity;
	}

	/**
	 * get the Entity-object of the specified target.
	 *
	 * @param player
	 * @param action
	 * @return the Entity or null if it does not exist
	 */
	protected final Entity getTarget(final Player player, final RPAction action) {
		Entity entity = EntityHelper.entityFromSlot(player, action);

		if (entity == null) {
			entity = EntityHelper.entityFromTargetName(action.get(Actions.TARGET), player);
		}

		return entity;
	}
}
