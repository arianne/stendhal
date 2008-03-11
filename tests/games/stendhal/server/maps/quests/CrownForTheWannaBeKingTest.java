package games.stendhal.server.maps.quests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class CrownForTheWannaBeKingTest {
	private static final String QUEST_SLOT = "crown_for_the_wannabe_king";
	private static Engine npcEngine;
	private static SpeakerNPC npc;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PlayerTestHelper.generateNPCRPClasses();

		npc = new SpeakerNPC("Ivan Abe");
		SingletonRepository.getNPCList().add(npc);

		SpeakerNPC rewardnpc = new SpeakerNPC("Kendra Mattori");
		SingletonRepository.getNPCList().add(rewardnpc);

		CrownForTheWannaBeKing cotbk = new CrownForTheWannaBeKing();
		cotbk.addToWorld();

		npcEngine = npc.getEngine();

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		SingletonRepository.getNPCList().clear();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testIdleToQuestion1() throws Exception {
		for (String playerSays : ConversationPhrases.GREETING_MESSAGES) {

			Player bob = PlayerTestHelper.createPlayer("bob");
			bob.setQuest(QUEST_SLOT, "blabla");
			npcEngine.setCurrentState(ConversationStates.IDLE);
			npcEngine.step(bob, playerSays);
			assertThat(npcEngine.getCurrentState(), is(ConversationStates.QUESTION_1));
			assertEquals("Oh it's you again. Did you bring me any #items for my new crown?", npc.getText());
		}
	}

	@Test
	public void testIdleToIdleQuestCompleted() throws Exception {
		for (String playerSays : ConversationPhrases.GREETING_MESSAGES) {

			Player bob = PlayerTestHelper.createPlayer("bob");
			npcEngine.setCurrentState(ConversationStates.IDLE);
			bob.setQuest(QUEST_SLOT, "done");
			assertThat(bob.isQuestCompleted(QUEST_SLOT), is(true));
			npcEngine.step(bob, playerSays);
			assertThat(npcEngine.getCurrentState(), is(ConversationStates.IDLE));
			assertEquals("My new crown will be ready soon and I will dethrone the king! Mwahahaha!", npc.getText());
		}
	}

	@Test
	public void testIdleToIdleQuestinStatereward() throws Exception {
		for (String playerSays : ConversationPhrases.GREETING_MESSAGES) {

			Player bob = PlayerTestHelper.createPlayer("bob");
			bob.setQuest(QUEST_SLOT, "reward");
			npcEngine.setCurrentState(ConversationStates.IDLE);

			npcEngine.step(bob, playerSays);
			assertThat(npcEngine.getCurrentState(), is(ConversationStates.IDLE));
			assertEquals("My new crown will be ready soon and I will dethrone the king! Mwahahaha!", npc.getText());
		}
	}

	@Test
	public void testIdleToAttending() throws Exception {
		for (String playerSays : ConversationPhrases.GREETING_MESSAGES) {

			Player bob = PlayerTestHelper.createPlayer("bob");
			assertThat(bob.hasQuest(QUEST_SLOT), is(false));
			npcEngine.setCurrentState(ConversationStates.IDLE);

			npcEngine.step(bob, playerSays);
			assertThat(npcEngine.getCurrentState(), is(ConversationStates.ATTENDING));
			assertEquals(
					"Greetings. Be quick with your matters, I have a lot of work to do. And next time clean your boots, you are lucky that I'm not the king...yet!",
					npc.getText());
		}
	}

	@Test
	public void testAttendingToQuestOffered() throws Exception {
		Player bob = PlayerTestHelper.createPlayer("bob");
		npcEngine.setCurrentState(ConversationStates.ATTENDING);
		assertThat(bob.isQuestCompleted(QUEST_SLOT), is(false));
		npcEngine.step(bob, "crown");
		assertThat(npcEngine.getCurrentState(), is(ConversationStates.QUEST_OFFERED));
		assertEquals("Yes, I need jewels and gold for my new crown. Will you help me?", npc.getText());
	}

	@Test
	public void testAttendingToIdle() throws Exception {
		Player bob = PlayerTestHelper.createPlayer("bob");
		npcEngine.setCurrentState(ConversationStates.ATTENDING);
		npcEngine.step(bob, "reward");
		assertThat(npcEngine.getCurrentState(), is(ConversationStates.IDLE));
		assertEquals(
				"As I said, find priestess Kendra Mattori in a temple at the city of wizards. She will give you your reward. Now go, I'm busy!",
				npc.getText());
	}

	@Test
	public void testAttendingToIdleQuestNotCompleted() throws Exception {
		String[] triggers = { "no", "nothing" };
		for (String playerSays : triggers) {
			Player bob = PlayerTestHelper.createPlayer("bob");
			npcEngine.setCurrentState(ConversationStates.ATTENDING);
			bob.setQuest(QUEST_SLOT, "blabl");
			assertThat(bob.hasQuest(QUEST_SLOT), is(true));
			assertThat(bob.isQuestCompleted(QUEST_SLOT), is(false));
			npcEngine.step(bob, playerSays);
			assertThat(npcEngine.getCurrentState(), is(ConversationStates.IDLE));
			assertEquals("Well don't come back before you find something for me!", npc.getText());
		}
	}

	@Test
	public void testQuestOfferedToQuestion1() throws Exception {

		for (String playerSays : ConversationPhrases.YES_MESSAGES) {
			Player bob = PlayerTestHelper.createPlayer("bob");
			double oldkarma = bob.getKarma();
			npcEngine.setCurrentState(ConversationStates.QUEST_OFFERED);
			assertTrue(new QuestNotStartedCondition(QUEST_SLOT).fire(bob, null, npc));

			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.QUESTION_1));
			assertThat(playerSays, bob.hasQuest(QUEST_SLOT), is(true));
			assertThat(playerSays, bob.getKarma(), greaterThan(oldkarma));
			assertEquals(
					"I want my crown to be beautiful and shiny. I need 2 #gold bar, 4 #emerald, 3 #sapphire, 2 #carbuncle, 2 #diamond, and 1 #obsidian. Do you have some of those now with you?",
					npc.getText());
		}
	}

	@Test
	public void testQuestOfferedToIdle() throws Exception {
		String[] triggers = { "no", "nothing" };
		for (String playerSays : triggers) {
			Player bob = PlayerTestHelper.createPlayer("bob");
			double oldkarma = bob.getKarma();
			npcEngine.setCurrentState(ConversationStates.QUEST_OFFERED);
			assertThat(playerSays, bob.isQuestCompleted(QUEST_SLOT), is(false));
			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.IDLE));
			assertThat(playerSays, bob.getKarma(), lessThan(oldkarma));
			assertThat(playerSays, bob.hasQuest(QUEST_SLOT), is(true));
			assertThat(playerSays, bob.getQuest(QUEST_SLOT), is("rejected"));
			assertEquals("Oh you don't want to help me?! Get lost, you are wasting my precious time!", npc.getText());
		}
	}

	@Test
	public void testAttendingToAttending() throws Exception {
		String[] triggers = { "plan", "favor", "favour", "quest", "task", "work", "job", "trade", "deal", "offer" };
		for (String playerSays : triggers) {
			Player bob = PlayerTestHelper.createPlayer("bob");

			npcEngine.setCurrentState(ConversationStates.ATTENDING);

			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.ATTENDING));

		}

	}

	@Test
	public void testQuestion1toQuestion1() throws Exception {
		for (String playerSays : ConversationPhrases.YES_MESSAGES) {
			Player bob = PlayerTestHelper.createPlayer("bob");

			npcEngine.setCurrentState(ConversationStates.QUESTION_1);

			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.QUESTION_1));

		}

	}

	@Test
	public void testQuestion1toQuestion1PosactionList() throws Exception {
		npc.remove("text");
		Player bob = PlayerTestHelper.createPlayer("bob");
		bob.setQuest(QUEST_SLOT, CrownForTheWannaBeKing.NEEDED_ITEMS);
		npcEngine.setCurrentState(ConversationStates.QUESTION_1);

		npcEngine.step(bob, "items");
		assertThat("items", npcEngine.getCurrentState(), is(ConversationStates.QUESTION_1));
		assertThat(
				"items",
				npc.getText(),
				is("I need 2 #gold bar, 4 #emerald, 3 #sapphire, 2 #carbuncle, 2 #diamond, and 1 #obsidian. Did you bring something?"));

	}

	@Test
	public void testQuestion1ToIdle() throws Exception {
		String[] triggers = { "no", "nothing" };
		for (String playerSays : triggers) {
			Player bob = PlayerTestHelper.createPlayer("bob");
			bob.setQuest(QUEST_SLOT, "blabla");
			assertThat(bob.isQuestCompleted(QUEST_SLOT), is(false));
			assertThat(bob.getQuest(QUEST_SLOT), not((is("reward"))));

			npcEngine.setCurrentState(ConversationStates.QUESTION_1);

			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.IDLE));

		}

	}

	@Test
	public void testQuestion1ToQuestion1Itembrought() throws Exception {
		String[] triggers = { "obsidian", "diamond", "carbuncle", "sapphire", "emerald", "gold bar" };
		for (String playerSays : triggers) {
			Player bob = PlayerTestHelper.createPlayer("bob");
			bob.setQuest(QUEST_SLOT, "blabla");
			assertThat(bob.isQuestCompleted(QUEST_SLOT), is(false));
			assertThat(bob.getQuest(QUEST_SLOT), not((is("reward"))));

			npcEngine.setCurrentState(ConversationStates.QUESTION_1);

			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.QUESTION_1));
			assertEquals("You have already brought that!", npc.getText());
		}
		for (String playerSays : triggers) {
			Player bob = PlayerTestHelper.createPlayer("bob");
			bob.setQuest(QUEST_SLOT, CrownForTheWannaBeKing.NEEDED_ITEMS);
			assertThat(bob.isQuestCompleted(QUEST_SLOT), is(false));
			assertThat(bob.getQuest(QUEST_SLOT), not((is("reward"))));

			npcEngine.setCurrentState(ConversationStates.QUESTION_1);

			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.QUESTION_1));
			assertEquals("You don't have " + playerSays + " with you!", npc.getText());
		}

		for (String playerSays : triggers) {
			Player bob = PlayerTestHelper.createPlayer("bob");
			bob.setQuest(QUEST_SLOT, CrownForTheWannaBeKing.NEEDED_ITEMS);

			PlayerTestHelper.equipWithItem(bob, playerSays);
			assertThat(bob.isQuestCompleted(QUEST_SLOT), is(false));
			assertThat(bob.getQuest(QUEST_SLOT), not((is("reward"))));

			npcEngine.setCurrentState(ConversationStates.QUESTION_1);

			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.QUESTION_1));
			assertEquals("Good, do you have anything else?", npc.getText());
		}

		for (String playerSays : triggers) {
			Player bob = PlayerTestHelper.createPlayer("bob");
			bob.setQuest(QUEST_SLOT, CrownForTheWannaBeKing.NEEDED_ITEMS);

			PlayerTestHelper.equipWithItem(bob, playerSays);
			assertThat(bob.isQuestCompleted(QUEST_SLOT), is(false));
			assertThat(bob.getQuest(QUEST_SLOT), not((is("reward"))));

			npcEngine.setCurrentState(ConversationStates.QUESTION_1);

			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.QUESTION_1));
			assertEquals("Good, do you have anything else?", npc.getText());
		}

	}

	@Test
	public void testBringItems() throws Exception {
		Player bob = PlayerTestHelper.createPlayer("bob");
		bob.setQuest(QUEST_SLOT, CrownForTheWannaBeKing.NEEDED_ITEMS);

		// is("I need 2 #gold bar, 4 #emerald, 3 #sapphire, 2 #carbuncle, 2
		// #diamond, and 1 #obsidian. Did you bring something?"));
		String[] triggers = { "obsidian", "diamond", "diamond", "carbuncle", "carbuncle", "sapphire", "sapphire",
				"sapphire", "emerald", "emerald", "emerald", "emerald", "gold bar" };
		npcEngine.setCurrentState(ConversationStates.QUESTION_1);
		for (String playerSays : triggers) {

			PlayerTestHelper.equipWithItem(bob, playerSays);
			assertThat(bob.isQuestCompleted(QUEST_SLOT), is(false));
			assertThat(bob.getQuest(QUEST_SLOT), not((is("reward"))));

			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.QUESTION_1));
			assertEquals("Good, do you have anything else?", npc.getText());
		}

		PlayerTestHelper.equipWithItem(bob, "gold bar");
		assertThat(bob.isQuestCompleted(QUEST_SLOT), is(false));
		assertThat(bob.getQuest(QUEST_SLOT), not((is("reward"))));

		npcEngine.step(bob, "gold bar");
		assertThat("last thing brought", npcEngine.getCurrentState(), is(ConversationStates.ATTENDING));
		assertEquals("reward", bob.getQuest(QUEST_SLOT));
		assertEquals(
				"You have served me well, my crown will be the mightiest of them all! Go to see Kendra Mattori in the Wizard City to get your #reward.",
				npc.getText());

		double oldkarma = bob.getKarma();
		int oldatk = bob.getATKXP();
		
		SpeakerNPC rewardnpc = SingletonRepository.getNPCList().get("Kendra Mattori");
		Engine rewardEngine = rewardnpc.getEngine();
		rewardEngine.setCurrentState(ConversationStates.ATTENDING);
		rewardEngine.step(bob, "reward");
		
		assertThat(bob.isQuestCompleted(QUEST_SLOT), is(true));
		assertThat(bob.getKarma(), greaterThan(oldkarma));
		assertThat(bob.getATKXP(), greaterThan(oldatk));
		
	}

}
