package games.stendhal.client.actions;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import games.stendhal.client.MockStendhalClient;
import marauroa.common.game.RPAction;

import org.junit.Test;

public class AlterCreatureActionTest {


	/**
	 * Tests for execute.
	 */
	@Test
	public void testExecute() {

		new MockStendhalClient("") {
			@Override
			public void send(final RPAction action) {
				client = null;
				assertEquals("altercreature", action.get("type"));
				assertEquals("schnick", action.get("target"));
				assertEquals("schnack", action.get("text"));
			}
		};
		final AlterCreatureAction action = new AlterCreatureAction();
		assertFalse(action.execute(null, null));
		assertFalse(action.execute(new String[] { "schnick" }, null));
		assertTrue(action.execute(new String[] { "schnick", "schnack" }, null));

	}

	/**
	 * Tests for getMaximumParameters.
	 */
	@Test
	public void testGetMaximumParameters() {
		final AlterCreatureAction action = new AlterCreatureAction();
		assertThat(action.getMaximumParameters(), is(2));
	}

	/**
	 * Tests for getMinimumParameters.
	 */
	@Test
	public void testGetMinimumParameters() {
		final AlterCreatureAction action = new AlterCreatureAction();
		assertThat(action.getMinimumParameters(), is(2));
	}


}
