package games.stendhal.server.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.engine.SingletonRepository;
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
		SingletonRepository.getRPWorld().addRPZone(zone);

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
		boolean executeSucceeded = CommandCenter.execute(player1, action);
		assertTrue(executeSucceeded);
		assertEquals(
				"You see player1.\nplayer1 is level 0 and has been playing 0 hours and 0 minutes.",
				player1.getPrivateTextString());
		player1.resetPrivateTextString();

		// test "/look #id" syntax
		action = new RPAction();
		action.put("type", "look");
		action.put("target", "#"
				+ Integer.toString(player2.getID().getObjectID()));
		executeSucceeded = CommandCenter.execute(player1, action);
		assertTrue(executeSucceeded);
		assertEquals(
				"You see player2.\nplayer2 is level 0 and has been playing 0 hours and 0 minutes.",
				player1.getPrivateTextString());
		player1.resetPrivateTextString();

		action = new RPAction();
		action.put("type", "look");
		action.put("target", "npc");
		executeSucceeded = CommandCenter.execute(player1, action);
		assertTrue(executeSucceeded);
		assertEquals("You see npc.", player1.getPrivateTextString());
		player1.resetPrivateTextString();
	}

	@Test
	// Test for 1847043 - out-of-screen commands
	public void testLookOutOfScreen() {
		PrivateTextMockingTestPlayer player1 = (PrivateTextMockingTestPlayer) MockStendhalRPRuleProcessor.get().getPlayer("player1");
		assertNotNull(player1);

		PrivateTextMockingTestPlayer player2 = (PrivateTextMockingTestPlayer) MockStendhalRPRuleProcessor.get().getPlayer("player2");
		assertNotNull(player2);

		player1.setPosition(20, 20);
		player2.setPosition(50, 50);

		RPAction action = new RPAction();
		action.put("type", "look");
		action.put("target", "player1");
		boolean executeSucceeded = CommandCenter.execute(player1, action);
		assertTrue(executeSucceeded);
		assertTrue(player1.getPrivateTextString().startsWith("You see player1."));
		player1.resetPrivateTextString();

		player1.setPosition(20, 20);
		player2.setPosition(50, 50);

		action = new RPAction();
		action.put("type", "look");
		action.put("target", "player2");
		executeSucceeded = CommandCenter.execute(player1, action);
		assertTrue(executeSucceeded);
		assertEquals("", player1.getPrivateTextString());
		player1.resetPrivateTextString();

		player1.setPosition(20, 20);
		player2.setPosition(19, 50);

		action = new RPAction();
		action.put("type", "look");
		action.put("target", "player2");
		executeSucceeded = CommandCenter.execute(player1, action);
		assertTrue(executeSucceeded);
		assertEquals("", player1.getPrivateTextString());
		player1.resetPrivateTextString();

		player1.setPosition(20, 20);
		player2.setPosition(10, 15);

		action = new RPAction();
		action.put("type", "look");
		action.put("target", "player2");
		executeSucceeded = CommandCenter.execute(player1, action);
		assertTrue(executeSucceeded);
		assertTrue(player1.getPrivateTextString().startsWith("You see player2."));
		player1.resetPrivateTextString();
	}

	@Test
	// Test for 1864205 - /look adminname shows admin in ghostmode
	public void testLookAdmin() {
		PrivateTextMockingTestPlayer player1 = (PrivateTextMockingTestPlayer) MockStendhalRPRuleProcessor.get().getPlayer("player1");
		assertNotNull(player1);

		PrivateTextMockingTestPlayer player2 = (PrivateTextMockingTestPlayer) MockStendhalRPRuleProcessor.get().getPlayer("player2");
		assertNotNull(player2);

		player1.setAdminLevel(0);
		player2.setGhost(false);
		RPAction action = new RPAction();
		action.put("type", "look");
		action.put("target", "player2");
		boolean executeSucceeded = CommandCenter.execute(player1, action);
		assertTrue(executeSucceeded);
		assertTrue(player1.getPrivateTextString().startsWith("You see player2."));
		player1.resetPrivateTextString();

		player1.setAdminLevel(0);
		player2.setGhost(true);
		action = new RPAction();
		action.put("type", "look");
		action.put("target", "player2");
		executeSucceeded = CommandCenter.execute(player1, action);
		assertTrue(executeSucceeded);
		assertEquals("", player1.getPrivateTextString());
		player1.resetPrivateTextString();

		player1.setAdminLevel(1000);
		player2.setGhost(true);
		action = new RPAction();
		action.put("type", "look");
		action.put("target", "player2");
		executeSucceeded = CommandCenter.execute(player1, action);
		assertTrue(executeSucceeded);
		assertTrue(player1.getPrivateTextString().startsWith("You see player2."));
		player1.resetPrivateTextString();
	}

}
