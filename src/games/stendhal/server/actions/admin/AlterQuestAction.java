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

import games.stendhal.common.NotificationType;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

class AlterQuestAction extends AdministrationAction {

	private static final int REQUIREDLEVEL = 900;

	@Override
	protected void perform(final Player player, final RPAction action) {


		// find player
		final StendhalRPRuleProcessor rules = SingletonRepository.getRuleProcessor();
		final Player target = rules.getPlayer(action.get("target"));
		if (target != null) {

			// old state
			final String questName = action.get("name");
			String oldQuestState = null;
			if (target.hasQuest(questName)) {
				oldQuestState = target.getQuest(questName);
			}

			// new state (or null to remove the quest)
			final String newQuestState = action.get("state");


			// set the quest
			target.setQuest(questName, newQuestState);

			// notify admin and altered player
			target.sendPrivateText(NotificationType.SUPPORT,
					"Admin " + player.getTitle()
					+ " changed your state of the quest '" + questName
					+ "' from '" + oldQuestState + "' to '" + newQuestState
					+ "'");
			player.sendPrivateText("Changed the state of quest '" + questName
					+ "' from '" + oldQuestState + "' to '" + newQuestState
					+ "'");
		} else {
			player.sendPrivateText(action.get("target") + " is not logged in");
		}

	}

	public static void register() {
		CommandCenter.register("alterquest", new AlterQuestAction(), REQUIREDLEVEL);
	}

}
