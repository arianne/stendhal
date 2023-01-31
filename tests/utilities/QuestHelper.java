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

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
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

	private static final Logger logger = Logger.getLogger(QuestHelper.class);

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

	/**
	 * Loads quest instances.
	 */
	public static void loadQuests(final List<IQuest> qs) {
		for (final IQuest q: qs) {
			quests.loadQuest(q);
		}
	}

	/**
	 * Loads quest instances.
	 */
	public static void loadQuests(final IQuest... qs) {
		loadQuests(Arrays.asList(qs));
	}

	/**
	 * Loads quests for a region.
	 *
	 * FIXME: does not always load quest from resource
	 *
	 * @param region
	 *     Region identifier.
	 * @return
	 *     List of quests loaded for that region.
	 */
	public static List<IQuest> loadRegionalQuests(final String region) {
		final List<IQuest> loaded = new ArrayList<>();
		for (final IQuest q: getQuestResources()) {
			if (region.equals(q.getRegion())) {
				loaded.add(q);
			}
		}
		loadQuests(loaded);
		return loaded;
	}

	/**
	 * Loads quests using slot identifiers.
	 *
	 * FIXME: does not always load quest from resource
	 *
	 * @param slots
	 *     List of slot identifiers.
	 * @return
	 *     List of quests loaded that match slot list.
	 */
	public static List<IQuest> loadQuestsBySlot(final String... slots) {
		final List<String> slotlist = Arrays.asList(slots);
		final List<IQuest> loaded = new ArrayList<>();
		for (final IQuest q: getQuestResources()) {
			if (slotlist.contains(q.getSlotName())) {
				loaded.add(q);
			}
		}
		loadQuests(loaded);
		return loaded;
	}

	private static List<IQuest> getQuestResources() {
		final List<IQuest> resources = new ArrayList<>();
		final String packagename = IQuest.class.getPackage().getName();
		// this only works for non-jar packaged resources
		final URL dirquests = ClassLoader.getSystemClassLoader().getResource(packagename.replace(".", "/"));
		List<String> excludes = Arrays.asList("IQuest.class", "AbstractQuest.class", "package-info.class");
		final String[] filenames = new File(dirquests.getFile()).list(new FilenameFilter() {
			public boolean accept(final File dir, final String name) {
				return name.endsWith(".class") && name.indexOf("$") < 0 && !excludes.contains(name);
			}
		});
		for (String f: filenames) {
			f = packagename + "." + f.substring(0, f.indexOf(".class"));
			Object obj = null;
			try {
				obj = Class.forName(f).getConstructor().newInstance();
			} catch (final Exception e) {
				logger.error(e, e);
			}
			if (!(obj instanceof IQuest)) {
				continue;
			}
			resources.add((IQuest) obj);
		}
		return resources;
	}

	/**
	 * Unloads quest instances.
	 */
	public static void unloadQuests(final IQuest... qs) {
		for (final IQuest q: qs) {
			quests.unloadQuest(q);
		}
	}

	/**
	 * Unloads quest instances.
	 */
	public static void unloadQuests(final List<IQuest> qs) {
		for (final IQuest q: qs) {
			quests.unloadQuest(q);
		}
	}

	/**
	 * Unloads quests using slot identifier strings.
	 */
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

	/**
	 * Retrieves a list of loaded quest slot identifiers.
	 */
	public static List<String> getLoadedSlots() {
		return quests.getLoadedSlots();
	}

	/**
	 * Checks if quests are loaded using slot ID.
	 *
	 * @param qs
	 *     Quest instances to be checked.
	 */
	public static boolean isLoaded(final IQuest... qs) {
		for (final IQuest q: qs) {
			if (!quests.isLoadedSlot(q.getSlotName())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if quests are loaded using slot ID.
	 *
	 * @param slots
	 *     Slot IDs to be checked.
	 */
	public static boolean isLoaded(final String... slots) {
		for (final String slot: slots) {
			if (!quests.isLoadedSlot(slot)) {
				return false;
			}
		}
		return true;
	}
}
