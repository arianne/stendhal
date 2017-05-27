/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
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

import static games.stendhal.common.constants.Actions.SUPPORTANSWER;
import static games.stendhal.common.constants.Actions.TARGET;
import static games.stendhal.common.constants.Actions.TEXT;

import java.util.HashMap;
import java.util.Map;

import games.stendhal.common.MathHelper;
import games.stendhal.common.NotificationType;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.messages.SupportMessageTemplatesFactory;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.dbcommand.StoreMessageCommand;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnListenerDecorator;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.player.Player;
import marauroa.common.Pair;
import marauroa.common.game.RPAction;
import marauroa.server.db.command.DBCommand;
import marauroa.server.db.command.DBCommandQueue;
import marauroa.server.db.command.ResultHandle;

/**
 * answers a support question
 */
public class SupportAnswerAction extends AdministrationAction implements TurnListener  {

	private static final Map<String, String> messageTemplates = new SupportMessageTemplatesFactory().getTemplates();

	/**
	 * Keeps track of the anonymised admin names and when they were last used
	 */
	private final Map<String, Pair<String, Long>> adminNames = new HashMap<String, Pair<String, Long>> ();

	/**
	 * Amount of time after which an admin name 'anonymiser' will be replaced
	 */
	private static final int DELAY = 3 * MathHelper.SECONDS_IN_ONE_HOUR;

	/**
	 * increments as new names are added to the admin list.
	 * not really bothered as it grows, and that we aren't re-using numbers,
	 * just easier than checking the list to see what the next number should be
	 *
	 */
	private int nameCounter = 0;

	/**
	 * the admin who sent the message (needed as a class variable for postman messages)
	 */
	private String sender;

	private final ResultHandle handle = new ResultHandle();

	public static void register() {
		CommandCenter.register(SUPPORTANSWER, new SupportAnswerAction(), 50);
	}

	@Override
	public void perform(final Player player, final RPAction action) {
		if (!action.has(TARGET) || !action.has(TEXT)) {
			return;
		}
		String reply = action.get(TEXT);

		if (!player.getChatBucket().checkAndAdd(reply.length())) {
			return;
		}
		sender = player.getName();
		if (action.has("sender") && (player.getName().equals("postman"))) {
			sender = action.get("sender");
		}

		// test for use of standard response shortcut, and replace the reply message if so
		// if you alter these please update client/actions/GMHelpAction (or put the string replies in a common file if you like)
		final Player supported = SingletonRepository.getRuleProcessor().getPlayer(action.get(TARGET));

		if (reply.startsWith("$")) {
			if (messageTemplates.containsKey(reply)) {
				reply = messageTemplates.get(reply);
				reply = String.format(reply, action.get(TARGET));
			} else {
				player.sendPrivateText(reply + " is not a recognised shortcut. Please check #/gmhelp #support for a list.");
				// send no support answer message if the shortcut wasn't understood
				return;
			}
		}

		final String message = sender + " answers " + Grammar.suffix_s(action.get(TARGET))
				+ " support question: " + reply;

		new GameEvent(sender, SUPPORTANSWER, action.get(TARGET), reply).raise();
		if (supported != null) {

			supported.sendPrivateText(NotificationType.SUPPORT, "Support (" + getAnonymisedAdminName(sender) + ") tells you: " + reply + " \nIf you wish to reply, use /support.");
			supported.notifyWorldAboutChanges();
			SingletonRepository.getRuleProcessor().sendMessageToSupporters(message);

		} else {
			// that player is not logged in. Do they exist at all or are they just offline? Try sending a message with postman.
			DBCommand command = new StoreMessageCommand(getAnonymisedAdminName(sender), action.get(TARGET), "In answer to your support question:\n" + reply + " \nIf you wish to reply, use /support.", "S");
			DBCommandQueue.get().enqueueAndAwaitResult(command, handle);
			TurnNotifier.get().notifyInTurns(0, new TurnListenerDecorator(this));
		}
	}

	/**
	 * Completes handling the supportanswer action.
	 *
	 * @param currentTurn ignored
	 */
	@Override
	public void onTurnReached(int currentTurn) {
		StoreMessageCommand checkcommand = DBCommandQueue.get().getOneResult(StoreMessageCommand.class, handle);

		if (checkcommand == null) {
			TurnNotifier.get().notifyInTurns(0, new TurnListenerDecorator(this));
			return;
		}

		boolean characterExists = checkcommand.targetCharacterExists();
		String target = checkcommand.getTarget();
		String supportmessage = checkcommand.getMessage();

		final Player admin = SingletonRepository.getRuleProcessor().getPlayer(sender);

		if(!characterExists) {
			if (admin != null) {
				// incase admin logged out while waiting we want to avoid NPE
				admin.sendPrivateText(NotificationType.ERROR, "Sorry, " + target + " could not be found.");
			}
			return;
		}

		final String message = sender + " answers " + Grammar.suffix_s(target)
				+ " support question using postman: " + supportmessage;

		SingletonRepository.getRuleProcessor().sendMessageToSupporters(message);
	}


	/**
	 * Gets anonymised admin name from map and updates timestamp,
	 * or sets new anonymised name if some time has passed since last use of supportanswer
	 *
	 * @param adminName adminstrator name
	 * @return anonymized name for the adminstarator
	 */
	private String getAnonymisedAdminName(String adminName) {
		String anonymisedAdminName;
		// is the name already listed?
		if(adminNames.containsKey(adminName)) {
			Long lastTime = adminNames.get(adminName).second();
			// time has passed, use a new name in place of the last anonymised name
			if ((System.currentTimeMillis() - lastTime) > (DELAY * 1000L)) {
				nameCounter++;
				anonymisedAdminName = "admin"+nameCounter;
			} else {
				// just get the name to use from the existing map without changing it
				anonymisedAdminName = adminNames.get(adminName).first();
			}
		} else {
			// name wasn't listed yet, set up the next name to use
			nameCounter++;
			anonymisedAdminName = "admin"+nameCounter;
		}
		// whether we changed the name to use or not, update the last used timestamp
		adminNames.put(adminName, new Pair<String,Long>(anonymisedAdminName, System.currentTimeMillis()));
		return anonymisedAdminName;
	}


}
