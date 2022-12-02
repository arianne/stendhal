/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.rp.achievement.friend;

import static games.stendhal.server.core.rp.achievement.factory.FriendAchievementFactory.ID_PRIVATE_DETECTIVE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.quests.AGrandfathersWish;
import games.stendhal.server.maps.quests.FindGhosts;
import games.stendhal.server.maps.quests.FindJefsMom;
import games.stendhal.server.maps.quests.FindRatChildren;
import games.stendhal.server.maps.quests.IQuest;
import games.stendhal.server.maps.quests.SevenCherubs;
import marauroa.server.game.db.DatabaseFactory;
import utilities.AchievementTestHelper;
import utilities.PlayerTestHelper;
import utilities.ZonePlayerAndNPCTestImpl;


public class PrivateDetectiveAchievementTest extends ZonePlayerAndNPCTestImpl {

	private static final Logger logger = Logger.getLogger(PrivateDetectiveAchievementTest.class);

	private Player player;

	private static final StendhalRPWorld world = MockStendlRPWorld.get();

	private static final NPCList npcs = SingletonRepository.getNPCList();

	private final String[] questSlots = {
			"find_rat_kids", "find_ghosts", "seven_cherubs", "find_jefs_mom",
			AGrandfathersWish.QUEST_SLOT
	};


	@BeforeClass
	public static void setUpBeforeClass() {
		new DatabaseFactory().initializeDatabase();
	}

	@Override
	@Before
	public void setUp() throws Exception {
		final Map<String, ZoneConfigurator> configurators = new HashMap<>();
		configurators.put("Agnus", new games.stendhal.server.maps.ratcity.house1.OldRatWomanNPC());
		configurators.put("Opal", new games.stendhal.server.maps.orril.dungeon.RatChild1NPC());
		configurators.put("Mariel", new games.stendhal.server.maps.orril.dungeon.RatChild2NPC());
		configurators.put("Cody", new games.stendhal.server.maps.orril.dungeon.RatChildBoy1NPC());
		configurators.put("Avalon", new games.stendhal.server.maps.orril.dungeon.RatChildBoy2NPC());
		configurators.put("Carena", new games.stendhal.server.maps.ados.hauntedhouse.WomanGhostNPC());
		configurators.put("Mary", new games.stendhal.server.maps.athor.cave.GhostNPC());
		configurators.put("Ben", new games.stendhal.server.maps.ados.city.KidGhostNPC());
		configurators.put("Zak", new games.stendhal.server.maps.wofol.house5.GhostNPC());
		configurators.put("Goran", new games.stendhal.server.maps.orril.dungeon.GhostNPC());
		configurators.put("Jef", new games.stendhal.server.maps.kirdneh.city.GossipNPC());
		configurators.put("Amber", new games.stendhal.server.maps.fado.forest.OldWomanNPC());
		configurators.put("Elias Breland", new games.stendhal.server.maps.deniran.cityinterior.brelandhouse.GrandfatherNPC());
		configurators.put("Niall Breland", new games.stendhal.server.maps.deniran.cityinterior.brelandhouse.GrandsonNPC());
		configurators.put("Marianne", new games.stendhal.server.maps.deniran.cityoutside.LittleGirlNPC());
		configurators.put("Father Calenus", new games.stendhal.server.maps.ados.church.PriestNPC());

		final String zoneName = "testzone";
		for (final ZoneConfigurator zc: configurators.values()) {
			addZoneConfigurator(zc, zoneName);
		}

		final Set<String> allNPCs = new HashSet<>();
		allNPCs.addAll(configurators.keySet());

		// zones required for Seven Cherubs quest
		world.addRPZone("none", new StendhalRPZone("0_semos_village_w"));
		world.addRPZone("none", new StendhalRPZone("0_nalwor_city"));
		world.addRPZone("none", new StendhalRPZone("0_orril_river_s"));
		world.addRPZone("none", new StendhalRPZone("0_orril_river_s_w2"));
		world.addRPZone("none", new StendhalRPZone("0_orril_mountain_w2"));
		world.addRPZone("none", new StendhalRPZone("0_semos_mountain_n2_w2"));
		world.addRPZone("none", new StendhalRPZone("0_ados_rock"));

		// zones required for A Grandfather's Wish quest
		world.addRPZone("none", new StendhalRPZone("-1_myling_well"));

		setNpcNames(allNPCs.toArray(new String[0]));
		zone = setupZone(zoneName);
		setZoneForPlayer(zoneName);

		super.setUp();

		// load quests
		quests.loadQuest(new FindRatChildren());
		quests.loadQuest(new FindGhosts());
		quests.loadQuest(new SevenCherubs());
		quests.loadQuest(new FindJefsMom());
		quests.loadQuest(new AGrandfathersWish());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		PlayerTestHelper.removeAllPlayers();

		final IQuest toUnload = quests.getQuestFromSlot(AGrandfathersWish.QUEST_SLOT);
		assertNotNull(toUnload);
		quests.unloadQuest(toUnload.getName());
		assertFalse(quests.isLoaded(toUnload));
	}

	@Test
	public void init() {
		resetPlayer();

		for (final String slot : questSlots) {
			final IQuest qInstance = quests.getQuestFromSlot(slot);
			assertNotNull(qInstance);
			logger.info("checking quest loaded: " + qInstance.getName());
			assertTrue(quests.isLoaded(qInstance));
		}

		doQuestAgnus();
		assertFalse(achievementReached());
		doQuestCarena();
		assertFalse(achievementReached());
		doQuestCherubs();
		assertFalse(achievementReached());
		doQuestJef();
		assertFalse(achievementReached());
		doQuestGrandfathersWish();

		assertTrue(achievementReached());
	}

