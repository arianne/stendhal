package games.stendhal.client.actions;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import games.stendhal.client.MockStendhalClient;

import marauroa.common.game.RPAction;

import org.junit.BeforeClass;
import org.junit.Test;

public class WhoActionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testExecute() {
		new MockStendhalClient("") {
			@Override
			public void send(RPAction action) {
				client = null;
				for (String attrib : action) {
					assertEquals("type", attrib);
					assertEquals("who", (action.get(attrib)));
				}
			}
		};
		WhoAction action = new WhoAction();
		assertTrue(action.execute(null, null));
	}

	@Test
	public void testGetMaximumParameters() {
		WhoAction action = new WhoAction();
		assertThat(action.getMaximumParameters(), is(0));
	}

	@Test
	public void testGetMinimumParameters() {
		WhoAction action = new WhoAction();
		assertThat(action.getMinimumParameters(), is(0));
	}

}
