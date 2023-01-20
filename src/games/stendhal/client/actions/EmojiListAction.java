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

import java.util.Collections;
import java.util.List;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.gui.UserInterface;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.common.NotificationType;


public class EmojiListAction implements SlashAction {

	@Override
	public boolean execute(final String[] params, final String remainder) {
		final UserInterface ui = ClientSingletonRepository.getUserInterface();
		final List<String> emojilist = ClientSingletonRepository.getEmojiStore().getEmojiList();
		Collections.sort(emojilist);
		ui.addEventLine(new HeaderLessEventLine(
				emojilist.size() + " emojis available:",
				NotificationType.CLIENT));
		for (final String ename: emojilist) {
			ui.addEventLine(new HeaderLessEventLine(
					"  - :" + ename + ":",
					NotificationType.CLIENT));
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
