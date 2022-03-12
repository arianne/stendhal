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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import games.stendhal.common.constants.Actions;
import games.stendhal.common.parser.SimilarExprMatcher;
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
import games.stendhal.server.actions.equip.ReorderAction;
import games.stendhal.server.actions.move.AutoWalkAction;
import games.stendhal.server.actions.move.ConditionalStopAction;
import games.stendhal.server.actions.move.FaceAction;
import games.stendhal.server.actions.move.MoveAction;
import games.stendhal.server.actions.move.MoveContinuousAction;
import games.stendhal.server.actions.move.MoveToAction;
import games.stendhal.server.actions.move.PushAction;
import games.stendhal.server.actions.pet.ForsakeAction;
import games.stendhal.server.actions.pet.NameAction;
import games.stendhal.server.actions.pet.OwnAction;
import games.stendhal.server.actions.query.InfoAction;
import games.stendhal.server.actions.query.LanguageAction;
import games.stendhal.server.actions.query.ListProducersAction;
import games.stendhal.server.actions.query.LookAction;
import games.stendhal.server.actions.query.ProgressStatusQueryAction;
import games.stendhal.server.actions.query.QuestListAction;
import games.stendhal.server.actions.query.WhereAction;
import games.stendhal.server.actions.query.WhoAction;
import games.stendhal.server.actions.spell.CastSpellAction;
import games.stendhal.server.core.engine.Translate;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

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
		final ActionListener command = getActionsMap().put(action, actionClass);

		//TODO mf - register slash commands as verbs in WordList
		//		WordList.getInstance().registerVerb(action);
		if (command != null) {
			logger.error("ActionListener " + command.getClass()
					+ " for action " + action + " was replaced with  "
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
		CastSpellAction.register();
		ChallengePlayerAction.register();
		ChatAction.register();
		ConditionalStopAction.register();
		CStatusAction.register();
		DisplaceAction.register();
		DropAction.register();
		EquipAction.register();
		FaceAction.register();
		ForsakeAction.register();
		GroupManagementAction.register();
		KnockAction.register();
		LanguageAction.register();
		ListProducersAction.register();
		LookAction.register();
		MoveAction.register();
		MoveContinuousAction.register();
		MoveToAction.register();
		NameAction.register();
		OutfitAction.register();
		OwnAction.register();
		ProgressStatusQueryAction.register();
		PushAction.register();
		QuestListAction.register();
		RemoveDetailAction.register();
		ReorderAction.register();
		SentenceAction.register();
		StoreMessageAction.register();
		StopAction.register();
		TradeAction.register();
		UseAction.register();
		AutoWalkAction.register();
		WhereAction.register();
		WhoAction.register();
		register("info", new InfoAction());
		register("markscroll", new MarkScrollAction());
	}

	/**
	 * executes an action from the client
	 *
	 * @param caster player requesting the action
	 * @param action action details
	 * @return true, if it was processed
	 */
	public static boolean execute(final RPObject caster, final RPAction action) {
		try {
			if (caster instanceof Player) {
				final Player player = (Player) caster;
				Translate.setThreadLanguage(player.getLanguage());
				final ActionListener actionListener = getAction(action);
				final String type = action.get(Actions.TYPE);
				if (!AdministrationAction.isPlayerAllowedToExecuteAdminCommand(player, type, true)) {
					return false;
				}
				actionListener.onAction(player, action);
				Translate.setThreadLanguage(null);
				return true;
			} else {
				logger.error("caster is no Player; cannot execute action " + action +
						" send by " + caster, new Throwable());
				return false;
			}
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
			String type = action.getRPClass().getName();
			if (type.equals("")) {
				type = action.get("type");
			}
			return getAction(type);
		}
	}

	private static ActionListener getAction(final String type) {
		if (type == null) {
			return UNKNOWN_ACTION;
		}

		ActionListener action = getActionsMap().get(type);
		if (action == null) {
			// Look up for close matches that can be suggested to the user.
			List<String> suggestions = new ArrayList<String>();
			for (String s : getActionsMap().keySet()) {
				if (SimilarExprMatcher.isSimilar(type, s, 0.1)) {
					suggestions.add(s);
				}
			}
			if (suggestions.size() != 0) {
				return new UnknownAction(suggestions);
			}

			return UNKNOWN_ACTION;
		} else {
			return action;
		}
	}
}
