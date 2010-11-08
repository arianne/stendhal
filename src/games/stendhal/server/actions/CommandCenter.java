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
package games.stendhal.server.actions;

import games.stendhal.common.NotificationType;
import games.stendhal.common.constants.Actions;
import games.stendhal.server.actions.admin.AdministrationAction;
import games.stendhal.server.actions.admin.BanAction;
import games.stendhal.server.actions.attack.AttackAction;
import games.stendhal.server.actions.attack.StopAction;
import games.stendhal.server.actions.buddy.BuddyAction;
import games.stendhal.server.actions.chat.AwayAction;
import games.stendhal.server.actions.chat.ChatAction;
import games.stendhal.server.actions.chat.StoreMessageAction;
import games.stendhal.server.actions.equip.DisplaceAction;
import games.stendhal.server.actions.equip.DropAction;
import games.stendhal.server.actions.equip.EquipAction;
import games.stendhal.server.actions.guild.CreateGuildAction;
import games.stendhal.server.actions.move.FaceAction;
import games.stendhal.server.actions.move.MoveAction;
import games.stendhal.server.actions.move.MoveToAction;
import games.stendhal.server.actions.move.PushAction;
import games.stendhal.server.actions.pet.ForsakeAction;
import games.stendhal.server.actions.pet.NameAction;
import games.stendhal.server.actions.pet.OwnAction;

import games.stendhal.server.entity.player.Player;

import java.util.concurrent.ConcurrentHashMap;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

/**
 * Handles actions sent by the client. They are dispatched to the 
 * specialized action classes in which they are processed.
 */
public class CommandCenter {
	private static final UnknownAction UNKNOWN_ACTION = new UnknownAction();
	private static ConcurrentHashMap<String, ActionListener> actionsMap;
	private static Logger logger = Logger.getLogger(CommandCenter.class);

	private static ConcurrentHashMap<String, ActionListener> getActionsMap() {
		if (actionsMap == null) {
			actionsMap = new ConcurrentHashMap<String, ActionListener>();
			registerActions();

		}
		return actionsMap;
	}

	public static void register(final String action, final ActionListener actionClass) {
		final ActionListener command = getActionsMap().putIfAbsent(action, actionClass);

		//TODO mf - register slash commands as verbs in WordList
		//		WordList.getInstance().registerVerb(action);
		if (command != null) {
			logger.error("not registering " + command.getClass()
					+ ". it has the same handler: " + action + " as  "
					+ CommandCenter.getAction(action).getClass());
		}
	}

	public static void register(final String action, final ActionListener actionClass,
			final int requiredAdminLevel) {
		register(action, actionClass);
		AdministrationAction.registerCommandLevel(action, requiredAdminLevel);
	}

	private static void registerActions() {
		AdministrationAction.registerActions();
		AttackAction.register();
		AwayAction.register();
		BanAction.register();
		BuddyAction.register();
		ChatAction.register();
		CIDSubmitAction.register();
		CreateGuildAction.register();
		DisplaceAction.register();
		DropAction.register();
		EquipAction.register();
		FaceAction.register();
		ForsakeAction.register();
		KnockAction.register();
		ListProducersAction.register();
		LookAction.register();
		MoveAction.register();
		MoveToAction.register();
		NameAction.register();
		OutfitAction.register();
		OwnAction.register();
		PushAction.register();
		QuestListAction.register();
		SentenceAction.register();
		StoreMessageAction.register();
		StopAction.register();
		TradeAction.register();
		UseAction.register();
		WhereAction.register();
		WhoAction.register();
		register("info", new InfoAction());
		register("markscroll", new MarkScrollAction());
	}

	public static boolean execute(final RPObject caster, final RPAction action) {
		try {

			final Player player = (Player) caster;
			final ActionListener actionListener = getAction(action);
			final String type = action.get(Actions.TYPE);
			if (!AdministrationAction.isPlayerAllowedToExecuteAdminCommand(player, type, true)) {
				return false;
			}
			actionListener.onAction(player, action);

			return true;
		} catch (final Exception e) {
			logger.error("Cannot execute action " + action + " send by "
					+ caster, e);
			return false;
		}
	}

	private static ActionListener getAction(final RPAction action) {
		if (action == null) {
			return UNKNOWN_ACTION;
		} else {
			return getAction(action.get("type"));
		}
	}

	private static ActionListener getAction(final String type) {
		if (type == null) {
			return UNKNOWN_ACTION;
		}

		final ActionListener action = getActionsMap().get(type);
		if (action == null) {
			return UNKNOWN_ACTION;
		} else {
			return action;
		}
	}

	private static class UnknownAction implements ActionListener {
		private static Logger logger = Logger.getLogger(UnknownAction.class);

		public void onAction(final Player player, final RPAction action) {
			String type = "null";
			if (action != null) {
				type = action.get("type");
			}
			logger.warn(player + " tried to execute unknown action " + type);
			if (player != null) {
				player.sendPrivateText(NotificationType.ERROR,
						"Unknown command " + type + ". Please type /help to get a list.");
			}
		}
	}

}
