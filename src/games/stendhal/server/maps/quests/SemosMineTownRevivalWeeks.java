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
package games.stendhal.server.maps.quests;

import games.stendhal.server.maps.quests.revivalweeks.DadNPC;
import games.stendhal.server.maps.quests.revivalweeks.FoundGirl;
import games.stendhal.server.maps.quests.revivalweeks.NineSwitchesGame;
import games.stendhal.server.maps.quests.revivalweeks.PaperChaseSign;
import games.stendhal.server.maps.quests.revivalweeks.TicTacToeGame;
import games.stendhal.server.maps.quests.revivalweeks.TownerClosedSign;

/**
 * <p>Creates a special version of Susi by the semos mine town.
 * <p>Creates a special version of Susi's father in a nearby house.
 * <p>Puts a sign by the tower to say why it is shut.
 */
public class SemosMineTownRevivalWeeks extends AbstractQuest {

	private static final String QUEST_SLOT = "semos_mine_town_revival";
	private PaperChaseSign paperChaseSign;

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		new FoundGirl().addToWorld();
		new DadNPC().addToWorld();
		new TownerClosedSign().addToWorld();
		new TicTacToeGame().addToWorld();
		new NineSwitchesGame().addToWorld();
		paperChaseSign = new PaperChaseSign();
		paperChaseSign.addToWorld();
	}

	/**
	 * removes a quest from the world.
	 *
	 * @return true, if the quest could be removed; false otherwise.
	 */
	@Override
	public boolean removeFromWorld() {
		// TODO: implement me
		paperChaseSign.removeFromWorld();
		return true;
	}

	@Override
	public String getName() {
		return "SemosMineTownRevivalWeeks";
	}
}
