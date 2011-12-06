/***************************************************************************
 *                   (C) Copyright 2011 - Faiumoni e. V.                   *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.bot.postman;

import java.util.Locale;

/**
 * creates a command handler based on a private message to postman
 *
 * @author hendrik
 */
public class CommandFactory {

	/**
	 * creates a command.
	 *
	 * @param message message sent to postman
	 * @param sender  sender of the private message
	 * @param postmanIRC instance of the postman IRC client
	 * @return EventHandler or <code>null</code>
	 */
	public static EventHandler create(String message, String sender, PostmanIRC postmanIRC) {
		String command = message.toLowerCase(Locale.ENGLISH);
		if (command.startsWith("adminnote")) {
			return new AdminNote(message, sender, postmanIRC);
		} else if (command.startsWith("ban")) {
			return new Ban(message, sender, postmanIRC);
		} else if (command.startsWith("ircban")) {
			return new IrcBanAuth(message, sender, postmanIRC);
		} else if (command.startsWith("npcshout")) {
			return new NPCShout(message, sender, postmanIRC);
		} else if (command.startsWith("supporta")) {
			return new SupportAnswer(message, sender, postmanIRC);
		} else if (command.startsWith("support")) {
			return new Support(message, sender, postmanIRC);
		} else if (command.startsWith("tellall")) {
			return new TellAll(message, sender, postmanIRC);
		}
		return null;
	}
}
