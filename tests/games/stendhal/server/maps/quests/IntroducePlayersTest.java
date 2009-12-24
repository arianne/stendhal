package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.semos.townhall.BoyNPC;
import marauroa.common.Log4J;
import marauroa.common.game.RPObject.ID;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.ZonePlayerAndNPCTestImpl;
import utilities.RPClass.ItemTestHelper;

public class IntroducePlayersTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "testzone";

	private static final String INTRODUCE_PLAYERS = "introduce_players";
	private static final String SSSHH_COME_HERE = "Ssshh! Come here, player! I have a #task for you.";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();

		generatePlayerRPClasses();
		ItemTestHelper.generateRPClasses();

		MockStendhalRPRuleProcessor.get();
		MockStendlRPWorld.get();

		setupZone(ZONE_NAME, new BoyNPC());

		SingletonRepository.getNPCList().add(new SpeakerNPC("Ilisa"));

		new IntroducePlayers().addToWorld();
	}

	public IntroducePlayersTest() {
		super(ZONE_NAME, "Tad", "Ilisa");
	}

	/**
	 * Tests for hiAndbye.
	 */
	@Test
	public void testHiAndbye() {
		final SpeakerNPC npc = getNPC("Tad");
		final Engine en = npc.getEngine();
		en.step(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertTrue(npc.isTalking());
		assertEquals(IntroducePlayersTest.SSSHH_COME_HERE, getReply(npc));
		en.step(player, "task");
		assertTrue(npc.isTalking());
		assertEquals(
				"I'm not feeling well... I need to get a bottle of medicine made. Can you fetch me an empty #flask?",
				getReply(npc));
		en.step(player, "flask");
		assertTrue(npc.isTalking());
		assertEquals("You could probably get a flask from #Margaret.", getReply(npc));
		en.step(player, ConversationPhrases.GOODBYE_MESSAGES.get(0));
		assertFalse(npc.isTalking());
		assertEquals("Bye.", getReply(npc));
	}

	/**
	 * Tests for hiNoAndHiAgain.
	 */
	@Test
	public void testHiNoAndHiAgain() {
		final SpeakerNPC npc = getNPC("Tad");
		final Engine en = npc.getEngine();
		en.step(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertTrue(npc.isTalking());

		en.step(player, "task");
		assertTrue(npc.isTalking());
		assertEquals(
				"I'm not feeling well... I need to get a bottle of medicine made. Can you fetch me an empty #flask?",
				getReply(npc));
		en.step(player, "No");
		assertTrue(npc.isTalking());
		assertEquals("Oh, please won't you change your mind? *sneeze*", getReply(npc));
		en.step(player, ConversationPhrases.GOODBYE_MESSAGES.get(0));
		assertFalse(npc.isTalking());
		assertFalse(player.hasQuest(IntroducePlayersTest.INTRODUCE_PLAYERS));
		assertEquals("Bye.", getReply(npc));
		en.step(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertTrue(npc.isTalking());
		assertEquals(IntroducePlayersTest.SSSHH_COME_HERE, getReply(npc));
		en.step(player, ConversationPhrases.GOODBYE_MESSAGES.get(0));
	}

	/**
	 * Tests for quest.
	 */
	@Test
	public void testQuest() {
		final SpeakerNPC tad = getNPC("Tad");
		final Engine engineTad = tad.getEngine();
		engineTad.step(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertTrue(tad.isTalking());
		assertEquals("Ssshh! Come here, player! I have a #task for you.", getReply(tad));
		engineTad.step(player, "task");
		assertTrue(tad.isTalking());
		assertEquals(
				"I'm not feeling well... I need to get a bottle of medicine made. Can you fetch me an empty #flask?",
				getReply(tad));
		engineTad.step(player, "yes");
		assertTrue(player.hasQuest(IntroducePlayersTest.INTRODUCE_PLAYERS));
		engineTad.step(player, ConversationPhrases.GOODBYE_MESSAGES.get(0));
		assertFalse(tad.isTalking());
		assertEquals("Bye.", getReply(tad));

		final StackableItem flask = new StackableItem("flask", "", "", null);
		flask.setQuantity(1);
		flask.setID(new ID(2, ZONE_NAME));
		player.getSlot("bag").add(flask);
		assertTrue(player.isEquipped("flask"));
		engineTad.step(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertTrue(tad.isTalking());
		assertEquals(
				"Ok, you got the flask! Now, I need you to take it to #ilisa... she'll know what to do next.",
				getReply(tad));
		assertTrue(player.hasQuest(IntroducePlayersTest.INTRODUCE_PLAYERS));
		assertEquals("ilisa", player.getQuest(IntroducePlayersTest.INTRODUCE_PLAYERS));
		engineTad.step(player, ConversationPhrases.GOODBYE_MESSAGES.get(0));

		final SpeakerNPC ilisa = getNPC("Ilisa");
		final Engine engineIlisa = ilisa.getEngine();
		engineIlisa.step(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertEquals(
				"Ah, I see you have that flask. #Tad needs medicine, right? Hmm... I'll need a #herb. Can you help?",
				getReply(ilisa));
		engineIlisa.step(player, "yes");
		assertEquals("corpse&herbs", player.getQuest(IntroducePlayersTest.INTRODUCE_PLAYERS));
		engineIlisa.step(player, ConversationPhrases.GOODBYE_MESSAGES.get(0));

		engineTad.step(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertTrue(tad.isTalking());

		assertEquals("Tad has already asked and the quest was accepted",
				"*sniff* *sniff* I still feel ill, please hurry with that #favour for me.", getReply(tad));

		//TODO still to be finished: get the herb for Ilisa
	}

}
