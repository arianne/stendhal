package games.stendhal.server.maps.quests;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.common.Grammar;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class CloakCollector2Test {
	private static final String NPC = "Josephine";
	private static final String QUEST_NAME = "cloaks_collector_2";
	private static final String OLD_QUEST = "cloaks_collector";
	private static final List<String> CLOAKS = Arrays.asList("red cloak", 
		"shadow cloak", "xeno cloak",  "elvish cloak", "chaos cloak", 
		"mainio cloak", "golden cloak", "black dragon cloak");
	
	private static final List<String> missingCloaks(Player player) {
		String done = player.getQuest(QUEST_NAME);
		List<String> needed = new LinkedList<String>(CLOAKS);
		List<String> colored = new LinkedList<String>();

		if (done == null) {
			done = "";
		}
		
		needed.removeAll(Arrays.asList(done.split(";")));
		for (String cloak : needed) {
			colored.add("#" + cloak);
		}
		
		return colored;
	}
	
	private static final String initiallyWantedMessage(Player player) {
		List<String> needed = missingCloaks(player);
		
		return "It's missing "
			+ Grammar.quantityplnoun(needed.size(), "cloak")
			+ ". That's " + Grammar.enumerateCollection(needed)
			+ ". Will you find them?";
	}
	
	private static final String stillWantedMessage(Player player) {
		List<String> needed = missingCloaks(player);
		
		return ("I want " + Grammar.quantityplnoun(needed.size(), "cloak")
			+ ". That's " + Grammar.enumerateCollection(needed)
			+ ". Did you bring any?");
	}
	
	@BeforeClass
	public static void setupBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		SingletonRepository.getNPCList().remove(NPC);
	}
	
	@Test
	public final void missingPreviousQuest() {		
		SingletonRepository.getNPCList().add(new SpeakerNPC(NPC));
		CloakCollector2 cc = new CloakCollector2();
		cc.addToWorld();
		SpeakerNPC npc = cc.npcs.get(NPC);
		Engine en = npc.getEngine();
		Player player = PlayerTestHelper.createPlayer("player");
		
		/* 
		 * Josephine should have nothing to say to us, unless we have completed
		 * cloaks_collector quest. Those people would be getting the answer from
		 *  that quest.
		 */
		en.stepTest(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertEquals("Josephines answer to non cloak1 people", null, npc.get("text"));		
	}

	@Test
	public final void rejectQuest() {
		SingletonRepository.getNPCList().add(new SpeakerNPC(NPC));
		CloakCollector2 cc = new CloakCollector2();
		cc.addToWorld();
		SpeakerNPC npc = cc.npcs.get(NPC);
		Engine en = npc.getEngine();
		Player player = PlayerTestHelper.createPlayer("player");
		double karma = player.getKarma();
		
		// CloakCollector needs to be done to start this quest
		player.setQuest(OLD_QUEST, "done");
		en.stepTest(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertEquals("Josephines first greeting",  "Hi again! I hear there's some new cloaks out, and I'm regretting not asking you about the ones I didn't like before. It feels like my #collection isn't complete...", npc.get("text"));
		
		en.stepTest(player, "no");
		assertEquals("Answer to refusal", "Oh ... you're not very friendly. Please say yes?", npc.get("text"));
		assertEquals("Karma penalty at refusal", karma - 5.0, player.getKarma(), 0.01);
	}
	
	@Test
	public final void doQuest() {
		
		SingletonRepository.getNPCList().add(new SpeakerNPC(NPC));
		CloakCollector2 cc = new CloakCollector2();
		cc.addToWorld();
		SpeakerNPC npc = cc.npcs.get(NPC);
		Engine en = npc.getEngine();
		Player player = PlayerTestHelper.createPlayer("player");
		double karma = player.getKarma();
		
		player.setQuest(OLD_QUEST, "done");

		en.stepTest(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertEquals("Hi again! I hear there's some new cloaks out, and I'm regretting not asking you about the ones I didn't like before. It feels like my #collection isn't complete...", npc.get("text"));
		
		en.stepTest(player, "collection");
		assertEquals("Answer to 'collection'", 
				initiallyWantedMessage(player), npc.get("text"));
		
		for (String item : CLOAKS) {
			en.stepTest(player, item);
			String expected = "You haven't seen one before? Well, it's a "
				+ item 
				+ ". Sorry if that's not much help, it's all I know! So, will you find them all?";
			assertEquals(expected, npc.get("text"));
		}
		
		// does not exist
		en.stepTest(player, "pink cloak");
		assertEquals(null, npc.get("text"));

		en.stepTest(player, ConversationPhrases.YES_MESSAGES.get(0));
		assertEquals("Brilliant! I'm all excited again! Bye!", npc.get("text"));
		assertEquals(karma + 5.0, player.getKarma(), 0.01);
		
		en.stepTest(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertEquals("Welcome back! Have you brought any #cloaks with you?", npc.get("text"));
		
		en.stepTest(player, "cloaks");
		assertEquals(stillWantedMessage(player), npc.get("text"));
		
		en.stepTest(player, "no");
		assertEquals("Okay then. Come back later.", npc.get("text"));
		
		// This is weird, but it's how the quest works at the moment
		en.stepTest(player, "no");
		assertEquals("Ok. If you want help, just say.", npc.get("text"));
		
		/* Josephine does not know what to do with "bye" without CloakCollector,
		   so do it manually. Jump over the greeting as it was already tested above */
		en.setCurrentState(ConversationStates.QUESTION_2);
		
		en.stepTest(player, "yes");
		assertEquals("Woo! What #cloaks did you bring?", npc.get("text"));
		
		// Give her all but the last - Thrice to test the possible answers  
		for (String itemName : CLOAKS.subList(1, CLOAKS.size())) {
			en.stepTest(player, itemName);
			assertEquals("Oh, I'm disappointed. You don't really have "
					+ Grammar.a_noun(itemName) + " with you.", npc.get("text"));
			
			Item cloak = new Item(itemName, "", "", null);
			player.getSlot("bag").add(cloak);
			en.stepTest(player, itemName);
			assertEquals("Wow, thank you! What else did you bring?", npc.get("text"));
			
			en.stepTest(player, itemName);
			assertEquals("You're terribly forgetful, you already brought that one to me.", npc.get("text"));
		}
		
		// check the message again now that it has changed
		en.stepTest(player, "cloaks");
		assertEquals(stillWantedMessage(player), npc.get("text"));
		
		// Give the last one too. Try lying first again just to be sure
		String lastCloak = CLOAKS.get(0);
		en.stepTest(player, lastCloak);
		assertEquals("Oh, I'm disappointed. You don't really have "
				+ Grammar.a_noun(lastCloak) + " with you.", npc.get("text"));
		Item cloak = new Item(lastCloak, "", "", null);
		player.getSlot("bag").add(cloak);
		en.stepTest(player, lastCloak);
		assertEquals("Answer to last brought cloak", "Oh, yay! You're so kind, I bet you'll have great Karma now! Here, take these killer boots. I think they're gorgeous but they don't fit me!", npc.get("text"));
		
		// check the rewards
		assertEquals(karma + 5.0 + 100.0, player.getKarma(), 0.01);
		assertEquals(100000, player.getXP());
		assertEquals("done;rewarded", player.getQuest(QUEST_NAME));
		assertEquals(true, player.isEquipped("killer boots"));
		
		Item boots = player.getFirstEquipped("killer boots");
		assertEquals("player", boots.getBoundTo());
	}
	
	@Test
	public final void compatibility() {
		SingletonRepository.getNPCList().add(new SpeakerNPC(NPC));
		CloakCollector2 cc = new CloakCollector2();
		cc.addToWorld();
		SpeakerNPC npc = cc.npcs.get(NPC);
		Engine en = npc.getEngine();
		Player player = PlayerTestHelper.createPlayer("player");
		
		player.setQuest(OLD_QUEST, "done");
		player.setQuest(QUEST_NAME, "done");
		
		en.stepTest(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertEquals("Message for the compatibility hack",  "Oh! I didnt' reward you for helping me again! Here, take this boots. I think they're gorgeous but they don't fit me :(", npc.get("text"));
		assertEquals("done;rewarded", player.getQuest(QUEST_NAME));
		assertTrue("The player got the boots", player.isEquipped("killer boots"));
		
		Item boots = player.getFirstEquipped("killer boots");
		assertEquals("player", boots.getBoundTo());
	}
}
	