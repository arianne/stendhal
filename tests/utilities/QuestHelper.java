/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package utilities;

import java.util.List;

import org.junit.BeforeClass;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rp.StendhalQuestSystem;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.quests.IQuest;
import marauroa.common.Log4J;
import marauroa.server.game.db.DatabaseFactory;
import utilities.RPClass.ItemTestHelper;

/**
 * Helper methods for testing quests.
 *
 * @author hendrik
 */
public abstract class QuestHelper extends PlayerTestHelper  {

	protected static final StendhalQuestSystem quests = SingletonRepository.getStendhalQuestSystem();


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		new DatabaseFactory().initializeDatabase();
		MockStendlRPWorld.get();
		generatePlayerRPClasses();
		ItemTestHelper.generateRPClasses();
		generateNPCRPClasses();

		MockStendhalRPRuleProcessor.get();
		// load item configurations to handle money and other items
		SingletonRepository.getEntityManager();

		SingletonRepository.getNPCList().clear();
	}

	public static void loadQuests(final IQuest... qs) {
		for (final IQuest q: qs) {
			quests.loadQuest(q);
		}
	}

	public static void unloadQuests(final IQuest... qs) {
		for (final IQuest q: qs) {
			quests.unloadQuest(q);
		}
	}

	public static void unloadQuests(final String... slots) {
		for (final String slot: slots) {
			quests.unloadQuestSlot(slot);
		}
	}

	/**
	 * Unloads all loaded quests.
	 */
	public static void unloadQuests() {
		for (final String slot: quests.getLoadedSlots()) {
			quests.unloadQuestSlot(slot);
		}
	}

	public static List<String> getLoadedSlots() {
		return quests.getLoadedSlots();
	}

	public static boolean isLoaded(final IQuest q) {
		return quests.isLoaded(q);
	}
}
