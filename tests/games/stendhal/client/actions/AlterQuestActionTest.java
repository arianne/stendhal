package games.stendhal.client.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.client.MockStendhalClient;
import marauroa.common.game.RPAction;

import org.junit.Test;

public class AlterQuestActionTest {
	@Test
	public void testExecute() {

		new MockStendhalClient("") {
			@Override
			public void send(RPAction action) {
				client = null;
				assertEquals("alterquest", action.get("type"));
				assertEquals("schnick", action.get("target"));
				assertEquals("schnack", action.get("name"));
				assertEquals("schnuck", action.get("state"));
				
			}
		};
		AlterQuestAction action = new AlterQuestAction();
		assertFalse(action.execute(null, null));
		assertFalse(action.execute(new String[] { "schnick" }, null));
		assertFalse(action.execute(new String[] { "schnick", "schnick" }, null));
		assertTrue(action.execute(new String[] { "schnick", "schnack", "schnuck" }, null));
	}
}
