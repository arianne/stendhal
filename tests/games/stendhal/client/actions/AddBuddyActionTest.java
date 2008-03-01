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

	@Test
	public void testExecute() {
		new MockStendhalClient("") {
			@Override
			public void send(RPAction action) {
				client = null;
				assertEquals("addbuddy", action.get("type"));
				assertEquals("schnick", action.get("target"));
			}
		};
		AddBuddyAction action = new AddBuddyAction();
		assertFalse(action.execute(null, null));
		assertTrue(action.execute(new String []{"schnick"}, null));
	}

	@Test
	public void testGetMaximumParameters() {
		AddBuddyAction action = new AddBuddyAction();
		assertThat(action.getMaximumParameters(), is(1));
	}

	@Test
	public void testGetMinimumParameters() {
		AddBuddyAction action = new AddBuddyAction();
		assertThat(action.getMinimumParameters(), is(1));
	}

}
