package games.stendhal.server.maps.quests.thepiedpiper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import java.util.LinkedList;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.maps.quests.ThePiedPiper;
import games.stendhal.server.maps.quests.piedpiper.TPPQuestHelperFunctions;

public class InvasionPhaseTest extends TPPTestHelper {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TPPTestHelper.setUpBeforeClass();
	}

	@Test
	public void testInvasionPhase() {
		// [17:50] Mayor Chalmers shouts: Ados city is under rats invasion! Anyone who will help to clean up city, will be rewarded!
        ThePiedPiper.setPhase(TPP_Phase.TPP_INACTIVE);
        ThePiedPiper.switchToNextPhase();
		//quest.phaseInactiveToInvasion();
        en.step(player, "bye"); // in case if previous test was failed
        en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "rats");
		assertEquals("There " + Grammar.isare(TPPQuestHelperFunctions.getRatsCount()) +
				" still about "+ TPPQuestHelperFunctions.getRatsCount() + " rats alive.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));
		assertTrue(quest.getHistory(player).isEmpty());
		killRats(TPPQuestHelperFunctions.getRatsCount());
		// [17:58] Mayor Chalmers shouts: No rats in Ados now, exclude those who always lived in storage and haunted house. Rats hunters are welcome to get their reward.
		LinkedList<String> questHistory = new LinkedList<String>();
		questHistory.add("I have killed some rats in Ados city already, and am trying to kill more.");
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "rats");
		assertEquals("Ados isn't being invaded by rats right now. You can still "+
							  "get a #reward for the last time you helped. You can ask for #details "+
							  "if you want.", getReply(npc));
		en.step(player, "details");

		assertEquals("Well, from the last reward, you killed "+
				details()+
				"so I will give you "+rewardMoneys+
				" money as a #reward for that job.", getReply(npc));
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "reward");
		assertEquals("Please take "+ rewardMoneys +" money, thank you very much for your help.", getReply(npc));
		questHistory.clear();
		questHistory.add("I have killed some rats in Ados city and got a reward from Mayor Chalmers!");
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));
	}

	@Test
	public void testAccumulatingRewards() {
		int tempReward = 0;
		LinkedList<String> questHistory = new LinkedList<String>();
        ThePiedPiper.setPhase(TPP_Phase.TPP_INACTIVE);
        ThePiedPiper.switchToNextPhase();
		//quest.phaseInactiveToInvasion();
        // [18:09] Mayor Chalmers shouts: Ados city is under rats invasion! Anyone who will help to clean up city, will be rewarded!
		en.step(player, "bye"); // in case if previous test was failed
		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "rats");
		assertEquals("There "+ Grammar.isare(TPPQuestHelperFunctions.getRatsCount()) +
				" still about "+ TPPQuestHelperFunctions.getRatsCount() +" rats alive.", getReply(npc));
		en.step(player, "details");
		assertEquals("Ados is being invaded by rats! I dont want to either reward you or "+
				  "explain details to you now, until all rats are dead.", getReply(npc));
		en.step(player, "reward");
		assertEquals("Ados is being invaded by rats! "+
				  "I dont want to reward you now, "+
  				  " until all rats are dead.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));

		killRats(TPPQuestHelperFunctions.getRatsCount());
		questHistory.add("I have killed some rats in Ados city already, and am trying to kill more.");
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "rats");
		assertEquals("Ados isn't being invaded by rats right now. You can still "+
							  "get a #reward for the last time you helped. You can ask for #details "+
							  "if you want.", getReply(npc));
		en.step(player, "details");
		assertEquals("Well, from the last reward, you killed "+
				details()+
				"so I will give you "+rewardMoneys+
				" money as a #reward for that job.", getReply(npc));
		tempReward = rewardMoneys;
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));
		assertEquals(questHistory, quest.getHistory(player));
		ThePiedPiper.switchToNextPhase();
		//quest.phaseInactiveToInvasion();
		killRats(TPPQuestHelperFunctions.getRatsCount());
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "rats");
		assertEquals("Ados isn't being invaded by rats right now. You can still "+
							  "get a #reward for the last time you helped. You can ask for #details "+
							  "if you want.", getReply(npc));
		en.step(player, "details");
		assertEquals("Well, from the last reward, you killed "+
				details()+
				"so I will give you "+rewardMoneys+
				" money as a #reward for that job.", getReply(npc));
		assertTrue("", (rewardMoneys > tempReward));
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "reward");
		assertEquals("Please take "+ rewardMoneys +" money, thank you very much for your help.", getReply(npc));
		questHistory.clear();
		questHistory.add("I have killed some rats in Ados city and got a reward from Mayor Chalmers!");
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));
	}



}
