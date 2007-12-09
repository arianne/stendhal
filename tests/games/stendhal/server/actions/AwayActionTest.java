package games.stendhal.server.actions;

import static org.junit.Assert.assertEquals;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

import org.junit.Test;

import utilities.PlayerTestHelper;

public class AwayActionTest {

	@Test
	public void testOnAway() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		RPAction action = new RPAction();
		action.put("type", "away");
		AwayAction aa  = new AwayAction();
		aa.onAway(bob, action);
		assertEquals(null,bob.getAwayMessage());
		action.put("message", "bla");
		aa.onAway(bob, action);
		assertEquals("bla",bob.getAwayMessage());
		
	}

	@Test
	public void testOnAction() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		RPAction action = new RPAction();
		action.put("type", "away");
		AwayAction aa  = new AwayAction();
		aa.onAction(bob, action);
		assertEquals(null,bob.getAwayMessage());
		action.put("message", "bla");
		aa.onAction(bob, action);
		assertEquals("bla",bob.getAwayMessage());
	}

}
