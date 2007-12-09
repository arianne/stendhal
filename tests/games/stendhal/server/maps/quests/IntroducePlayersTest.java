package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.semos.townhall.BoyNPC;
import marauroa.common.Log4J;
import marauroa.common.game.RPObject.ID;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class IntroducePlayersTest {

	private static final String INTRODUCE_PLAYERS = "introduce_players";
	private static final String SSSHH_COME_HERE = "Ssshh! Come here, player! I have a #task for you.";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();

		PlayerTestHelper.generatePlayerRPClasses();
		PlayerTestHelper.generateItemRPClasses();

		assertTrue(MockStendhalRPRuleProcessor.get() instanceof MockStendhalRPRuleProcessor);
		MockStendlRPWorld.get();
		BoyNPC tad = new BoyNPC();
		tad.configureZone(new StendhalRPZone("testzone"), null);
		SpeakerNPC Ilisa = new SpeakerNPC("Ilisa");
		NPCList.get().add(Ilisa);
		IntroducePlayers ip = new IntroducePlayers();
		ip.addToWorld();
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testHiAndbye() {
		Player player;
		player = PlayerTestHelper.createPlayer();

		SpeakerNPC npc = NPCList.get().get("Tad");
		assertNotNull(npc);
		Engine en = npc.getEngine();
		en.step(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertTrue(npc.isTalking());
		assertEquals(IntroducePlayersTest.SSSHH_COME_HERE, npc
				.get("text"));
		en.step(player, "task");
		assertTrue(npc.isTalking());
		assertEquals(
				"I'm not feeling well... I need to get a bottle of medicine made. Can you fetch me an empty #flask?",
				npc.get("text"));
		en.step(player, "flask");
		assertTrue(npc.isTalking());
		assertEquals("You could probably get a flask from #Margaret.", npc
				.get("text"));
		en.step(player, ConversationPhrases.GOODBYE_MESSAGES.get(0));
		assertFalse(npc.isTalking());
		assertEquals("Bye.", npc.get("text"));
	}

	@Test
	public void testHiNoAndHiAgain() {
		Player player;
		player = PlayerTestHelper.createPlayer();


		SpeakerNPC npc = NPCList.get().get("Tad");
		assertNotNull(npc);
		Engine en = npc.getEngine();
		en.step(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertTrue(npc.isTalking());

		en.step(player, "task");
		assertTrue(npc.isTalking());
		assertEquals(
				"I'm not feeling well... I need to get a bottle of medicine made. Can you fetch me an empty #flask?",
				npc.get("text"));
		en.step(player, "No");
		assertTrue(npc.isTalking());
		assertEquals("Oh, please won't you change your mind? *sneeze*", npc
				.get("text"));
		en.step(player, ConversationPhrases.GOODBYE_MESSAGES.get(0));
		assertFalse(npc.isTalking());
		assertFalse(player.hasQuest(IntroducePlayersTest.INTRODUCE_PLAYERS));
		assertEquals("Bye.", npc.get("text"));
		en.step(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertTrue(npc.isTalking());
		assertEquals(IntroducePlayersTest.SSSHH_COME_HERE, npc
				.get("text"));
		en.step(player, ConversationPhrases.GOODBYE_MESSAGES.get(0));
	}

	@Test
	@Ignore
	public void testQuest() {
		Player player = PlayerTestHelper.createPlayer();
		SpeakerNPC tad = NPCList.get().get("Tad");
		assertNotNull(tad);
		Engine engineTad = tad.getEngine();
		engineTad.step(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertTrue(tad.isTalking());
		assertEquals("Ssshh! Come here, player! I have a #task for you.", tad
				.get("text"));
		engineTad.step(player, "task");
		assertTrue(tad.isTalking());
		assertEquals(
				"I'm not feeling well... I need to get a bottle of medicine made. Can you fetch me an empty #flask?",
				tad.get("text"));
		engineTad.step(player, "yes");
		assertTrue(player.hasQuest(IntroducePlayersTest.INTRODUCE_PLAYERS));
		engineTad.step(player, ConversationPhrases.GOODBYE_MESSAGES.get(0));
		assertFalse(tad.isTalking());
		assertEquals("Bye.", tad.get("text"));

		StackableItem flask = new StackableItem("flask", "", "", null);
		flask.setQuantity(1);
		flask.setID(new ID(2, "testzone"));
		player.getSlot("bag").add(flask);
		assertTrue(player.isEquipped("flask"));
		engineTad.step(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertTrue(tad.isTalking());
		assertEquals(
				"Ok, you got the flask! Now, I need you to take it to #ilisa... she'll know what to do next.",
				tad.get("text"));
		assertTrue(player.hasQuest(IntroducePlayersTest.INTRODUCE_PLAYERS));
		assertEquals("ilisa", player.getQuest(IntroducePlayersTest.INTRODUCE_PLAYERS));
		engineTad.step(player, ConversationPhrases.GOODBYE_MESSAGES.get(0));

		SpeakerNPC ilisa = NPCList.get().get("Ilisa");
		assertNotNull(ilisa);
		Engine engineIlisa = ilisa.getEngine();
		engineIlisa.step(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertEquals(
				"Ah, I see you have that flask. #Tad needs medicine, right? Hmm... I'll need a few #herbs. Can you help?",
				ilisa.get("text"));
		engineIlisa.step(player, "yes");
		assertEquals("corpse&herbs", player.getQuest(IntroducePlayersTest.INTRODUCE_PLAYERS));
		engineIlisa.step(player, ConversationPhrases.GOODBYE_MESSAGES.get(0));

		engineTad.step(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertTrue(tad.isTalking());

		assertEquals("Tad has already asked and the quest was accepted",
				"*sniff* *sniff* I still feel ill, please hurry", tad
						.get("text"));

	}

}
