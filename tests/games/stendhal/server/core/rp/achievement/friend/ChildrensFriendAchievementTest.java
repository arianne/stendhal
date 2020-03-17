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

import static games.stendhal.server.core.rp.achievement.factory.FriendAchievementFactory.ID_CHILD_FRIEND;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rp.StendhalQuestSystem;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.ados.city.KidsNPCs;
import games.stendhal.server.maps.ados.farmhouse.MotherNPC;
import games.stendhal.server.maps.ados.wall.HolidayingBoyNPC;
import games.stendhal.server.maps.fado.forest.OldWomanNPC;
import games.stendhal.server.maps.kirdneh.city.GossipNPC;
import games.stendhal.server.maps.orril.river.CampingGirlNPC;
import games.stendhal.server.maps.quests.Campfire;
import games.stendhal.server.maps.quests.ChocolateForElisabeth;
import games.stendhal.server.maps.quests.CodedMessageFromFinnFarmer;
import games.stendhal.server.maps.quests.EggsForMarianne;
import games.stendhal.server.maps.quests.FindJefsMom;
import games.stendhal.server.maps.quests.FishSoupForHughie;
import games.stendhal.server.maps.quests.IcecreamForAnnie;
import games.stendhal.server.maps.quests.MedicineForTad;
import games.stendhal.server.maps.quests.MineTownRevivalWeeks;
import games.stendhal.server.maps.quests.PlinksToy;
import games.stendhal.server.maps.quests.ToysCollector;
import games.stendhal.server.maps.semos.plains.LittleBoyNPC;
import games.stendhal.server.maps.semos.temple.HealerNPC;
import games.stendhal.server.maps.semos.townhall.DecencyAndMannersWardenNPC;
import marauroa.server.game.db.DatabaseFactory;
import utilities.AchievementTestHelper;
import utilities.PlayerTestHelper;
import utilities.ZonePlayerAndNPCTestImpl;


public class ChildrensFriendAchievementTest extends ZonePlayerAndNPCTestImpl {

	private Player player;

	private static final StendhalRPWorld world = MockStendlRPWorld.get();
	private static final StendhalQuestSystem questSystem = StendhalQuestSystem.get();

	private static final NPCList npcs = SingletonRepository.getNPCList();

	private final String[] questSlots = {
			"susi", "introduce_players", "plinks_toy", "toys_collector", "campfire",
			"icecream_for_annie", "chocolate_for_elisabeth", "find_jefs_mom",
			"fishsoup_for_hughie", "coded_message", "eggs_for_marianne"
	};


	@BeforeClass
	public static void setUpBeforeClass() {
		new DatabaseFactory().initializeDatabase();
	}

	@Override
	@Before
	public void setUp() throws Exception {
		final Map<String, ZoneConfigurator> configurators = new HashMap<>();
		configurators.put("Susi", new games.stendhal.server.maps.ados.rosshouse.LittleGirlNPC());
		configurators.put("Tad", new games.stendhal.server.maps.semos.hostel.BoyNPC());
		configurators.put("Ilisa", new HealerNPC());
		configurators.put("Ketteh Wehoh", new DecencyAndMannersWardenNPC());
		configurators.put("Plink", new LittleBoyNPC());
		configurators.put("Anna", new KidsNPCs());
		configurators.put("Sally", new CampingGirlNPC());
		configurators.put("Annie Jones", new games.stendhal.server.maps.kalavan.citygardens.LittleGirlNPC());
		configurators.put("Mrs Jones", new games.stendhal.server.maps.kalavan.citygardens.MummyNPC());
		configurators.put("Elisabeth", new games.stendhal.server.maps.kirdneh.city.LittleGirlNPC());
		configurators.put("Carey", new games.stendhal.server.maps.kirdneh.city.MummyNPC());
		configurators.put("Jef", new GossipNPC());
		configurators.put("Amber", new OldWomanNPC());
		//configurators.put("Hughie", new games.stendhal.server.maps.ados.farmhouse.BoyNPC());
		configurators.put("Anastasia", new MotherNPC());
		configurators.put("Finn Farmer", new HolidayingBoyNPC());
		configurators.put("Marianne", new games.stendhal.server.maps.deniran.cityoutside.LittleGirlNPC());

		final String zoneName = "testzone";
		for (final ZoneConfigurator zc: configurators.values()) {
			addZoneConfigurator(zc, zoneName);
		}

		Set<String> allNPCs = new HashSet<>();
		allNPCs.addAll(configurators.keySet());
		allNPCs.add("George"); // games.stendhal.server.maps.ados.city.KidsNPCs

		// zones required for Susi's quest
		world.addRPZone("none", new StendhalRPZone("int_semos_frank_house"));
		world.addRPZone("none", new StendhalRPZone("0_semos_mountain_n2"));
		// zone required for Plink's Toy quest
		world.addRPZone("none", new StendhalRPZone("0_semos_plains_n"));
		// zone required for Fish Soup for Hughie quest
		world.addRPZone("none", new StendhalRPZone("int_ados_farm_house_1"));

		setNpcNames(allNPCs.toArray(new String[0]));
		zone = setupZone(zoneName);
		setZoneForPlayer(zoneName);

		super.setUp();

		// load quests
		questSystem.loadQuest(new MineTownRevivalWeeks());
		questSystem.loadQuest(new MedicineForTad());
		questSystem.loadQuest(new PlinksToy());
		questSystem.loadQuest(new ToysCollector());
		questSystem.loadQuest(new Campfire());
		questSystem.loadQuest(new IcecreamForAnnie());
		questSystem.loadQuest(new ChocolateForElisabeth());
		questSystem.loadQuest(new FindJefsMom());
		questSystem.loadQuest(new FishSoupForHughie());
		questSystem.loadQuest(new CodedMessageFromFinnFarmer());
		questSystem.loadQuest(new EggsForMarianne());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		PlayerTestHelper.removeAllPlayers();
	}

