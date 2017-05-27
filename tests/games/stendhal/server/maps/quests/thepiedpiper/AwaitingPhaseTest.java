package games.stendhal.server.maps.quests.thepiedpiper;

import static org.junit.Assert.assertEquals;
import static utilities.SpeakerNPCTestHelper.getReply;

import java.util.LinkedList;

import org.junit.Test;

import games.stendhal.server.maps.quests.ThePiedPiper;
import games.stendhal.server.maps.quests.piedpiper.TPPQuestHelperFunctions;

public class AwaitingPhaseTest extends TPPTestHelper {

	/**
	 * Tests for quest2.
	 */
	@Test
	public void testAwaitingPhase() {
		LinkedList<String> questHistory = new LinkedList<String>();
        ThePiedPiper.setPhase(TPP_Phase.TPP_INACTIVE);
		ThePiedPiper.switchToNextPhase();
		//quest.phaseInactiveToInvasion();
		killRats(TPPQuestHelperFunctions.getRatsCount()/2);
		questHistory.add("I have killed some rats in Ados city already, and am trying to kill more.");
		assertEquals(questHistory, quest.getHistory(player));
		// [18:19] Mayor Chalmers shouts: Saddanly, rats captured city, they are living now under all Ados buildings. I am now in need of call Piped Piper, rats exterminator. Thank to all who tryed to clean up Ados,  you are welcome to get your reward.

		ThePiedPiper.switchToNextPhase();
		//quest.phaseInvasionToAwaiting();
		en.step(player, "bye"); // in case if previous test was failed
		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "rats");
	//	assertEquals("I called a rats exterminator. "+
		assertEquals("Well, we tried to clean up the city. "+
		"You can get a #reward for your help now, ask about #details "+
		  "if you want to know more.",getReply(npc));
		en.step(player, "details");
		assertEquals("Well, from the last reward, you killed "+
				details()+
				"so I will give you " + rewardMoneys + " money as a #reward for that job.", getReply(npc));
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "reward");
		assertEquals("Please take " + rewardMoneys + " money, thank you very much for your help.", getReply(npc));
		questHistory.clear();
		questHistory.add("I have killed some rats in Ados city and got a reward from Mayor Chalmers!");
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));

		// [19:20] Mayor Chalmers shouts: Thanx gods, rats is gone now, Pied Piper hypnotized them and lead away to dungeons. Those of you, who helped to Ados city with rats problem, can get your reward now.
		ThePiedPiper.getPhaseClass(
				ThePiedPiper.getPhase()).phaseToDefaultPhase(new LinkedList<String>());
		//quest.phaseAwaitingToInactive();
		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "rats");
		assertEquals("Ados isn't being invaded by rats right now. You can still "+
				  "get a #reward for the last time you helped. You can ask for #details "+
				  "if you want.", getReply(npc));
		en.step(player, "details");
		assertEquals("You killed no rats during the #rats invasion. "+
				  "To get a #reward you have to kill at least "+
				  "one rat at that time.", getReply(npc));
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "reward");
		assertEquals("You didn't kill any rats which invaded the city, so you don't deserve a reward.", getReply(npc));
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));

		en.step(player, "hi");
		assertEquals("On behalf of the citizens of Ados, welcome.", getReply(npc));
		en.step(player, "reward");
		assertEquals("You didn't kill any rats which invaded the city, so you don't deserve a reward.", getReply(npc));
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));
	}
}
