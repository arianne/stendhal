package games.stendhal.server.maps.quests.thepiedpiper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.maps.quests.ThePiedPiper;

public class InactivePhaseTest extends TPPTestHelper {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TPPTestHelper.setUpBeforeClass();
	}

	/**
	 * Tests for quest.
	 */
	@Test
	public void testInactivePhase() {
		ThePiedPiper.setPhase(TPP_Phase.TPP_INACTIVE);
		assertTrue(quest.getHistory(player).isEmpty());
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
		en.step(player, "reward");
		assertEquals("You didn't kill any rats which invaded the city, so you don't deserve a reward.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Good day to you.", getReply(npc));
		assertTrue(quest.getHistory(player).isEmpty());
	}


}
