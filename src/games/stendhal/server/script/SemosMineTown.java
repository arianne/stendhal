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

import games.stendhal.server.core.rp.StendhalQuestSystem;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.SemosMineTownRevivalWeeks;

import java.util.List;

/**
 * Starts or stops the Semos Mine Town Revival Weeks.
 * 
 * @author hendrik
 */
public class SemosMineTown extends ScriptImpl {

	@Override
	public void execute(final Player admin, final List<String> args) {
		if (args.size() != 1) {
			admin.sendPrivateText("/script SemosMineTown.class {true|false}");
			return;
		}

		boolean enable = Boolean.parseBoolean(args.get(0));
		if (enable) {
			startSemosMineTowns(admin);
		} else {
			stopSemosMineTowns(admin);
		}
	}

	/**
	 * starts the Semos Mine Town Revival Weeks
	 */
	private void startSemosMineTowns(Player admin) {
		if (StendhalQuestSystem.get().getQuest(SemosMineTownRevivalWeeks.QUEST_NAME) != null) {
			admin.sendPrivateText("Semos Mine Town Revival Weeks are already active.");
			return;
		}
		StendhalQuestSystem.get().loadQuest(new SemosMineTownRevivalWeeks());
	}

	/**
	 * ends the Semos Mine Town Revival Weeks
	 */
	private void stopSemosMineTowns(Player admin) {
		if (StendhalQuestSystem.get().getQuest(SemosMineTownRevivalWeeks.QUEST_NAME) == null) {
			admin.sendPrivateText("Semos Mine Town Revival Weeks are not active.");
			return;
		}
		StendhalQuestSystem.get().unloadQuest(SemosMineTownRevivalWeeks.QUEST_NAME);
	}

}
