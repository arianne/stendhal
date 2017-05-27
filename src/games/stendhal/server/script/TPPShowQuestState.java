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

import games.stendhal.server.core.rp.StendhalQuestSystem;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.ThePiedPiper;
import games.stendhal.server.util.TimeUtil;

/**
 * Showing what is current ThePiedPiper quest state, and when it will switch to next.
 *
 * @author yoriy
 */
public class TPPShowQuestState extends ScriptImpl {

	@Override
	public void execute(final Player admin, final List<String> args) {

		StringBuilder sb = new StringBuilder();
		ThePiedPiper TPP = (ThePiedPiper) StendhalQuestSystem.get().getQuest("ThePiedPiper");
		sb.append("The Pied Piper quest state:\n");
        sb.append("Quest phase: "+ThePiedPiper.getPhase().toString()+"\n");
		sb.append("Next phase : "+ThePiedPiper.getNextPhase(ThePiedPiper.getPhase()).toString()+"\n");
		int turns=TPP.getRemainingTurns();
		int seconds=TPP.getRemainingSeconds();
		sb.append("Remaining turns: ");
		sb.append(turns);
		sb.append("\n");
		sb.append("Remaining seconds: ");
        sb.append(seconds);
        sb.append(" ("+TimeUtil.timeUntil(seconds, true)+")");
		//sb.append("\n");
        admin.sendPrivateText(sb.toString());
	}
}
