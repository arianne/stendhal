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
package games.stendhal.server.actions.query;

import static games.stendhal.common.constants.Actions.LISTQUESTS;
import static games.stendhal.common.constants.Actions.TARGET;

import games.stendhal.common.NotificationType;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * list the known quest or gives details on them.
 */
public class QuestListAction implements ActionListener {


	public static void register() {
		CommandCenter.register(LISTQUESTS, new QuestListAction());
	}

	@Override
	public void onAction(final Player player, final RPAction action) {

		final StringBuilder st = new StringBuilder();
		if (action.has(TARGET)) {
			final String which = action.get(TARGET);
			st.append(SingletonRepository.getStendhalQuestSystem().listQuest(player, which));

		} else {
			st.append(SingletonRepository.getStendhalQuestSystem().listQuests(player));
		}
		player.sendPrivateText(NotificationType.DETAILED, st.toString());
		player.notifyWorldAboutChanges();

	}

}
