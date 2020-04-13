package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.fado.city.SmallBoyNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class BalloonForBobbyTest {

	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	private String questSlot = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}

	@Before
	public void setUp() {
		final StendhalRPZone zone = new StendhalRPZone("admin_test");
		new SmallBoyNPC().configureZone(zone, null);


		AbstractQuest quest = new BalloonForBobby();
		quest.addToWorld();

		questSlot = quest.getSlotName();

		player = PlayerTestHelper.createPlayerWithOutFit("bob");
	}

	@Test
	public void testQuest() {
		// Quest not started
		player.setQuest(questSlot, null);
		runQuestDialogue();
		// Quest started
		player.setQuest(questSlot, 0, "start");
		runQuestDialogue();
		// Quest rejected
		player.setQuest(questSlot,  0, "rejected");
		runQuestDialogue();
		// Quest done
		player.setQuest(questSlot, 0, "done");
		runQuestDialogue();

		// Quest not started (during Mine Town Weeks)
		player.setQuest(questSlot, null);
		runMinetownQuestDialogue();
		// Quest started (during Mine Town Weeks)
		player.setQuest(questSlot, 0, "start");
		runMinetownQuestDialogue();
		// Quest rejected (during Mine Town Weeks)
		player.setQuest(questSlot,  0, "rejected");
		runMinetownQuestDialogue();
		// Quest done (during Mine Town Weeks)
		player.setQuest(questSlot, 0, "done");
		runMinetownQuestDialogue();
	}

	public void runQuestDialogue() {
		System.getProperties().remove("stendhal.minetown");

		npc = SingletonRepository.getNPCList().get("Bobby");
		en = npc.getEngine();
		Outfit outfitNoBalloon = new Outfit(8, 7, 6, 5, 4, 3, 2, 1, 0);
		Outfit outfitWithBalloon = new Outfit(9, 8, 7, 6, 5, 4, 3, 2, 1);

		// -----------------------------------------------

		//Not really necessary
		//assertEquals(player.getOutfit().getCode(), outfitNoBalloon.getCode());

		// Player has NO BALLOON; NO Mine Town Weeks
		player.setOutfit(outfitNoBalloon);

		en.step(player, "hi");
		assertEquals("Hm?", getReply(npc));
		en.step(player, "help");
		assertEquals("I wonder if a #balloon could fly high enough to touch the clouds...", getReply(npc));
		en.step(player, "job");
		assertEquals("A Job? Is that something you can eat?", getReply(npc));
		en.step(player, "balloon");
		if (player.getQuest(questSlot) == null || player.getQuest(questSlot, 0).equals("rejected")) {
			assertEquals("One day, i will have enough balloons to fly away!", getReply(npc));
		} else {
			assertEquals("You don't even have a balloon for me :(", getReply(npc));
		}
		en.step(player, "quest");
		if (player.getQuest(questSlot) == null || player.getQuest(questSlot, 0).equals("rejected")) {
			assertEquals("Would you get me a #balloon? Unless the mine town weeks are currently on, then I can get my own :)", getReply(npc));
		} else {
			assertEquals("I hope you can get me a #balloon soon. Unless the mine town weeks are currently on, then I can get my own :)", getReply(npc));
		}
		en.step(player, "bye");
		assertEquals("Good bye.", getReply(npc));


		// Player HAS balloon; NO Mine Town Weeks
		// Player says "Yes" straight out
		player.setOutfit(outfitWithBalloon);


		en.step(player, "hi");
		assertEquals("Hello, is that balloon for me?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Yippie! Fly balloon! Fly!", getReply(npc));
		en.step(player, "balloon");
		assertEquals("You don't even have a balloon for me :(", getReply(npc));
		en.step(player, "help");
		assertEquals("I wonder if a #balloon could fly high enough to touch the clouds...", getReply(npc));
		en.step(player, "job");
		assertEquals("A Job? Is that something you can eat?", getReply(npc));
		en.step(player, "quest");
		assertEquals("I hope you can get me a #balloon soon. Unless the mine town weeks are currently on, then I can get my own :)", getReply(npc));
		en.step(player, "bye");
		assertEquals("Good bye.", getReply(npc));


		// Player HAS balloon; NO Mine Town Weeks
		// Player says "No" first
		player.setOutfit(outfitWithBalloon);

		en.step(player, "hi");
		assertEquals("Hello, is that balloon for me?", getReply(npc));
		en.step(player, "no");
		assertEquals("!me pouts.", getReply(npc));
		en.step(player, "help");
		assertEquals("I wonder if a #balloon could fly high enough to touch the clouds...", getReply(npc));
		en.step(player, "job");
		assertEquals("A Job? Is that something you can eat?", getReply(npc));
		en.step(player, "quest");
		assertEquals("I hope you can get me a #balloon soon. Unless the mine town weeks are currently on, then I can get my own :)", getReply(npc));
		en.step(player, "balloon");
		assertEquals("Is that balloon for me?", getReply(npc));
		en.step(player, "no");
		assertEquals("!me pouts.", getReply(npc));
		en.step(player, "balloon");
		assertEquals("Is that balloon for me?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Yippie! Fly balloon! Fly!", getReply(npc));
		en.step(player, "balloon");
		assertEquals("You don't even have a balloon for me :(", getReply(npc));
		en.step(player, "bye");
		assertEquals("Good bye.", getReply(npc));

		assertTrue(player.isQuestCompleted(questSlot));
	}

	public void runMinetownQuestDialogue() {

		npc = SingletonRepository.getNPCList().get("Bobby");
		en = npc.getEngine();
		Outfit outfitNoBalloon = new Outfit(4, 3, 2, null, 0, null, 1, null, 0);
		Outfit outfitWithBalloon = new Outfit(5, 4, 3, null, 0, null, 2, null, 1);

		// Mine Town weeks are on: it should not matter if player has a balloon or not
		System.setProperty("stendhal.minetown", "true");

		// Player HAS balloon; Mine Town Weeks ARE ON
		player.setOutfit(outfitWithBalloon);

		en.step(player, "hi");
		assertEquals("Hm?", getReply(npc));
		en.step(player, "help");
		assertEquals("I wonder if a #balloon could fly high enough to touch the clouds...", getReply(npc));
		en.step(player, "quest");
		if (player.getQuest(questSlot) == null || player.getQuest(questSlot, 0).equals("rejected")) {
			assertEquals("Would you get me a #balloon? Unless the mine town weeks are currently on, then I can get my own :)", getReply(npc));
		} else {
			assertEquals("I hope you can get me a #balloon soon. Unless the mine town weeks are currently on, then I can get my own :)", getReply(npc));
		}
		en.step(player, "balloon");
		if (player.getQuest(questSlot) == null || player.getQuest(questSlot, 0).equals("rejected")) {
			assertEquals("One day, i will have enough balloons to fly away!", getReply(npc));
		} else {
			assertEquals("The clouds told me that the mine town weeks are still going - I can get my own balloons. Come back when mine town weeks are over :)", getReply(npc));
		}
		en.step(player, "bye");
		assertEquals("Good bye.", getReply(npc));

		System.getProperties().remove("stendhal.minetown");


		// Player HAS NO balloon; Mine Town Weeks ARE ON
		player.setOutfit(outfitNoBalloon);
		System.setProperty("stendhal.minetown", "true");

		en.step(player, "hi");
		assertEquals("Hm?", getReply(npc));
		en.step(player, "help");
		assertEquals("I wonder if a #balloon could fly high enough to touch the clouds...", getReply(npc));
		en.step(player, "quest");
		if (player.getQuest(questSlot) == null || player.getQuest(questSlot, 0).equals("rejected")) {
			assertEquals("Would you get me a #balloon? Unless the mine town weeks are currently on, then I can get my own :)", getReply(npc));
		} else {
			assertEquals("I hope you can get me a #balloon soon. Unless the mine town weeks are currently on, then I can get my own :)", getReply(npc));
		}
		en.step(player, "balloon");
		if (player.getQuest(questSlot) == null || player.getQuest(questSlot, 0).equals("rejected")) {
			assertEquals("One day, i will have enough balloons to fly away!", getReply(npc));
		} else {
			assertEquals("The clouds told me that the mine town weeks are still going - I can get my own balloons. Come back when mine town weeks are over :)", getReply(npc));
		}
		en.step(player, "bye");
		assertEquals("Good bye.", getReply(npc));

		System.getProperties().remove("stendhal.minetown");
	}
}
