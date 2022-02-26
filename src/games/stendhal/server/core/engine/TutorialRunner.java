/***************************************************************************
 *                   Copyright (C) 2003-2022 - Arianne                     *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.engine;

import org.apache.log4j.Logger;

import games.stendhal.server.core.events.LoginListener;
import games.stendhal.server.core.events.LogoutListener;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.TutorialIsland;


/**
 * Listens for players that have not completed tutorial.
 */
public class TutorialRunner implements LoginListener, LogoutListener {

	private static final Logger logger = Logger.getLogger(TutorialRunner.class);

	private static TutorialRunner instance;

	private static final String SLOT = "tutorial_island";


	public static TutorialRunner get() {
		if (instance == null) {
			instance = new TutorialRunner();
		}

		return instance;
	}

	private TutorialRunner() {
		super();
	}

	/**
	 * Initializes tutorial sequence if player has not completed
	 * tutorial quest.
	 */
	public void onLoggedIn(final Player player) {
		if (!player.hasQuest(SLOT) || !player.getQuest(SLOT, 0).equals("done")) {
			// make sure tutorial quest is loaded
			final TutorialIsland quest = (TutorialIsland) SingletonRepository
				.getStendhalQuestSystem().getQuestFromSlot(SLOT);
			if (quest != null) {
				quest.startTutorialForPlayer(player);
			} else {
				logger.error("failed to load TutorialIsland quest");
			}
		}
	}

	/**
	 * Removes tutorial zone from world.
	 */
	public void onLoggedOut(final Player player) {
		final TutorialIsland quest = (TutorialIsland) SingletonRepository
			.getStendhalQuestSystem().getQuestFromSlot(SLOT);
		if (quest != null) {
			quest.dismantleIsland(player);
		}
	}
}
