/***************************************************************************
 *                     Copyright © 2023 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.rp.achievement.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getSpeakerNPC;
import static utilities.ZoneAndPlayerTestImpl.setupZone;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.*;
import utilities.AchievementTestHelper;
import utilities.NPCTestHelper;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.QuestRunner;


public class QuestAchievementFactoryTest extends AchievementTestHelper {

	private Player player;

	private List<IQuest> qloaded = new ArrayList<>();


	@Before
	public void setUp() {
		player = createPlayer("player");
		assertNotNull(player);
		init(player);
		setupZone("testzone");
		PlayerTestHelper.registerPlayer(player, "testzone");
		assertNotNull(player.getZone());
	}

	@After
	public void tearDown() {
		QuestHelper.unloadQuests(qloaded);
		for (int idx = qloaded.size()-1; idx >= 0; idx--) {
			assertFalse(QuestHelper.isLoaded(qloaded.get(idx)));
			qloaded.remove(idx);
		}
		assertEquals(0, qloaded.size());
		//~ assertEquals(0, QuestHelper.getLoadedSlots().size());
		// clean up NPCs
		assertTrue(NPCTestHelper.removeAllNPCs());
		PlayerTestHelper.removePlayer(player);
	}

	private void loadQuests(final IQuest... qs) {
		final int qcount = qloaded.size();
		QuestHelper.loadQuests(qs);
		for (final IQuest q: qs) {
			assertTrue(QuestHelper.isLoaded(q));
			qloaded.add(q);
		}
		assertEquals(qcount + qs.length, qloaded.size());
	}

	private void setupZones(final String... zones) {
		for (final String zone: zones) {
			setupZone(zone);
		}
	}

	private void loadConfigurators(final ZoneConfigurator... zc) {
		setupZone("testzone", zc);
	}

	/* TODO:
	 * - Fairgoer
	 * - Patiently Waiting on Grumpy
	 * - Aide to Semos Folk
	 * - Helper of Ados City Dwellers
	 * - Quest Junkie
	 */

	@Test
	public void testFaiumonisCasanova() {
		final int required = 25;
		final String id = "quest.special.elf_princess.00" + required;
		setupZones("int_semos_house");
		loadConfigurators(
			new games.stendhal.server.maps.nalwor.tower.PrincessNPC(),
			new games.stendhal.server.maps.semos.house.FlowerSellerNPC()
		);
		assertNotNull(getSpeakerNPC("Tywysoga"));
		assertNotNull(getSpeakerNPC("Rose Leigh"));
		loadQuests(new ElfPrincess());
		for (int completions = 0; completions < required; completions++) {
			assertFalse(achievementReached(player, id));
			QuestRunner.doQuestElfPrincess(player);
		}
		assertEquals(String.valueOf(required), player.getQuest("elf_princess", 2));
		assertTrue(achievementReached(player, id));
	}

	@Test
	public void testFloralFondness() {
		final int required = 50;
		final String id = "quest.flowershop.00" + required;
		loadConfigurators(new games.stendhal.server.maps.nalwor.flowershop.FlowerGrowerNPC());
		assertNotNull(getSpeakerNPC("Seremela"));
		loadQuests(new RestockFlowerShop());
		for (int completions = 0; completions < required; completions++) {
			assertFalse(achievementReached(player, id));
			QuestRunner.doQuestRestockFlowerShop(player);
		}
		assertEquals(String.valueOf(required), player.getQuest("restock_flowershop", 2));
		assertTrue(achievementReached(player, id));
	}

	@Test
	public void testHeretic() {
		final int required = 25;
		final String id = "quest.special.kill_monks.00" + required;
		loadConfigurators(new games.stendhal.server.maps.ados.city.ManWithHatNPC());
		assertNotNull(getSpeakerNPC("Andy"));
		loadQuests(new KillMonks());
		for (int completions = 0; completions < required; completions++) {
			assertFalse(achievementReached(player, id));
			QuestRunner.doQuestKillMonks(player);
		}
		assertEquals(String.valueOf(required), player.getQuest("kill_monks", 2));
		assertTrue(achievementReached(player, id));
	}

	@Test
	public void testPathfinder() {
		final String id = "quest.special.maze";
		loadConfigurators(new games.stendhal.server.maps.ados.magician_house.WizardNPC());
		final SpeakerNPC haizen = getSpeakerNPC("Haizen");
		assertNotNull(haizen);
		loadQuests(new Maze());
		assertFalse(achievementReached(player, id));
		final Engine en = haizen.getEngine();
		en.step(player, "hi");
		en.step(player, "maze");
		en.step(player, "yes");
		final StendhalRPZone maze = player.getZone();
		assertNotNull(maze);
		// check that player is in maze
		assertEquals("player_maze", maze.getName());
		final Portal exit = maze.getPortals().get(0);
		player.setPosition(exit.getX(), exit.getY());
		assertEquals(exit.getX(), player.getX());
		assertEquals(exit.getY(), player.getY());
		exit.onUsed(player);
		// portal doesn't teleport in tests
		//~ assertEquals("testzone", player.getZone().getName());
		assertTrue(achievementReached(player, id));
	}
}
