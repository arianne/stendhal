package games.stendhal.client.actions;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import games.stendhal.client.MockStendhalClient;

import marauroa.common.game.RPAction;

import org.junit.BeforeClass;
import org.junit.Test;

public class AdminLevelActionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testExecuteOneParam() {
		new MockStendhalClient("") {
			@Override
			public void send(RPAction action) {
				client = null;
				assertEquals("adminlevel", action.get("type"));
				assertEquals("schnick", action.get("target"));
			}
		};
		AdminLevelAction action = new AdminLevelAction();
		assertFalse(action.execute(null, null));
		assertTrue(action.execute(new String[] { "schnick" }, null));
	}

	@Test
	public void testExecuteSecondParamNull() {

		new MockStendhalClient("") {
			@Override
			public void send(RPAction action) {
				client = null;
				assertEquals("adminlevel", action.get("type"));
				assertEquals("schnick", action.get("target"));
				assertFalse(action.has("newlevel"));
			}
		};
		AdminLevelAction action = new AdminLevelAction();
		assertFalse(action.execute(null, null));
		assertTrue(action.execute(new String[] { "schnick", null }, null));
	}

	@Test
	public void testExecuteSecondParamValid() {

		new MockStendhalClient("") {
			@Override
			public void send(RPAction action) {
				client = null;
				assertEquals("adminlevel", action.get("type"));
				assertEquals("schnick", action.get("target"));
				assertEquals("100", action.get("newlevel"));
			}
		};
		AdminLevelAction action = new AdminLevelAction();
		assertFalse(action.execute(null, null));
		assertTrue(action.execute(new String[] { "schnick", "100" }, null));
	}

	@Test
	public void testGetMaximumParameters() {
		AdminLevelAction action = new AdminLevelAction();
		assertThat(action.getMaximumParameters(), is(2));
	}

	@Test
	public void testGetMinimumParameters() {
		AdminLevelAction action = new AdminLevelAction();
		assertThat(action.getMinimumParameters(), is(1));
	}

}
