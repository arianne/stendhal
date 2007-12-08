package games.stendhal.client.scripting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * test SlashActionParser class
 *
 * @author Martin Fuchs
 */
public class SlashActionParserTest {

	@Test
	public final void test() {
		SlashActionCommand cmd = SlashActionParser.parse("who");
		assertFalse(cmd.getError());
		assertEquals("who", cmd.getName());
		assertEquals("", cmd.getRemainder());
		assertNotNull(cmd.getParams());
		assertEquals(1, cmd.getParams().length);
		assertNull(cmd.getParams()[0]);

		cmd = SlashActionParser.parse("where ghost");
		assertFalse(cmd.getError());
		assertEquals("where", cmd.getName());
		assertEquals("", cmd.getRemainder());
		assertNotNull(cmd.getParams());
		assertEquals(1, cmd.getParams().length);
		assertEquals("ghost", cmd.getParams()[0]);

		cmd = SlashActionParser.parse("where 'player 2'");
		assertFalse(cmd.getError());
		assertEquals("where", cmd.getName());
		assertEquals("", cmd.getRemainder());
		assertNotNull(cmd.getParams());
		assertEquals(1, cmd.getParams().length);
		assertEquals("player 2", cmd.getParams()[0]);
	}

	@Test
	public final void testError() {
		SlashActionCommand cmd = SlashActionParser.parse("");
		assertTrue(cmd.getError());

		cmd = SlashActionParser.parse("where 'abc");
		assertTrue(cmd.getError());
	}

}
