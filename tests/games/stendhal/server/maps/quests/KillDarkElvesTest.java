package games.stendhal.server.maps.quests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class KillDarkElvesTest {
	private static final String THING = "thing";

	private static final String DARK_ELF_CAPTAIN = "dark elf captain";

	private static final String DARK_ELF_ARCHER = "dark elf archer";

	private static final String QUEST_SLOT = "kill_dark_elves";

	private static SpeakerNPC npc;
	private static Engine npcEngine;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PlayerTestHelper.generateNPCRPClasses();

		npc = new SpeakerNPC("maerion");
		npcEngine = npc.getEngine();
		SingletonRepository.getNPCList().add(npc);
		final KillDarkElves quest = new KillDarkElves();
		quest.addToWorld();

		MockStendlRPWorld.get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		MockStendlRPWorld.reset();
		SingletonRepository.getNPCList().clear();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		npc.remove("text");
	}

	@Test
	public void testIdleToAttending() throws Exception {

		for (final String playerSays : ConversationPhrases.QUEST_MESSAGES) {

			final Player bob = PlayerTestHelper.createPlayer("bob");
			assertThat(bob.hasQuest(QUEST_SLOT), is(false));
			npcEngine.setCurrentState(ConversationStates.ATTENDING);

			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.QUEST_OFFERED));
			assertEquals(
					playerSays,
					"I have a problem with some dark elves. I used to be in league with them... now they are too strong. There is access to their lair from a #secret #room in this hall.",
					getReply(npc));
		}
	}

	@Test
	public void testQuestOfferedToQuestOffered() throws Exception {

		for (final String playerSays : Arrays.asList("secret", "room", "secret xxxx", "secret room")) {

			final Player bob = PlayerTestHelper.createPlayer("bob");
			assertThat(bob.hasQuest(QUEST_SLOT), is(false));
			npcEngine.setCurrentState(ConversationStates.QUEST_OFFERED);

			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.QUEST_OFFERED));
			assertEquals(
					playerSays,
					"It's that room downstairs with a grey roof and the evil face on the door. Inside you'll find what the dark elves were making, a mutant thing. Will you help?",
					getReply(npc));
		}
	}

	@Test
	public void testQuestStartedTOAttending() throws Exception {

		for (final String playerSays : Arrays.asList("secret", "room", "secret xxxx", "secret room")) {

			final Player bob = PlayerTestHelper.createPlayer("bob");
			assertThat(bob.hasQuest(QUEST_SLOT), is(false));
			npcEngine.setCurrentState(ConversationStates.QUEST_STARTED);

			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.ATTENDING));
			assertEquals(
					playerSays,
					"The room is below us. It has a grey roof and a evil face for a door. I need you to kill all the dark elves and bring me the amulet from the mutant thing.",
					getReply(npc));
		}
	}

	@Test
	public void testIdleToQuestStarted() throws Exception {

		for (final String playerSays : ConversationPhrases.GREETING_MESSAGES) {
			final Player bob = PlayerTestHelper.createPlayer("bob");
			bob.setQuest(QUEST_SLOT, "start");
			npcEngine.setCurrentState(ConversationStates.IDLE);
			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.QUEST_STARTED));
			assertEquals(
					playerSays,
					"Don't you remember promising to sort out my dark elf problem? Kill every dark elf in the #secret room below - especially the snivelling dark elf captain and any evil dark elf archers you find! And bring me the amulet from the mutant thing.",
					getReply(npc));

		}
	}

	@Test
	public void testAttendingToQuestOffered() throws Exception {

		for (final String playerSays : ConversationPhrases.QUEST_MESSAGES) {
			final Player bob = PlayerTestHelper.createPlayer("bob");
			bob.setQuest(QUEST_SLOT, "start");
			npcEngine.setCurrentState(ConversationStates.ATTENDING);
			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.ATTENDING));
			assertEquals(
					playerSays,
					"I already asked you to kill every dark elf in the tunnel below the secret room. And bring me the amulet from the thing.",
					getReply(npc));

		}
	}

	@Test
	public void testAttendingToAttending() throws Exception {

		for (final String playerSays : ConversationPhrases.QUEST_MESSAGES) {
			final Player bob = PlayerTestHelper.createPlayer("bob");
			bob.setQuest(QUEST_SLOT, "done");
			npcEngine.setCurrentState(ConversationStates.ATTENDING);
			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.ATTENDING));
			assertEquals(playerSays, "Thanks for your help. I am relieved to have the amulet back.", getReply(npc));

		}
	}

	@Test
	public void testAttendingToAttendingallKilledNoRing() throws Exception {
		for (final String playerSays : ConversationPhrases.GREETING_MESSAGES) {
			final Player bob = PlayerTestHelper.createPlayer("bob");

			bob.setSharedKill(DARK_ELF_ARCHER);
			bob.setSharedKill(DARK_ELF_CAPTAIN);
			bob.setSharedKill(THING);

			bob.setQuest(QUEST_SLOT, "start");

			npcEngine.setCurrentState(ConversationStates.IDLE);
			npcEngine.step(bob, playerSays);

			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.QUEST_STARTED));
			assertEquals(playerSays, "What happened to the amulet? Remember I need it back!", getReply(npc));

		}
	}

	@Test
	public void testAttendingToAttendingallKilledRing() throws Exception {
		for (final String playerSays : ConversationPhrases.GREETING_MESSAGES) {
			final Player bob = PlayerTestHelper.createPlayer("bob");

			bob.setSharedKill(DARK_ELF_ARCHER);
			bob.setSharedKill(DARK_ELF_CAPTAIN);
			bob.setSharedKill(THING);

			bob.setQuest(QUEST_SLOT, "start");

			PlayerTestHelper.equipWithItem(bob, "amulet");

			assertTrue(bob.hasKilled(DARK_ELF_ARCHER));
			assertTrue(bob.hasKilled(DARK_ELF_CAPTAIN));
			assertTrue(bob.hasKilled(THING));
			assertTrue(bob.isEquipped("amulet"));

			final double karma = bob.getKarma();
			final int xp = bob.getXP();

			npcEngine.setCurrentState(ConversationStates.IDLE);
			npcEngine.step(bob, playerSays);
			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.ATTENDING));
			assertEquals(
					playerSays,
					"Many, many thanks. I am relieved to have that back. Here, take this ring. It can revive the powers of the dead.",
					getReply(npc));
			assertFalse(bob.isEquipped("amulet"));
			assertTrue(bob.isEquipped("emerald ring"));
			assertThat(bob.getKarma(), greaterThan(karma));
			assertThat(bob.getXP(), greaterThan(xp));
			assertTrue(bob.isQuestCompleted(QUEST_SLOT));

		}
	}

	@Test
	public void testQuestOfferedToAttendingYes() throws Exception {

		for (final String playerSays : ConversationPhrases.YES_MESSAGES) {
			final Player bob = PlayerTestHelper.createPlayer("bob");
			final double oldKarma = bob.getKarma();

			npcEngine.setCurrentState(ConversationStates.QUEST_OFFERED);

			npcEngine.step(bob, playerSays);

			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.ATTENDING));
			assertEquals(playerSays,
					"Good. Please kill every dark elf down there and get the amulet from the mutant thing.",
					getReply(npc));

			assertFalse(bob.hasKilled(DARK_ELF_ARCHER));
			assertFalse(bob.hasKilled(DARK_ELF_CAPTAIN));
			assertFalse(bob.hasKilled(THING));
			assertThat(bob.getKarma(), greaterThan(oldKarma));
			assertThat(bob.getQuest(QUEST_SLOT), is("start"));
		}
	}

	@Test
	public void testQuestOfferedToAttendingNo() throws Exception {

		
		final String[] triggers = { "no", "nothing" };
		for (final String playerSays : triggers) {
			final Player bob = PlayerTestHelper.createPlayer("bob");
			final double oldKarma = bob.getKarma();

			npcEngine.setCurrentState(ConversationStates.QUEST_OFFERED);

			npcEngine.step(bob, playerSays);

			assertThat(playerSays, npcEngine.getCurrentState(), is(ConversationStates.ATTENDING));
			assertEquals(playerSays,
					"Then I fear for the safety of the Nalwor elves...",
					getReply(npc));
			assertThat(bob.getKarma(), lessThan(oldKarma));
			assertThat(bob.getQuest(QUEST_SLOT), is("rejected"));
		}
	}
}
