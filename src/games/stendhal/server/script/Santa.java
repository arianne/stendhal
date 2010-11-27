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
import games.stendhal.server.maps.quests.MeetSanta;

import java.util.List;

/**
 * Starts or stops Santa.
 * 
 * @author kymara
 */
public class Santa extends ScriptImpl {

	@Override
	public void execute(final Player admin, final List<String> args) {
		if (args.size() != 1) {
			admin.sendPrivateText("/script Santa.class {true|false}");
			return;
		}

		boolean enable = Boolean.parseBoolean(args.get(0));
		if (enable) {
			startChristmas(admin);
		} else {
			stopChristmas(admin);
		}
	}

	/**
	 * starts Christmas.
	 */
	private void startChristmas(Player admin) {
		if (StendhalQuestSystem.get().getQuest(MeetSanta.QUEST_NAME) != null) {
			admin.sendPrivateText("Santa is already active.");
			return;
		}
		StendhalQuestSystem.get().loadQuest(new MeetSanta());
	}

	/**
	 * ends Christmas
	 */
	private void stopChristmas(Player admin) {
		if (StendhalQuestSystem.get().getQuest(MeetSanta.QUEST_NAME) == null) {
			admin.sendPrivateText("Santa is not active.");
			return;
		}
		StendhalQuestSystem.get().unloadQuest(MeetSanta.QUEST_NAME);
	}

}
