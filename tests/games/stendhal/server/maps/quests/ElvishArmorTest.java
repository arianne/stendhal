package games.stendhal.server.maps.quests;

import static games.stendhal.server.entity.npc.ConversationStates.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import games.stendhal.common.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import java.util.List;
import java.util.Arrays;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;



import utilities.PlayerTestHelper;

public class ElvishArmorTest {

	private static Engine npcEngine;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		MockStendlRPWorld.get();
		PlayerTestHelper.generateNPCRPClasses();
		npc = new SpeakerNPC("Lupos");
		npcEngine = npc.getEngine();
		SingletonRepository.getNPCList().add(npc);
		new ElvishArmor().addToWorld();
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
		npc.remove("text");
	}
	private static final String QUEST_SLOT = "elvish_armor";

	private static final List<String> NEEDEDITEMS = Arrays.asList(
			"elvish armor", "elvish legs", "elvish boots", "elvish sword",
			"elvish cloak", "elvish shield");

	private static SpeakerNPC npc;

	@Test
	public void testIdleTOAttending() {
		for (final String playerSays : ConversationPhrases.GREETING_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("bob");
			npcEngine.setCurrentState(IDLE);
			assertThat(player.hasQuest(QUEST_SLOT), is(false));

			npcEngine.step(player, playerSays);

			assertThat(playerSays, npcEngine.getCurrentState(), is(ATTENDING));
			assertThat(playerSays, npc.getText(), is("Greetings, traveller. I see that you have come far to be here. I am interested in anyone who has encountered our kin, the green elves of Nalwor. They guard their #secrets closely."));
		}
	}


	@Test
	public void testQuestOfferedToQuestOffered() {
		for (final String playerSays : Arrays.asList("secrets")) {
			final Player player = PlayerTestHelper.createPlayer("bob");
			npcEngine.setCurrentState(ATTENDING);

			npcEngine.step(player, playerSays);

			assertThat(playerSays, npcEngine.getCurrentState(), is(QUEST_OFFERED));
			assertThat(playerSays, npc.getText(), is("They won't share knowledge of how to create the green armor, shields and the like. You would call them elvish items. I wonder if a traveller like you could bring me any?"));
		}
	}


	@Test
	public void testQuestOfferedToIdle() {
		for (final String playerSays : ConversationPhrases.YES_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("bob");
			npcEngine.setCurrentState(QUEST_OFFERED);

			assertFalse(player.hasQuest(QUEST_SLOT));
			final double oldkarma = player.getKarma();

			npcEngine.step(player, playerSays);

			assertThat(playerSays, npcEngine.getCurrentState(), is(IDLE));
			assertThat(playerSays, npc.getText(), is("The secrets of the green elves shall be ours at last! Bring me all elvish equipment you can find, I'll reward you well!"));
			assertTrue(playerSays, player.hasQuest(QUEST_SLOT));
			assertThat(playerSays, player.getKarma(), greaterThan(oldkarma));
		}
	}

	/**
     * Player is not willing to help.
     */
	@Test
	public void testQuestOfferedToQuestOfferes() {
		for (final String playerSays : Arrays.asList("no", "nothing")) {
			final Player player = PlayerTestHelper.createPlayer("bob");
			npcEngine.setCurrentState(QUEST_OFFERED);

			assertFalse(player.hasQuest(QUEST_SLOT));
			final double oldkarma = player.getKarma();

			npcEngine.step(player, playerSays);

			assertThat(playerSays, npcEngine.getCurrentState(), is(IDLE));
			assertThat(playerSays, npc.getText(), is("Another unhelpful soul, I see."));
			assertFalse(playerSays, player.hasQuest(QUEST_SLOT));
			assertThat(playerSays, player.getKarma(), lessThan(oldkarma));
		}
	}


	/**
	 *  Player returns while quest is still active.
	 */
	@Test
	public void testIdleToQuestion1() {
		for (final String playerSays : ConversationPhrases.GREETING_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("bob");
			npcEngine.setCurrentState(IDLE);
			player.setQuest(QUEST_SLOT, "");
			assertTrue(player.hasQuest(QUEST_SLOT));
			assertFalse(player.isQuestCompleted(QUEST_SLOT));


			npcEngine.step(player, playerSays);

			assertThat(playerSays, npcEngine.getCurrentState(), is(ATTENDING));
			assertThat(playerSays, npc.getText(), is("Hello! I hope your search for elvish #equipment is going well?"));
		}
	}



	/**
	 * Player says he the name of a required item he has not got.
	 */
	@Test
	public void testQuestion1ToQuestion1NeededITems() {
		for (final String playerSays : NEEDEDITEMS) {

			final Player player = PlayerTestHelper.createPlayer("bob");
			npcEngine.setCurrentState(QUESTION_1);

			npcEngine.step(player, playerSays);

			assertThat(playerSays, npcEngine.getCurrentState(), is(QUESTION_1));
			assertThat(playerSays, npc.getText(), is("Liar! You don't really have "
					+ Grammar.a_noun(playerSays)	+ " with you."));
		}
	}

	/**
	 * Player says the name of a required item he has got.
	 * and repeats it (brings it twice).
	 */
	@Test
	public void testQuestion1ToQuestion1NeededITemsGot() {
		for (final String playerSays : NEEDEDITEMS) {
			final Player player = PlayerTestHelper.createPlayer("bob");

			PlayerTestHelper.equipWithItem(player, playerSays);
			npcEngine.setCurrentState(QUESTION_1);

			npcEngine.step(player, playerSays);

			assertThat(playerSays, npcEngine.getCurrentState(), is(QUESTION_1));
			assertThat(playerSays, npc.getText(), is("Excellent work. Is there more that you plundered?"));

			PlayerTestHelper.equipWithItem(player, playerSays);
			npcEngine.setCurrentState(QUESTION_1);

			npcEngine.step(player, playerSays);

			assertThat(playerSays, npc.getText(), is("You've already brought that elvish item to me."));
			assertThat(playerSays, npcEngine.getCurrentState(), is(QUESTION_1));
		}
	}

	/**
	 * Player brings all items.
	 */
	@Test
	public void testQuestion1ToAttending() {
		final Player player = PlayerTestHelper.createPlayer("bob");
		final double oldKarma = player.getKarma();
		final int oldXp = player.getXP();
		npcEngine.setCurrentState(QUESTION_1);
		for (final String playerSays : NEEDEDITEMS) {
			PlayerTestHelper.equipWithItem(player, playerSays);
			npcEngine.step(player, playerSays);
		}
		assertThat(npcEngine.getCurrentState(), is(ATTENDING));
		assertThat(npc.getText(), is("I will study these! The albino elves owe you a debt of thanks."));
		assertThat(player.getKarma(), greaterThan(oldKarma));
		assertThat(player.getXP(), is(oldXp + 20000));
	}

	/**
	 * Player brings an item not in list.
	 */
	@Test
	public void testQuestion1ToQuestion1NotInList() {
		final Player player = PlayerTestHelper.createPlayer("bob");
		npcEngine.setCurrentState(QUESTION_1);
		npcEngine.step(player, "NotanItem");

		assertThat(npcEngine.getCurrentState(), is(QUESTION_1));
		assertThat(npc.getText(), is("I don't think that's a piece of elvish armor..."));
	}
	
    @Ignore 
    // ignored because removed from quest logic now.
    public void testQuestion1toIdle() {
            for (final String playerSays : ConversationPhrases.GOODBYE_MESSAGES) {
                    final Player player = PlayerTestHelper.createPlayer("bob");
                    npcEngine.setCurrentState(QUESTION_1);

                    npcEngine.step(player, playerSays);

                    assertThat(playerSays, npcEngine.getCurrentState(), is(IDLE));
                    assertThat(playerSays, npc.getText(), is("Bye."));
            }
    }

	@Test
	public void testAttendingToAttending() {
		for (final String playerSays : Arrays.asList("no", "nothing")) {
			final Player player = PlayerTestHelper.createPlayer("bob");
			npcEngine.setCurrentState(ATTENDING);

			npcEngine.step(player, playerSays);

			assertThat(playerSays, npcEngine.getCurrentState(), is(IDLE));
			assertThat(playerSays, npc.getText(), is("I understand, the green elves protect themselves well. If there's anything else I can do for you, just say."));
		}
	}


	/**
	 * Player says no to different question.
	 */
	@Test
	public void testQuestion1ToAttendingNoToAny() {
		for (final String playerSays : Arrays.asList("no")) {
			final Player player = PlayerTestHelper.createPlayer("bob");
			player.setQuest(QUEST_SLOT, "");
			npcEngine.setCurrentState(QUESTION_1);

			npcEngine.step(player, playerSays);

			assertThat(playerSays, npc.getText(), is("I understand, the green elves protect themselves well. If there's anything else I can do for you, just say."));
			assertThat(playerSays, npcEngine.getCurrentState(), is(IDLE));

		}
	}

	@Test
	public void testIdleToAttendingQuestCompleted() {
		for (final String playerSays : ConversationPhrases.GREETING_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("bob");
			player.setQuest(QUEST_SLOT, "done");
			npcEngine.setCurrentState(IDLE);

			npcEngine.step(player, playerSays);

			assertThat(playerSays, npc.getText(), is("Greetings again, old friend."));
			assertThat(playerSays, npcEngine.getCurrentState(), is(ATTENDING));

		}
	}

    /**
     * Player with finished quest says offer.
     */
	@Test
	public void testAttendingtoAttendingOffer() {
		for (final String playerSays : ConversationPhrases.OFFER_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("bob");
			player.setQuest(QUEST_SLOT, "done");
			npcEngine.setCurrentState(ATTENDING);

			npcEngine.step(player, playerSays);

			assertThat(playerSays, npc.getText(), is("If you have found any more elvish items, I'd be glad if you would #sell them to me. I would buy elvish armor, shield, legs, boots, cloak or sword. I would also buy a drow sword if you have one."));
			assertThat(playerSays, npcEngine.getCurrentState(), is(ATTENDING));
		}
	}

	/**
	 * player returns after finishing the quest and says quest.
	 * *This is no longer in quest as the general logic doesn't have it in
	 */
	@Ignore
	public void testAttendingtoAttendingDoneQuestmessage() {
		for (final String playerSays : ConversationPhrases.QUEST_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("bob");
			player.setQuest(QUEST_SLOT, "done");
			npcEngine.setCurrentState(ATTENDING);

			npcEngine.step(player, playerSays);

			assertThat(playerSays, npc.getText(), is("I'm now busy studying the properties of the elvish armor you brought me. It really is intriguing. Until I can reproduce it, I would buy similar items from you."));
			assertThat(playerSays, npcEngine.getCurrentState(), is(ATTENDING));

		}
	}

	/**
	 * player returns after finishing the quest and says quest.
	 * *This is no longer in quest as the general logic doesn't have it in
	 */
	@Ignore
	public void testAttendingtoQuestion1NotDoneQuestmessage() {
		for (final String playerSays : ConversationPhrases.QUEST_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("bob");
			player.setQuest(QUEST_SLOT, "");
			npcEngine.setCurrentState(ATTENDING);

			npcEngine.step(player, playerSays);

			assertThat(playerSays, npc.getText(), is("As you already know, I seek elvish #equipment."));
			assertThat(playerSays, npcEngine.getCurrentState(), is(QUESTION_1));
		}
	}

	@Test
	public void testAttendingtoAttendingOfferquestnotdone() {
		for (final String playerSays : ConversationPhrases.OFFER_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("bob");
			player.setQuest(QUEST_SLOT, "");
			npcEngine.setCurrentState(ATTENDING);

			npcEngine.step(player, playerSays);

			assertThat(playerSays, npc.getText(), is("I don't think I trust you well enough yet ... "));
			assertThat(playerSays, npcEngine.getCurrentState(), is(ATTENDING));
		}
	}

}
