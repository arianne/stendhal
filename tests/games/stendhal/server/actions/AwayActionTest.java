package games.stendhal.server.actions;

import static org.junit.Assert.assertEquals;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

import org.junit.Test;

import utilities.PlayerTestHelper;

public class AwayActionTest {

	@Test(expected = NullPointerException.class)
	public void testPlayerIsNull() {
		RPAction action = new RPAction();
		action.put("type", "away");
		AwayAction aa = new AwayAction();
		aa.onAction(null, action);
	}

	@Test
	public void testOnAction() {
		Player bob = PlayerTestHelper.createPrivateTextMockingTestPlayer("bob");
		RPAction action = new RPAction();
		action.put("type", "away");
		AwayAction aa = new AwayAction();
		aa.onAction(bob, action);
		assertEquals(null, bob.getAwayMessage());
		action.put("message", "bla");
		aa.onAction(bob, action);
		assertEquals("bla", bob.getAwayMessage());
	}

	@Test
	public void testOnInvalidAction() {
		Player bob = PlayerTestHelper.createPrivateTextMockingTestPlayer("bob");
		bob.clearEvents();
		RPAction action = new RPAction();
		action.put("type", "bla");
		action.put("message", "bla");
		AwayAction aa = new AwayAction();
		aa.onAction(bob, action);
		assertEquals(null, bob.getAwayMessage());
	}

}