	private boolean achievementReached() {
		return AchievementTestHelper.achievementReached(player, ID_PRIVATE_DETECTIVE);
	}

	private void resetPlayer() {
		if (player != null) {
			removePlayer(player);
		}
		player = PlayerTestHelper.createPlayer("player");
		assertNotNull(player);
		registerPlayer(player, "testzone");

		for (final String quest: questSlots) {
			assertNull(player.getQuest(quest));
		}

		AchievementTestHelper.init(player);
		assertFalse(achievementReached());
	}

	private void doQuestAgnus() {
		final String questSlot = "find_rat_kids";
		assertNull(player.getQuest(questSlot));

		final SpeakerNPC agnus = npcs.get("Agnus");
		final SpeakerNPC opal = npcs.get("Opal");
		final SpeakerNPC mariel = npcs.get("Mariel");
		final SpeakerNPC cody = npcs.get("Cody");
		final SpeakerNPC avalon = npcs.get("Avalon");
		assertNotNull(agnus);
		assertNotNull(opal);
		assertNotNull(mariel);
		assertNotNull(cody);
		assertNotNull(avalon);

		Engine en = agnus.getEngine();

		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "children");
		en.step(player, "yes");
		en.step(player, "bye");

		for (final SpeakerNPC ratchild: Arrays.asList(opal, mariel, cody, avalon)) {
			en = ratchild.getEngine();

			en.step(player, "hi");

			// don't need to say "bye"
			assertEquals(ConversationStates.IDLE, en.getCurrentState());
		}

		en = agnus.getEngine();

		en.step(player, "hi");
		en.step(player, opal.getName());
		en.step(player, mariel.getName());
		en.step(player, cody.getName());
		en.step(player, avalon.getName());
		en.step(player, "bye");

		assertEquals("done", player.getQuest(questSlot, 0));
	}

	private void doQuestCarena() {
		final String questSlot = "find_ghosts";
		assertNull(player.getQuest(questSlot));

		final SpeakerNPC carena = npcs.get("Carena");
		final SpeakerNPC mary = npcs.get("Mary");
		final SpeakerNPC ben = npcs.get("Ben");
		final SpeakerNPC zak = npcs.get("Zak");
		final SpeakerNPC goran = npcs.get("Goran");
		assertNotNull(carena);
		assertNotNull(mary);
		assertNotNull(ben);
		assertNotNull(zak);
		assertNotNull(goran);

		Engine en = carena.getEngine();

		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "spirits");
		en.step(player, "yes");
		en.step(player, "bye");

		for (final SpeakerNPC ghost: Arrays.asList(mary, ben, zak, goran)) {
			en = ghost.getEngine();

			en.step(player, "hi");

			// don't need to say "bye"
			assertEquals(ConversationStates.IDLE, en.getCurrentState());
		}

		en = carena.getEngine();

		en.step(player, "hi");
		en.step(player, mary.getName());
		en.step(player, ben.getName());
		en.step(player, zak.getName());
		en.step(player, goran.getName());
		en.step(player, "bye");

		assertEquals("done", player.getQuest(questSlot, 0));
	}

	private void doQuestCherubs() {
		final String questSlot = "seven_cherubs";
		assertNull(player.getQuest(questSlot));

		SpeakerNPC cherub;
		Engine en;
		final List<String> nameList = Arrays.asList(
				"Cherubiel", "Gabriel", "Ophaniel", "Raphael", "Uriel",
				"Zophiel", "Azazel");

		final StringBuilder sb = new StringBuilder();

		for (final String name: nameList) {
			cherub = npcs.get(name);
			en = cherub.getEngine();

			en.step(player, "hi");

			// don't need to say "bye"
			assertEquals(ConversationStates.IDLE, en.getCurrentState());

			sb.append(";" + name);
		}

		assertEquals(sb.toString(), player.getQuest(questSlot));
	}

	private void doQuestJef() {
		final String questSlot = "find_jefs_mom";
		assertNull(player.getQuest(questSlot));

		final SpeakerNPC jef = npcs.get("Jef");
		final SpeakerNPC amber = npcs.get("Amber");
		assertNotNull(jef);
		assertNotNull(amber);

		Engine en = jef.getEngine();

		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "yes");
		en.step(player, "bye");

		assertNotNull(player.getQuest(questSlot));

		en = amber.getEngine();

		en.step(player, "hi");
		en.step(player, "jef");
		en.step(player, "bye");

		en = jef.getEngine();

		en.step(player, "hi");
		en.step(player, "fine");
		en.step(player, "bye");

		assertEquals("done", player.getQuest(questSlot, 0));
	}

	private void doQuestGrandfathersWish() {
		final String questSlot = AGrandfathersWish.QUEST_SLOT;
		assertNull(player.getQuest(questSlot));

		final SpeakerNPC niall = npcs.get("Niall Breland");
		assertNotNull(niall);

		// set quest to final step
		player.setQuest(questSlot, "investigate;find_myling:done;holy_water:done;cure_myling:done");

		final Engine en = niall.getEngine();
		en.step(player, "hi");
		en.step(player, "bye");

		assertEquals("done", player.getQuest(questSlot, 0));
	}
}
