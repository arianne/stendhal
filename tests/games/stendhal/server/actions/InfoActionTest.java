package games.stendhal.server.actions;


import static org.junit.Assert.assertFalse;
import games.stendhal.server.entity.player.Player;

import org.junit.Test;

import utilities.PlayerTestHelper;

public class InfoActionTest {

	/**
	 * Tests for execute.
	 */
	@Test
	public void testExecute() throws Exception {
		Player bob = PlayerTestHelper.createPlayer("bob");
		InfoAction info = new InfoAction();
		info.onAction(bob, null);
		assertFalse(bob.events().isEmpty());
		//assertEquals(null,bob.events().get(0));
		
	}
}