	@Test
	public void init() {
		resetPlayer();

		doQuestSusi();
		assertFalse(achievementReached());
		doQuestTad();
		assertFalse(achievementReached());
		doQuestPlink();
		assertFalse(achievementReached());
		doQuestAnna();
		assertFalse(achievementReached());
		doQuestSally();
		assertFalse(achievementReached());
		doQuestAnnie();
		assertFalse(achievementReached());
		doQuestElisabeth();
		assertFalse(achievementReached());
		doQuestJef();
		assertFalse(achievementReached());
		doQuestHughie();
		assertFalse(achievementReached());
		doQuestFinn();
		assertFalse(achievementReached());
		doQuestMarianne();

		assertTrue(achievementReached());
	}

	private boolean achievementReached() {
		return AchievementTestHelper.achievementReached(player, ID_CHILD_FRIEND);
	}

	private void resetPlayer() {
		player = PlayerTestHelper.createPlayer("player");
		assertNotNull(player);

		for (final String quest: questSlots) {
			assertNull(player.getQuest(quest));
		}

		AchievementTestHelper.init(player);
		assertFalse(achievementReached());
	}

	private void doQuestSusi() {
		final String questSlot = "susi";
		assertNull(player.getQuest(questSlot));

		final SpeakerNPC susi = npcs.get("Susi");
		assertNotNull(susi);

		final Engine en = susi.getEngine();

		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "friend");
		en.step(player, "A circle is round,");
		en.step(player, "it has no end.");
		en.step(player, "That's how long,");
		en.step(player, "I will be your friend.");
		en.step(player, "bye");
	}

	private void doQuestTad() {
		final String questSlot = "introduce_players";
		assertNull(player.getQuest(questSlot));

		final SpeakerNPC tad = npcs.get("Tad");
		final SpeakerNPC ilisa = npcs.get("Ilisa");
		assertNotNull(tad);
		assertNotNull(ilisa);

		Engine en = tad.getEngine();

		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "yes");
		en.step(player, "bye");

		PlayerTestHelper.equipWithItem(player, "flask");

		en.step(player, "hi");
		en.step(player, "bye");

		en = ilisa.getEngine();

		en.step(player, "hi");
		en.step(player, "yes");
		en.step(player, "bye");

		PlayerTestHelper.equipWithItem(player, "arandula");

		en.step(player, "hi");
		en.step(player, "bye");

		en = tad.getEngine();

		en.step(player, "hi");
		en.step(player, "bye");

		assertEquals("done", player.getQuest(questSlot, 0));
	}

	private void doQuestPlink() {
		final String questSlot = "plinks_toy";
		assertNull(player.getQuest(questSlot));

		final SpeakerNPC plink = npcs.get("Plink");
		assertNotNull(plink);

		final Engine en = plink.getEngine();

		en.step(player, "hi");
		en.step(player, "yes");

		// don't need to say "bye"
		assertEquals(ConversationStates.IDLE, en.getCurrentState());

		PlayerTestHelper.equipWithItem(player, "teddy");

		en.step(player, "hi");
		en.step(player, "bye");

		assertEquals("done", player.getQuest(questSlot, 0));
	}

	private void doQuestAnna() {
		final String questSlot = "toys_collector";
		assertNull(player.getQuest(questSlot));

		final SpeakerNPC anna = npcs.get("Anna");
		assertNotNull(anna);

		final Engine en = anna.getEngine();

		en.step(player, "hi");
		en.step(player, "toys");
		en.step(player, "yes");

		// don't need to say "bye"
		assertEquals(ConversationStates.IDLE, en.getCurrentState());

		en.step(player, "hi");
		en.step(player, "yes");

		for (final String toy: Arrays.asList("teddy", "dice", "dress")) {
			PlayerTestHelper.equipWithItem(player, toy);
			en.step(player, toy);
		}

		en.step(player, "bye");

		assertEquals("done", player.getQuest(questSlot, 0));
	}

	private void doQuestSally() {
		final String questSlot = "campfire";
		assertNull(player.getQuest(questSlot));

		final SpeakerNPC sally = npcs.get("Sally");
		assertNotNull(sally);

		final Engine en = sally.getEngine();

		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "yes");
		en.step(player, "bye");

		PlayerTestHelper.equipWithStackableItem(player, "wood", 10);

		en.step(player, "hi");
		en.step(player, "yes");
		en.step(player, "bye");

		assertNotNull(player.getQuest(questSlot));
		assertNotEquals("start", player.getQuest(questSlot, 0));
	}

	private void doQuestAnnie() {
		final String questSlot = "icecream_for_annie";
		assertNull(player.getQuest(questSlot));

		final SpeakerNPC annie = npcs.get("Annie Jones");
		final SpeakerNPC mrsjones = npcs.get("Mrs Jones");
		assertNotNull(annie);
		assertNotNull(mrsjones);

		Engine en = annie.getEngine();

		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "yes");
		en.step(player, "bye");

		en = mrsjones.getEngine();

		en.step(player, "hi");
		en.step(player, "bye");

		en = annie.getEngine();

		PlayerTestHelper.equipWithItem(player, "icecream");

		en.step(player, "hi");
		en.step(player, "yes");
		en.step(player, "bye");

		assertEquals("eating", player.getQuest(questSlot, 0));
	}

	private void doQuestElisabeth() {
		final String questSlot = "chocolate_for_elisabeth";
		assertNull(player.getQuest(questSlot));

		final SpeakerNPC elisabeth = npcs.get("Elisabeth");
		final SpeakerNPC carey = npcs.get("Carey");
		assertNotNull(elisabeth);
		assertNotNull(carey);

		Engine en = elisabeth.getEngine();

		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "yes");
		en.step(player, "bye");

		en = carey.getEngine();

		en.step(player, "hi");
		en.step(player, "bye");

		PlayerTestHelper.equipWithItem(player, "chocolate bar");

		en = elisabeth.getEngine();

		en.step(player, "hi");
		en.step(player, "yes");
		en.step(player, "bye");

		assertEquals("eating", player.getQuest(questSlot, 0));
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

		en = amber.getEngine();

		en.step(player, "hi");
		en.step(player, "Jef");

		// don't need to say "bye"
		assertEquals(ConversationStates.IDLE, en.getCurrentState());

		en = jef.getEngine();

		en.step(player, "hi");
		en.step(player, "fine");
		en.step(player, "bye");

		assertEquals("done", player.getQuest(questSlot, 0));
	}

	private void doQuestHughie() {
		final String questSlot = "fishsoup_for_hughie";
		assertNull(player.getQuest(questSlot));

		//final SpeakerNPC hughie = npcs.get("Hughie");
		final SpeakerNPC anastasia = npcs.get("Anastasia");
		//assertNotNull(hughie);
		assertNotNull(anastasia);

		final Engine en = anastasia.getEngine();

		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "yes");
		en.step(player, "bye");

		PlayerTestHelper.equipWithItem(player, "fish soup");

		en.step(player, "hi");
		en.step(player, "yes");
		en.step(player, "bye");

		assertNotNull(player.getQuest(questSlot));
		assertNotEquals("start", player.getQuest(questSlot, 0));
	}

	private void doQuestFinn() {
		final String questSlot = "coded_message";
		assertNull(player.getQuest(questSlot));

		final SpeakerNPC finn = npcs.get("Finn Farmer");
		final SpeakerNPC george = npcs.get("George");
		assertNotNull(finn);
		assertNotNull(george);

		Engine en = finn.getEngine();

		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "yes");

		String message = getReply(finn);

		en.step(player, "bye");

		en = george.getEngine();

		en.step(player, "hi");
		en.step(player, message);

		message = getReply(george);

		// don't need to say "bye"
		assertEquals(ConversationStates.IDLE, en.getCurrentState());

		en = finn.getEngine();

		en.step(player, "hi");
		en.step(player, message);
		en.step(player, "bye");

		assertEquals("done", player.getQuest(questSlot, 0));
	}

	private void doQuestMarianne() {
		final String questSlot = "eggs_for_marianne";
		assertNull(player.getQuest(questSlot));

		final SpeakerNPC marianne = npcs.get("Marianne");
		assertNotNull(marianne);

		final Engine en = marianne.getEngine();

		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "yes");
		en.step(player, "bye");

		PlayerTestHelper.equipWithStackableItem(player, "egg", 12);

		en.step(player, "hi");
		en.step(player, "yes");
		en.step(player, "bye");

		assertNotNull(player.getQuest(questSlot));
		assertNotEquals("start", player.getQuest(questSlot, 0));
	}
}
