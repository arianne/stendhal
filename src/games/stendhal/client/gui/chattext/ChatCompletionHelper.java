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
package games.stendhal.client.gui.chattext;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import games.stendhal.common.filter.CollectionFilter;

/**
 * Matches the entered text of chat with the online player list to enable tab completion of nicknames
 *
 * @author madmetzger
 */
public final class ChatCompletionHelper extends KeyAdapter {

	private static final Logger logger = Logger.getLogger(ChatCompletionHelper.class);

	private final ChatTextController chatController;

	private final Set<String> playersonline;
	private final Set<String> slashCommands;

	private int  lastkeypressed;

	private Collection< ? extends String> resultset = Collections.emptyList();

	private int currentIndex;

	private String output;

	/**
	 * Create a new ChatCompletionHelper
	 * @param chatTextController
	 * @param nameList
	 * @param commands slash commands
	 */
	public ChatCompletionHelper(final ChatTextController chatTextController,
			final Set<String> nameList, final Set<String> commands) {
		chatController = chatTextController;
		playersonline = nameList;
		slashCommands = new HashSet<String>(commands.size());
		for (String s : commands) {
			slashCommands.add("/" + s);
		}
	}

	@Override
	public void keyPressed(final KeyEvent e) {

		final int keypressed = e.getKeyCode();

		if (keypressed == KeyEvent.VK_TAB) {
			if (lastkeypressed != KeyEvent.VK_TAB) {
				currentIndex = 0;
				logger.debug("Contents of PlayerList on tab: "+ playersonline);
				buildNames();

			} else {
				currentIndex++;
				if (currentIndex == resultset.size()) {
					currentIndex = 0;
				}
			}
			if (!resultset.isEmpty()) {

				chatController.setChatLine(output
						+ resultset.toArray()[currentIndex]);
			}
		}
		lastkeypressed = e.getKeyCode();
	}

	private void buildNames() {
		final String[] strwords = chatController.getText()
				.split("\\s+");

		final String prefix = strwords[strwords.length - 1];
		Set<String> completionSet = playersonline;
		// Special handling for slash commands. Complete those only in the
		// beginning of line, and only if they start with a single /
		if ((strwords.length == 1) && prefix.startsWith("/") && !prefix.startsWith("//")) {
			completionSet = slashCommands;
		}

		final CollectionFilter<String> filter = new StringPrefixFilter(
				prefix);
		output = "";
		for (int j = 0; j < strwords.length - 1; j++) {
			output = output + strwords[j] + " ";
		}

		resultset = filter.filterCopy(completionSet);
	}

}
