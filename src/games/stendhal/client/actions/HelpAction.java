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
package games.stendhal.client.actions;

import static games.stendhal.common.constants.Actions.REMOVEDETAIL;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.common.NotificationType;

/**
 * Display command usage. Eventually replace this with ChatCommand.usage().
 */
class HelpAction implements SlashAction {

	/**
	 * Execute a chat command.
	 *
	 * @param params
	 *            The formal parameters.
	 * @param remainder
	 *            Line content after parameters.
	 *
	 * @return <code>true</code> if was handled.
	 */
	@Override
	public boolean execute(final String[] params, final String remainder) {
		final String[] lines = {
				"For a detailed reference, visit #https://stendhalgame.org/wiki/Stendhal_Manual",
				"Here are the most-used commands:",
				"* CHATTING:",
				"- /me <action> \tShow a message about what you are doing.",
				"- /tell <player> <message>",
				"\t\tSend a private message to #player.",
				"- /answer <message>",
				"\t\tSend a private message to the last player who sent a message to you.",
				"- // <message>\tSend a private message to the last player you sent a message to.",
				"- /storemessage <player> <message>",
				"\t\tStore a private message to deliver for an offline #player.",
				"- /who \tList all players currently online.",
				"- /where <player> \tShow the current location of #player.",
				"- /sentence <text> \tSet message on stendhalgame.org profile page and what players see when using #Look.",
				"* SUPPORT:",
				"- /support <message>",
				"\t\tAsk an administrator for help.",
				"- /faq \t\tOpen Stendhal FAQs wiki page in browser.",
				"* ITEM MANIPULATION:",
				"- /drop [quantity] <item>",
				"\t\tDrop a certain number of an #item.",
				"- /markscroll <text>",
				"\t\tMark your empty scroll and add a #text label.",
				"* BUDDIES AND ENEMIES:",
				"- /add <player> \tAdd #player to your buddy list.",
				"- /remove <player>",
				"\t\tRemove #player from your buddy list.",
				"- /ignore <player> [minutes|*|- [reason...]]",
				"\t\tAdd #player to your ignore list.",
				"- /ignore \tFind out who is on your ignore list.",
				"- /unignore <player>",
				"\t\tRemove #player from your ignore list.",
				"* STATUS:",
				"- /away <message>",
				"\t\tSet an away message.",
				"- /away \tRemove away status.",
				"- /grumpy <message>",
				"\t\tSet a message to ignore all non-buddies.",
				"- /grumpy \tRemove grumpy status.",
				"- /name <pet> <name>",
				"\t\tGive a name to your pet.",
				"- /profile [name] \tOpens a player profile page on stendhalgame.org.",
				"* PLAYER CONTROL:",
				"- /clickmode \tSwitches between single click mode and double click mode.",
				"- /walk \tToggles autowalk on/off.",
				"- /stopwalk \tTurns autowalk off.",
				"- /movecont \tToggle continuous movement (allows players to continue walking after map change or teleport without releasing direction key).",
				"* CLIENT SETTINGS:",
				"- /mute \tMute or unmute the sounds.",
				"- /volume \tLists or sets the volume for sound and music.",
				"* MISC:",
				"- /info \t\tFind out what the current server time is.",
				"- /clear \tClear chat log.",
				"- /help \tShow help information.",
				"- /" + REMOVEDETAIL + " \tRemove the detail layer (e.g. balloon, umbrella, etc.) from character."
		};

		for (final String line : lines) {
			ClientSingletonRepository.getUserInterface().addEventLine(new HeaderLessEventLine(line, NotificationType.CLIENT));
		}

		return true;
	}

	/**
	 * Get the maximum number of formal parameters.
	 *
	 * @return The parameter count.
	 */
	@Override
	public int getMaximumParameters() {
		return 0;
	}

	/**
	 * Get the minimum number of formal parameters.
	 *
	 * @return The parameter count.
	 */
	@Override
	public int getMinimumParameters() {
		return 0;
	}
}
