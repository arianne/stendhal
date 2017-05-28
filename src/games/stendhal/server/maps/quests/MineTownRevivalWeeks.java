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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.server.maps.quests.revivalweeks.DadNPC;
import games.stendhal.server.maps.quests.revivalweeks.FishermansDaughterSellingNPC;
import games.stendhal.server.maps.quests.revivalweeks.FoundGirl;
import games.stendhal.server.maps.quests.revivalweeks.LoadableContent;
import games.stendhal.server.maps.quests.revivalweeks.MakeupArtist;
import games.stendhal.server.maps.quests.revivalweeks.NineSwitchesGame;
import games.stendhal.server.maps.quests.revivalweeks.PaperChaseSign;
import games.stendhal.server.maps.quests.revivalweeks.PhotographerNPC;
import games.stendhal.server.maps.quests.revivalweeks.SoldierNPCs;
import games.stendhal.server.maps.quests.revivalweeks.TicTacToeGame;

/**
 * <p>Creates a special version of Susi by the mine town.
 * <p>Creates a special version of Susi's father in a nearby house.
 * <p>Puts a sign by the tower to say why it is shut.
 */
public class MineTownRevivalWeeks extends AbstractQuest {

	public static final String QUEST_NAME = "SemosMineTownRevivalWeeks";
	private static final String QUEST_SLOT = "semos_mine_town_revival";
	private List<LoadableContent> content = new LinkedList<LoadableContent>();

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public void addToWorld() {
		System.setProperty("stendhal.minetown", "true");

		content.add(new FoundGirl());
		content.add(new DadNPC());
		content.add(new SoldierNPCs());
		content.add(new MakeupArtist());
		content.add(new FishermansDaughterSellingNPC());
		content.add(new PaperChaseSign());
		content.add(new PhotographerNPC());
		content.add(new TicTacToeGame());
		content.add(new NineSwitchesGame());
//		content.add(new SokobanGame());

		// add it to the world
		for (LoadableContent loadableContent : content) {
			loadableContent.addToWorld();
		}
	}

	/**
	 * removes a quest from the world.
	 *
	 * @return true, if the quest could be removed; false otherwise.
	 */
	@Override
	public boolean removeFromWorld() {
		System.getProperties().remove("stendhal.minetown");
		for (LoadableContent loadableContent : content) {
			loadableContent.removeFromWorld();
		}
		return true;
	}

	@Override
	public String getName() {
		return QUEST_NAME;
	}

	@Override
	public boolean isVisibleOnQuestStatus() {
		return false;
	}

	@Override
	public List<String> getHistory(final Player player) {
		return new ArrayList<String>();
	}

	@Override
	public String getRegion() {
		return Region.SEMOS_SURROUNDS;
	}

}
