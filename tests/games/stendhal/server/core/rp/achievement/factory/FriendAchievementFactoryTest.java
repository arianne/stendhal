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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rp.StendhalQuestSystem;
import games.stendhal.server.core.rp.achievement.factory.stub.ChildrensFriendStub;
import games.stendhal.server.core.rp.achievement.factory.stub.PrivateDetectiveStub;
import games.stendhal.server.core.rp.achievement.factory.stub.StillBelievingStub;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.quests.AGrandfathersWish;
import games.stendhal.server.maps.quests.Campfire;
import games.stendhal.server.maps.quests.ChocolateForElisabeth;
import games.stendhal.server.maps.quests.CodedMessageFromFinnFarmer;
import games.stendhal.server.maps.quests.EggsForMarianne;
import games.stendhal.server.maps.quests.FindGhosts;
import games.stendhal.server.maps.quests.FindJefsMom;
import games.stendhal.server.maps.quests.FindRatChildren;
import games.stendhal.server.maps.quests.FishSoupForHughie;
import games.stendhal.server.maps.quests.IQuest;
import games.stendhal.server.maps.quests.IcecreamForAnnie;
import games.stendhal.server.maps.quests.MedicineForTad;
import games.stendhal.server.maps.quests.MeetBunny;
import games.stendhal.server.maps.quests.MeetSanta;
import games.stendhal.server.maps.quests.MineTownRevivalWeeks;
import games.stendhal.server.maps.quests.PlinksToy;
import games.stendhal.server.maps.quests.SevenCherubs;
import games.stendhal.server.maps.quests.ToysCollector;
import marauroa.common.game.RPObject;
import utilities.AchievementTestHelper;


public class FriendAchievementFactoryTest extends AchievementTestHelper {

	private static final Logger logger = Logger.getLogger(FriendAchievementFactoryTest.class);

	private static final StendhalRPWorld world = MockStendlRPWorld.get();
	private static final StendhalQuestSystem quests = SingletonRepository.getStendhalQuestSystem();
	private static final NPCList npcs = SingletonRepository.getNPCList();

