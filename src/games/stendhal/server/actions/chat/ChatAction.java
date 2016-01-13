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
package games.stendhal.server.actions.chat;

import static games.stendhal.common.constants.Actions.ANSWER;
import static games.stendhal.common.constants.Actions.CHAT;
import static games.stendhal.common.constants.Actions.EMOTE;
import static games.stendhal.common.constants.Actions.GROUP_MESSAGE;
import static games.stendhal.common.constants.Actions.REPORT_ERROR;
import static games.stendhal.common.constants.Actions.SUPPORT;
import static games.stendhal.common.constants.Actions.TELL;

import games.stendhal.server.actions.CommandCenter;

/**
 * Processes /chat, /tell (/msg) and /support.
 */
public class ChatAction {


	/**
	 * Registers AnswerAction ChatAction TellAction and SupportAction.
	 */
	public static void register() {
		CommandCenter.register(ANSWER, new AnswerAction());
		CommandCenter.register(CHAT, new PublicChatAction());
		CommandCenter.register(EMOTE, new EmoteAction());
		CommandCenter.register(GROUP_MESSAGE, new GroupMessageAction());
        CommandCenter.register(REPORT_ERROR, new ReportErrorAction());
        CommandCenter.register(SUPPORT, new AskForSupportAction());
		CommandCenter.register(TELL, new TellAction());
	}
}
