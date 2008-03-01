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

	@Test
	public void testExecute() {
		new MockStendhalClient("") {
			@Override
			public void send(RPAction action) {
				client = null;
				assertEquals("away", action.get("type"));
				assertEquals("schnick", action.get("message"));
			}
		};

		AwayAction action = new AwayAction();
		assertTrue(action.execute(null, "schnick"));
	}

	@Test
	public void testGetMaximumParameters() {
		AwayAction action = new AwayAction();
		assertThat(action.getMaximumParameters(), is(0));
	}

	@Test
	public void testGetMinimumParameters() {
		AwayAction action = new AwayAction();
		assertThat(action.getMinimumParameters(), is(0));
	}
}
