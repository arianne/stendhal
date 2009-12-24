package games.stendhal.client.actions;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import games.stendhal.client.MockStendhalClient;

import marauroa.common.game.RPAction;

import org.junit.BeforeClass;
import org.junit.Test;

public class AwayActionTest {
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
				assertEquals("away", action.get("type"));
				assertEquals("schnick", action.get("message"));
			}
		};

		final AwayAction action = new AwayAction();
		assertTrue(action.execute(null, "schnick"));
	}

	/**
	 * Tests for getMaximumParameters.
	 */
	@Test
	public void testGetMaximumParameters() {
		final AwayAction action = new AwayAction();
		assertThat(action.getMaximumParameters(), is(0));
	}

	/**
	 * Tests for getMinimumParameters.
	 */
	@Test
	public void testGetMinimumParameters() {
		final AwayAction action = new AwayAction();
		assertThat(action.getMinimumParameters(), is(0));
	}
}
