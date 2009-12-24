package games.stendhal.client.actions;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import games.stendhal.client.MockStendhalClient;

import marauroa.common.game.RPAction;

import org.junit.BeforeClass;
import org.junit.Test;

public class AlterActionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * Tests for execute.
	 */
	@Test
	public void testExecute() {
		new MockStendhalClient("") {
			@Override
			public void send(final RPAction action) {
				client = null;
				assertEquals("alter", action.get("type"));
				assertEquals("schnick", action.get("target"));
				assertEquals("schnack", action.get("stat"));
				assertEquals("schnuck", action.get("mode"));
				assertEquals("blabla", action.get("value"));
			}
		};
		final AlterAction action = new AlterAction();
		assertFalse(action.execute(null, null));
		assertFalse(action.execute(new String[] { "schnick" }, null));
		assertFalse(action.execute(new String[] { "schnick", "schnick" }, null));
		assertFalse(action.execute(new String[] { "schnick", "schnick", "schnick" }, null));

		assertTrue(action.execute(new String[] { "schnick", "schnack", "schnuck" }, "blabla"));
	}

	/**
	 * Tests for getMaximumParameters.
	 */
	@Test
	public void testGetMaximumParameters() {
		final AlterAction action = new AlterAction();
		assertThat(action.getMaximumParameters(), is(3));
	}

	/**
	 * Tests for getMinimumParameters.
	 */
	@Test
	public void testGetMinimumParameters() {
		final AlterAction action = new AlterAction();
		assertThat(action.getMinimumParameters(), is(3));
	}

}
