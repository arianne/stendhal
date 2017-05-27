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
package games.stendhal.server.script;

import java.util.List;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

/**
 * Puts the Deathmatch slot of a player into victory format.
 *
 * @author kymara
 */
public class FixDM extends ScriptImpl {

	private static final String questName = "deathmatch";

	@Override
	public void execute(final Player admin, final List<String> args) {

		// help text
		if (args.size() < 1) {
			admin.sendPrivateText("Usage /script FixDM.class <player>. Check they have killed all the creatures!");
			return;
		}

		// find player
		final StendhalRPRuleProcessor rules = SingletonRepository.getRuleProcessor();
		final Player target = rules.getPlayer(args.get(0));
		if (target != null) {

			// old state
			if (!target.hasQuest(questName)) {
				admin.sendPrivateText(target.getTitle() + " has never done a deathmatch.");
				return;
			}
			String oldQuestState = target.getQuest(questName);
			if (oldQuestState.equals("done")) {
				admin.sendPrivateText(target.getTitle() + " completed the last deathmatch successfully. No DM is in progress.");
				return;
			}
			final String[] questpieces = oldQuestState.split(";");
			if (questpieces.length < 2) {
				admin.sendPrivateText(target.getTitle() + " had a deathmatch state in " + oldQuestState + "  , i.e. a deathmatch was bailed or cancelled? You're going to need to fix his helmet manually, if you're sure it should be. The quest state has been fixed to #done.");
				target.setQuest(questName, "done");
				target.sendPrivateText("Admin " + admin.getTitle()
								   + " changed your state of the quest '" + questName
									   + "' from '" + oldQuestState + "' to 'done'. They need to fix your helmet, still.");
				return;
			} else {
				String newQuestState = "victory;" + questpieces[1] + ";" + questpieces[2];
				// set the quest
				target.setQuest(questName, newQuestState);

				// notify admin and altered player
				target.sendPrivateText("Try claiming victory again now that " + admin.getTitle()
									   + " has waved a magic wand.");
				admin.sendPrivateText("Changed the state of quest '" + questName
									  + "' from '" + oldQuestState + "' to '" + newQuestState
									  + "'. Told " + target.getTitle() + " to claim victory again.");
			}
		} else {
				admin.sendPrivateText(args.get(0) + " is not logged in");
		}
	}
}
