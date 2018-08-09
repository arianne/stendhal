/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions.buddy;

import static games.stendhal.common.constants.Actions.DURATION;
import static games.stendhal.common.constants.Actions.LIST;
import static games.stendhal.common.constants.Actions.REASON;
import static games.stendhal.common.constants.Actions.TARGET;

import java.util.LinkedList;
import java.util.List;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * manages the ignore list
 */
class IgnoreAction implements ActionListener {

	@Override
	public void onAction(final Player player, final RPAction action) {
		if (action.has(LIST)) {
			// if we don't test this then getSlot and it was empty does something bad to client.
			if (player.getSlot("!ignore").size() > 0) {
				// clone the !ignore slot to avoid a ConcurrentModificationException
				// as the check in getIgnore modifies !ignore
				final RPSlot ignoreSlot = (RPSlot) player.getSlot("!ignore").clone();
				final RPObject ignorelist = ignoreSlot.iterator().next();
				List<String> namesAndReasons = new LinkedList<String>();
				for (final String playerName : ignorelist) {
					String name;
					// Can remove handling of '_' prefix if ID is made completely virtual
					if (playerName.charAt(0) == '_') {
						name = playerName.substring(1);
					} else {
						name = playerName;
					}
					// id is on the ignore list, remove him.
					if (!"id".equals(name)) {
						String checkIgnore = player.getIgnore(name);
						// reason could be null if time expired
						if (checkIgnore != null) {
							String reason;
							if (checkIgnore.length() == 0) {
								reason = "";
							} else {
								reason = ", for " + checkIgnore;
							}
							namesAndReasons.add(name + reason);
						}
					}
				}
				if (namesAndReasons.isEmpty()) {
					//	no, we can't just use the slot size test above as 'id' is in there, a bogus entry.
					player.sendPrivateText("你的屏闭列表是空的。");
				} else if (namesAndReasons.size() < 50) {
					player.sendPrivateText("你屏闭的玩家有：" + Grammar.enumerateCollection(namesAndReasons) + ".");
				} else {
					// list could be unfeasibly long to print out to player so give a sublist but also the total number out of interest
					player.sendPrivateText("你屏闭了 " + namesAndReasons.size() + " 个玩家. 这是列表中的前50个玩家名单。"
							+ Grammar.enumerateCollection(namesAndReasons.subList(0, 50)) + ".");
				}
			}
		} else if (action.has(TARGET)) {
			int duration;
			String reason;
			final String who = action.get(TARGET);

			if (action.has(DURATION)) {
				duration = action.getInt(DURATION);
			} else {
				duration = 0;
			}

			if (action.has(REASON)) {
				reason = action.get(REASON);
			} else {
				reason = null;
			}

			if (player.addIgnore(who, duration, reason)) {
				player.sendPrivateText(who + " 已加入你的屏闭列表。");
			}
		}

	}

}
