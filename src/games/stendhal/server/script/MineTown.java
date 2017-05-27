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
import games.stendhal.server.maps.quests.MineTownRevivalWeeks;

/**
 * Starts or stops the Mine Town Revival Weeks.
 *
 * information about semos.xml changes at the bottom of the script
 * @author hendrik
 */
public class MineTown extends ScriptImpl {

	@Override
	public void execute(final Player admin, final List<String> args) {
		if (args.size() != 1) {
			admin.sendPrivateText("/script MineTown.class {true|false}");
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
	 * Starts the Mine Town Revival Weeks.
	 *
	 * @param admin adminstrator running the script
	 */
	private void startSemosMineTowns(Player admin) {
		if (StendhalQuestSystem.get().getQuest(MineTownRevivalWeeks.QUEST_NAME) != null) {
			admin.sendPrivateText("Mine Town Revival Weeks are already active.");
			return;
		}
		StendhalQuestSystem.get().loadQuest(new MineTownRevivalWeeks());
	}

	/**
	 * Ends the Mine Town Revival Weeks.
	 *
	 * @param admin adminstrator running the script
	 */
	private void stopSemosMineTowns(Player admin) {
		if (StendhalQuestSystem.get().getQuest(MineTownRevivalWeeks.QUEST_NAME) == null) {
			admin.sendPrivateText("Mine Town Revival Weeks are not active.");
			return;
		}
		StendhalQuestSystem.get().unloadQuest(MineTownRevivalWeeks.QUEST_NAME);
	}

}
// TODO: these should not be done manually but added and removed as part of the script load and unload
// Mine Town Weeks information
// Loading mountain_n2_mine_town_weeks.tmx and Semos halloween.city.tmx . Both contain several walkblockers for tables, Carolines shop and signs.

// Mine Town map:
// Walkblockers for tables at:
// x="55" y="110">, <entity x="61" y="110">, <entity x="67" y="110">
//<attribute name="description">You see a nice clean table for resting your drinks on.</attribute>
//<attribute name="width">4</attribute>
//<attribute name="height">2</attribute>

// Carolines shop signs at:
// x="60" y="105", x="55" y="105"
// <parameter name="shop">sellrevivalweeks</parameter>, <parameter name="title">Carolines snacks and drinks shop (sells)</parameter>

// Wooden arch at:
// x="94" y="118", x="95" y="119", x="96" y="119"
// <attribute name="text">Welcome to the Mine Town Revival Weeks xxxx!</attribute>

// The Semos city_halloween.tmx map
// Banners at:
// x="53" y="3", x="14" y="4", x="16" y="48", x="58" y="49"
// <attribute name="text">#Mine #Town #Revival #Weeks #xxxx! Enjoy the #x #festival and meet #Susi and her father while celebrating with snacks and drinks! Just take the path up to the #North #from #Semos #City to reach the #Mine #Town!</attribute>
// <attribute name="width">2</attribute>
// <attribute name="height">2</attribute>
