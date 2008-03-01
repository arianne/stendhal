package games.stendhal.client.actions;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import games.stendhal.client.MockStendhalClient;

import marauroa.common.game.RPAction;

import org.junit.BeforeClass;
import org.junit.Test;

public class AnswerActionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testExecute() {
		new MockStendhalClient("") {
			@Override
			public void send(RPAction action) {
				client = null;
				assertEquals("answer", action.get("type"));
				assertEquals("schnick", action.get("text"));

			}
		};
		AnswerAction action = new AnswerAction();
		assertTrue(action.execute(null, "schnick"));
	}

	@Test
	public void testGetMaximumParameters() {
		AnswerAction action = new AnswerAction();
		assertThat(action.getMaximumParameters(), is(0));
	}

	@Test
	public void testGetMinimumParameters() {
		AnswerAction action = new AnswerAction();
		assertThat(action.getMinimumParameters(), is(0));
	}

}