	private Player player;
	private static StendhalRPZone testzone;
	private static List<StendhalRPZone> zonelist;
	private static List<IQuest> questlist;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		AchievementTestHelper.setUpBeforeClass();
		testzone = new StendhalRPZone("testzone");
		world.addRPZone(null, testzone);
		zonelist = new ArrayList<>();
		questlist = new ArrayList<>();
	}

	@AfterClass
	public static void tearDownAfterClass() {
		world.removeZone(testzone);
	}

	@Before
	public void setUp() {
		assertNotNull(testzone);
		assertTrue(testzone.isEmpty());
		assertTrue(zonelist.isEmpty());
		// dress player for Meet Santa
		player = createPlayerWithOutFit("player");
		assertNotNull(player);
		init(player);
	}

	@After
	public void tearDown() {
		for (int idx = questlist.size()-1; idx >= 0; idx--) {
			questlist.get(idx).removeFromWorld();
			questlist.remove(idx);
		}
		assertTrue(questlist.isEmpty());
		for (int idx = zonelist.size()-1; idx >= 0; idx--) {
			final StendhalRPZone z = zonelist.get(idx);
			final Iterator<RPObject> it = z.iterator();
			while (it.hasNext()) {
				z.remove(it.next());
			}
			assertTrue(z.isEmpty());
			world.removeZone(z);
			zonelist.remove(idx);
		}
		assertTrue(zonelist.isEmpty());
		final List<NPC> zonenpcs = new ArrayList<>();
		zonenpcs.addAll(testzone.getNPCList());
		for (final NPC npc: zonenpcs) {
			testzone.remove(npc);
		}
		assertTrue(testzone.isEmpty());
	}

	private void addZones(final String... zonenames) {
		for (final String zonename: zonenames) {
			final StendhalRPZone newzone = new StendhalRPZone(zonename);
			world.addRPZone(null, newzone);
			zonelist.add(newzone);
		}
	}

	private void loadConfigurators(final ZoneConfigurator... toload) {
		for (final ZoneConfigurator configurator: toload) {
			configurator.configureZone(testzone, null);
		}
		assertFalse(testzone.getNPCList().isEmpty());
	}

	private void loadQuests(final IQuest... toload) {
		for (final IQuest quest: toload) {
			assertFalse(quests.isLoaded(quest));
			quests.loadQuest(quest);
			assertTrue(quests.isLoaded(quest));
		}
	}

	@Test
	public void testChildrensFriend() {
		final String id = "friend.quests.children";
		assertFalse(achievementReached(player, id));

		// Susi
		loadConfigurators(new games.stendhal.server.maps.ados.rosshouse.LittleGirlNPC());
		addZones("int_semos_frank_house", "0_semos_mountain_n2", "int_ados_ross_house",
			"0_ados_city_n", "int_ados_carolines_house_0");
		loadQuests(new MineTownRevivalWeeks());
		ChildrensFriendStub.doQuestSusi(player);
		assertFalse(achievementReached(player, id));

		loadConfigurators(
			// Tad
			new games.stendhal.server.maps.semos.hostel.BoyNPC(),
			// Ilisa
			new games.stendhal.server.maps.semos.temple.HealerNPC(),
			// Ketteh Wehoh
			new games.stendhal.server.maps.semos.townhall.DecencyAndMannersWardenNPC());
		loadQuests(new MedicineForTad());
		ChildrensFriendStub.doQuestTad(player);
		assertFalse(achievementReached(player, id));

		// Plink
		loadConfigurators(new games.stendhal.server.maps.semos.plains.LittleBoyNPC());
		addZones("0_semos_plains_n");
		loadQuests(new PlinksToy());
		ChildrensFriendStub.doQuestPlink(player);
		assertFalse(achievementReached(player, id));

		// Anna & George
		loadConfigurators(new games.stendhal.server.maps.ados.city.KidsNPCs());
		loadQuests(new ToysCollector());
		ChildrensFriendStub.doQuestAnna(player);
		assertFalse(achievementReached(player, id));

		// Sally
		loadConfigurators(new games.stendhal.server.maps.orril.river.CampingGirlNPC());
		loadQuests(new Campfire());
		ChildrensFriendStub.doQuestSally(player);
		assertFalse(achievementReached(player, id));

		loadConfigurators(
			// Annie Jones
			new games.stendhal.server.maps.kalavan.citygardens.LittleGirlNPC(),
			// Mrs Jones
			new games.stendhal.server.maps.kalavan.citygardens.MummyNPC());
		loadQuests(new IcecreamForAnnie());
		ChildrensFriendStub.doQuestAnnie(player);
		assertFalse(achievementReached(player, id));

		loadConfigurators(
			// Elisabeth
			new games.stendhal.server.maps.kirdneh.city.LittleGirlNPC(),
			// Carey
			new games.stendhal.server.maps.kirdneh.city.MummyNPC());
		loadQuests(new ChocolateForElisabeth());
		ChildrensFriendStub.doQuestElisabeth(player);
		assertFalse(achievementReached(player, id));

		loadConfigurators(
			// Jef
			new games.stendhal.server.maps.kirdneh.city.GossipNPC(),
			// Amber
			new games.stendhal.server.maps.fado.forest.OldWomanNPC());
		loadQuests(new FindJefsMom());
		ChildrensFriendStub.doQuestJef(player);
		assertFalse(achievementReached(player, id));

		// Anastasia
		loadConfigurators(new games.stendhal.server.maps.ados.farmhouse.MotherNPC());
		addZones("int_ados_farm_house_1");
		loadQuests(new FishSoupForHughie());
		ChildrensFriendStub.doQuestHughie(player);
		assertFalse(achievementReached(player, id));

		// Finn Farmer
		loadConfigurators(new games.stendhal.server.maps.ados.wall.HolidayingBoyNPC());
		loadQuests(new CodedMessageFromFinnFarmer());
		ChildrensFriendStub.doQuestFinn(player);
		assertFalse(achievementReached(player, id));

		// Marianne
		loadConfigurators(new games.stendhal.server.maps.deniran.cityoutside.LittleGirlNPC());
		loadQuests(new EggsForMarianne());
		ChildrensFriendStub.doQuestMarianne(player);

		assertTrue(achievementReached(player, id));
	}

	@Test
	public void testPrivateDetective() {
		final String id = "friend.quests.find";
		assertFalse(achievementReached(player, id));

		loadConfigurators(
			// Agnus
			new games.stendhal.server.maps.ratcity.house1.OldRatWomanNPC(),
			// Opal
			new games.stendhal.server.maps.orril.dungeon.RatChild1NPC(),
			// Mariel
			new games.stendhal.server.maps.orril.dungeon.RatChild2NPC(),
			// Cody
			new games.stendhal.server.maps.orril.dungeon.RatChildBoy1NPC(),
			// Avalon
			new games.stendhal.server.maps.orril.dungeon.RatChildBoy2NPC());
		loadQuests(new FindRatChildren());
		PrivateDetectiveStub.doQuestAgnus(player);
		assertFalse(achievementReached(player, id));

		loadConfigurators(
			// Carena
			new games.stendhal.server.maps.ados.hauntedhouse.WomanGhostNPC(),
			// Mary
			new games.stendhal.server.maps.athor.cave.GhostNPC(),
			// Ben
			new games.stendhal.server.maps.ados.city.KidGhostNPC(),
			// Zak
			new games.stendhal.server.maps.wofol.house5.GhostNPC(),
			// Goran
			new games.stendhal.server.maps.orril.dungeon.GhostNPC());
		loadQuests(new FindGhosts());
		PrivateDetectiveStub.doQuestCarena(player);
		assertFalse(achievementReached(player, id));

		addZones("0_semos_village_w", "0_nalwor_city", "0_orril_river_s",
			"0_orril_river_s_w2", "0_orril_mountain_w2", "0_semos_mountain_n2_w2",
			"0_ados_rock");
		loadQuests(new SevenCherubs());
		PrivateDetectiveStub.doQuestCherubs(player);
		assertFalse(achievementReached(player, id));

		loadConfigurators(
			// Jef
			new games.stendhal.server.maps.kirdneh.city.GossipNPC(),
			// Amber
			new games.stendhal.server.maps.fado.forest.OldWomanNPC());
		loadQuests(new FindJefsMom());
		PrivateDetectiveStub.doQuestJef(player);
		assertFalse(achievementReached(player, id));

		loadConfigurators(
			// Niall Breland
			new games.stendhal.server.maps.deniran.cityinterior.brelandhouse.GrandsonNPC(),
			// Elias Breland
			new games.stendhal.server.maps.deniran.cityinterior.brelandhouse.GrandfatherNPC(),
			// Marianne
			new games.stendhal.server.maps.deniran.cityoutside.LittleGirlNPC(),
			// Father Calenus
			new games.stendhal.server.maps.ados.church.PriestNPC());
		addZones("-1_myling_well");
		loadQuests(new AGrandfathersWish());
		PrivateDetectiveStub.doQuestNiall(player);

		assertTrue(achievementReached(player, id));
	}

	@Test
	public void testGoodSamaritan() {
		final String id = "friend.karma.250";
		assertEquals(10, player.getKarma(), 0);

		// Mayor Chalmers
		loadConfigurators(new games.stendhal.server.maps.ados.townhall.MayorNPC());
		loadQuests(new games.stendhal.server.maps.quests.DailyItemQuest());
		while (player.getKarma() < 251) {
			assertFalse(achievementReached(player, id));
			doDailyMonsterQuest();
		}
		assertTrue(achievementReached(player, id));
	}

	private void doDailyMonsterQuest() {
		final String questSlot = "daily_item";

		final SpeakerNPC mayor = npcs.get("Mayor Chalmers");
		assertNotNull(mayor);
		final Engine en = mayor.getEngine();

		String questState = player.getQuest(questSlot, 0);
		if (questState != null && questState.equals("done")) {
			player.setQuest(questSlot, 1, "0");
		}

		en.step(player, "hi");
		en.step(player, "quest");

		questState = player.getQuest(questSlot, 0);
		assertNotNull(questState);
		final String itemName = questState.split("=")[0];
		final int quantity = Integer.parseInt(questState.split("=")[1]);

		if (quantity > 1) {
			equipWithStackableItem(player, itemName, quantity);
		} else {
			equipWithItem(player, itemName);
		}

		en.step(player, "hi");
		en.step(player, "done");
		en.step(player, "bye");
		// not called internally from Daily Monster quest
		an.onFinishQuest(player);

		assertEquals("done", player.getQuest(questSlot, 0));
	}

	@Test
	public void testStillBelieving() {
		final String id = "friend.meet.seasonal";
		assertFalse(achievementReached(player, id));

		addZones("int_admin_playground");
		System.setProperty("stendhal.easterbunny", "");
		System.setProperty("stendhal.santa", "");
		loadQuests(new MeetBunny(), new MeetSanta());
		StillBelievingStub.doQuestBunny(player);
		assertFalse(achievementReached(player, id));
		StillBelievingStub.doQuestSanta(player);

		assertTrue(achievementReached(player, id));
	}
}
