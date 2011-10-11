package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rp.StendhalQuestSystem;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.fado.city.SmallBoyNPC;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import static utilities.SpeakerNPCTestHelper.getReply;

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
		
		npc = SingletonRepository.getNPCList().get("Bobby");
		en = npc.getEngine();
		Outfit outfitNoBalloon = new Outfit(0,1,2,3,4);
		Outfit outfitWithBalloon = new Outfit(1,2,3,4,5);

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
		assertEquals("You don't even have a balloon for me :(", getReply(npc));
		en.step(player, "quest");
		assertEquals("Would you get me a #balloon? Unless the mine town weeks are currently on, then I can get my own :)", getReply(npc));
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
		assertEquals("Would you get me a #balloon? Unless the mine town weeks are currently on, then I can get my own :)", getReply(npc));
		en.step(player, "bye");
		assertEquals("Good bye.", getReply(npc));
		
		
		// Player HAS balloon; NO Mine Town Weeks
		// Player says "No" first
		player.setOutfit(outfitWithBalloon);
		
		en.step(player, "hi");
		assertEquals("Hello, is that balloon for me?", getReply(npc));
		en.step(player, "no");
		assertEquals("*pouts*", getReply(npc));
		en.step(player, "help");
		assertEquals("I wonder if a #balloon could fly high enough to touch the clouds...", getReply(npc));
		en.step(player, "job");
		assertEquals("A Job? Is that something you can eat?", getReply(npc));
		en.step(player, "quest");
		assertEquals("Would you get me a #balloon? Unless the mine town weeks are currently on, then I can get my own :)", getReply(npc));
		en.step(player, "balloon");
		assertEquals("Is that balloon for me?", getReply(npc));
		en.step(player, "no");
		assertEquals("*pouts*", getReply(npc));
		en.step(player, "balloon");
		assertEquals("Is that balloon for me?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Yippie! Fly balloon! Fly!", getReply(npc));
		en.step(player, "balloon");
		assertEquals("You don't even have a balloon for me :(", getReply(npc));
		en.step(player, "bye");
		assertEquals("Good bye.", getReply(npc));
		
		assertTrue(player.isQuestCompleted(questSlot));
		
		// Mine Town weeks are on: it should not matter if player has a balloon or not
		StendhalQuestSystem.get().loadQuest(new MineTownRevivalWeeks());
		
		// Player HAS balloon; Mine Town Weeks ARE ON
		player.setOutfit(outfitWithBalloon);
		
		en.step(player, "hi");
		assertEquals("Hm?", getReply(npc));
		en.step(player, "help");
		assertEquals("I wonder if a #balloon could fly high enough to touch the clouds...", getReply(npc));
		en.step(player, "quest");
		assertEquals("Would you get me a #balloon? Unless the mine town weeks are currently on, then I can get my own :)", getReply(npc));
		en.step(player, "balloon");
		assertEquals("The clouds told me that the mine town weeks are still going - I can get my own balloons. Come back when mine town weeks are over :)", getReply(npc));
		en.step(player, "bye");
		assertEquals("Good bye.", getReply(npc));
		
		StendhalQuestSystem.get().unloadQuest(MineTownRevivalWeeks.QUEST_NAME);
		
		
		// Player HAS NO balloon; Mine Town Weeks ARE ON
		player.setOutfit(outfitNoBalloon);
		StendhalQuestSystem.get().loadQuest(new MineTownRevivalWeeks());
		
		en.step(player, "hi");
		assertEquals("Hm?", getReply(npc));
		en.step(player, "help");
		assertEquals("I wonder if a #balloon could fly high enough to touch the clouds...", getReply(npc));
		en.step(player, "quest");
		assertEquals("Would you get me a #balloon? Unless the mine town weeks are currently on, then I can get my own :)", getReply(npc));
		en.step(player, "balloon");
		assertEquals("The clouds told me that the mine town weeks are still going - I can get my own balloons. Come back when mine town weeks are over :)", getReply(npc));
		en.step(player, "bye");
		assertEquals("Good bye.", getReply(npc));

		StendhalQuestSystem.get().unloadQuest(MineTownRevivalWeeks.QUEST_NAME);
	}
}
