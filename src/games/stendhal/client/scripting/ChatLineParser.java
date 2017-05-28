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
package games.stendhal.client.scripting;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.common.NotificationType;
import marauroa.common.game.RPAction;

/**
 * Parses the input in the chat box and invokes the appropriate action.
 */
public abstract class ChatLineParser {

	/**
	 * parses a chat/command line and processes the result.
	 *
	 * @param input
	 *            string to handle
	 *
	 * @return <code>true</code> if command was valid enough to process,
	 *         <code>false</code> otherwise.
	 */
	public static boolean parseAndHandle(final String input) {
		// get line
		final String text = input.trim();

		if (text.length() == 0) {
			return false;
		}

		if (text.charAt(0) == '/') {
			final SlashActionCommand command = SlashActionParser.parse(text.substring(1));
			final String[] params = command.getParams();

			if (command.hasError()) {
				ClientSingletonRepository.getUserInterface().addEventLine(
					new HeaderLessEventLine(command.getErrorString(),
					NotificationType.ERROR));
				return false;
			}

			/*
			 * Execute
			 */
			if (command.getAction() != null) {
				return command.getAction().execute(params, command.getRemainder());
			} else {
				/*
				 * Server extension
				 */
				final RPAction extension = new RPAction();

				extension.put("type", command.getName());

				if ((params.length > 0) && (params[0] != null)) {
					extension.put("target", params[0]);
					extension.put("args", command.getRemainder());
				}

				ClientSingletonRepository.getClientFramework().send(extension);

				return true;
			}
		} else {
			// Chat command. The most frequent one.
			final RPAction chat = new RPAction("chat");
			chat.put("type", "chat");
			chat.put("text", text);

			ClientSingletonRepository.getClientFramework().send(chat);

			return true;
		}
	}

}
