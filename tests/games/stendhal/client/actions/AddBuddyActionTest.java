package games.stendhal.client.actions;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import games.stendhal.client.MockStendhalClient;

import marauroa.common.game.RPAction;

import org.junit.BeforeClass;
import org.junit.Test;

public class AddBuddyActionTest {

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
				assertEquals("addbuddy", action.get("type"));
				assertEquals("schnick", action.get("target"));
			}
		};
		final AddBuddyAction action = new AddBuddyAction();
		assertFalse(action.execute(null, null));
		assertTrue(action.execute(new String []{"schnick"}, null));
	}

	/**
	 * Tests for getMaximumParameters.
	 */
	@Test
	public void testGetMaximumParameters() {
		final AddBuddyAction action = new AddBuddyAction();
		assertThat(action.getMaximumParameters(), is(1));
	}

	/**
	 * Tests for getMinimumParameters.
	 */
	@Test
	public void testGetMinimumParameters() {
		final AddBuddyAction action = new AddBuddyAction();
		assertThat(action.getMinimumParameters(), is(1));
	}

}
