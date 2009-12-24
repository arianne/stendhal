package games.stendhal.client.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.client.MockStendhalClient;
import marauroa.common.game.RPAction;

import org.junit.Test;

public class AlterQuestActionTest {
	/**
	 * Tests for execute.
	 */
	@Test
	public void testExecute() {

		new MockStendhalClient("") {
			@Override
			public void send(final RPAction action) {
				client = null;
				assertEquals("alterquest", action.get("type"));
				assertEquals("schnick", action.get("target"));
				assertEquals("schnack", action.get("name"));
				assertEquals("schnuck", action.get("state"));
				
			}
		};
		final AlterQuestAction action = new AlterQuestAction();
		assertFalse(action.execute(null, null));
		assertFalse(action.execute(new String[] { "schnick" }, null));
		assertTrue(action.execute(new String[] { "schnick", "schnack", "schnuck" }, null));

		new MockStendhalClient("") {
			@Override
			public void send(final RPAction action) {
				client = null;
				assertEquals("alterquest", action.get("type"));
				assertEquals("schnick", action.get("target"));
				assertEquals("schnick", action.get("name"));
				assertEquals(null, action.get("state"));
				
			}
		};
		
		assertTrue(action.execute(new String[] { "schnick", "schnick" }, null));
		
	}
}
