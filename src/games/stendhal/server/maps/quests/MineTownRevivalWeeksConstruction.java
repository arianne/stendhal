/***************************************************************************
 *                      (C) Copyright 2010 - Stendhal                      *
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
import games.stendhal.server.maps.quests.revivalweeks.BuilderNPC;
import games.stendhal.server.maps.quests.revivalweeks.LoadableContent;

/**
 * Sets up the construction of Mine Town Revival Weeks
 */
public class MineTownRevivalWeeksConstruction extends AbstractQuest {

	public static final String QUEST_NAME = "SemosMineTownRevivalWeeksConstruction";
	private static final String QUEST_SLOT = "semos_mine_town_revival_construction";
	private List<LoadableContent> content = new LinkedList<LoadableContent>();

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public void addToWorld() {
		System.setProperty("stendhal.minetownconstruction", "true");

		content.add(new BuilderNPC());

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
		System.getProperties().remove("stendhal.minetownconstruction");
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
