/***************************************************************************
 *                    Copyright © 2003-2023 - Arianne                      *
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

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.common.NotificationType;


public class EmojiListAction implements SlashAction {

	@Override
	public boolean execute(final String[] params, final String remainder) {
		for (final String line: ClientSingletonRepository.getEmojiStore().getEmojiList()) {
			ClientSingletonRepository.getUserInterface().addEventLine(
					new HeaderLessEventLine(":" + line + ":", NotificationType.CLIENT));
		}
		return true;
	}

	@Override
	public int getMaximumParameters() {
		return 0;
	}

	@Override
	public int getMinimumParameters() {
		return 0;
	}
}
