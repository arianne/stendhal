package games.stendhal.server.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.NPC;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.PrivateTextMockingTestPlayer;

/**
 * Test server actions.
 * 
 * @author Martin Fuchs
 */
public class LookActionTest {

	@BeforeClass
	public static void setUpBeforeClass() {
		Log4J.init();
		PlayerTestHelper.generatePlayerRPClasses();
	}

	@Before
	public void setup() {
		MockStendhalRPRuleProcessor processor = MockStendhalRPRuleProcessor
				.get();

		StendhalRPZone zone = new StendhalRPZone("testzone");
		StendhalRPWorld.get().addRPZone(zone);

		PrivateTextMockingTestPlayer player1 = PlayerTestHelper.createPrivateTextMockingTestPlayer("player1");
		processor.addPlayer(player1);
		zone.add(player1);

		PrivateTextMockingTestPlayer player2 = PlayerTestHelper.createPrivateTextMockingTestPlayer("player2");
		processor.addPlayer(player2);
		zone.add(player2);

		NPC npc = new SpeakerNPC("npc");
		zone.add(npc);
	}

	@Test
	public void testLook() {
		PrivateTextMockingTestPlayer player1 = (PrivateTextMockingTestPlayer) MockStendhalRPRuleProcessor.get().getPlayer("player1");
		assertNotNull(player1);

		PrivateTextMockingTestPlayer player2 = (PrivateTextMockingTestPlayer) MockStendhalRPRuleProcessor.get().getPlayer("player2");
		assertNotNull(player2);

		// test "/look <name>" syntax
		RPAction action = new RPAction();
		action.put("type", "look");
		action.put("target", "player1");
		CommandCenter.execute(player1, action);
		assertEquals(
				"You see player1.\nplayer1 is level 0 and has been playing 0 hours and 0 minutes.",
				player1.getPrivateTextString());
		player1.resetPrivateTextString();

		// test "/look #id" syntax
		action = new RPAction();
		action.put("type", "look");
		action.put("target", "#"
				+ Integer.toString(player2.getID().getObjectID()));
		CommandCenter.execute(player1, action);
		assertEquals(
				"You see player2.\nplayer2 is level 0 and has been playing 0 hours and 0 minutes.",
				player1.getPrivateTextString());
		player1.resetPrivateTextString();

		action = new RPAction();
		action.put("type", "look");
		action.put("target", "npc");
		CommandCenter.execute(player1, action);
		assertEquals("You see npc.", player1.getPrivateTextString());
		player1.resetPrivateTextString();
	}
}
