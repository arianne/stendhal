package games.stendhal.client.scripting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import games.stendhal.client.actions.SlashActionRepository;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.QuestHelper;

/**
 * test SlashActionParser class
 *
 * @author Martin Fuchs
 */
public class SlashActionParserTest {

	@BeforeClass
	public static void setupclass() {
		QuestHelper.setUpBeforeClass();
		SlashActionRepository.register();
	}

	@Test
	public final void test() {
		SlashActionCommand cmd = SlashActionParser.parse("who");
		assertFalse(cmd.hasError());
		assertEquals("who", cmd.getName());
		assertNotNull(cmd.getParams());
		assertEquals(0, cmd.getParams().length);
		assertEquals("", cmd.getRemainder());

		cmd = SlashActionParser.parse("unknown-command");
		assertFalse(cmd.hasError());
		assertEquals("unknown-command", cmd.getName());
		assertNotNull(cmd.getParams());
		assertEquals(1, cmd.getParams().length);
		assertNull("", cmd.getParams()[0]);
		assertEquals("", cmd.getRemainder());

		cmd = SlashActionParser.parse("where ghost");
		assertFalse(cmd.hasError());
		assertEquals("where", cmd.getName());
		assertNotNull(cmd.getParams());
		assertEquals(1, cmd.getParams().length);
		assertEquals("ghost", cmd.getParams()[0]);
		assertEquals("", cmd.getRemainder());

		cmd = SlashActionParser.parse("adminlevel player 100");
		assertFalse(cmd.hasError());
		assertEquals("adminlevel", cmd.getName());
		assertNotNull(cmd.getParams());
		assertEquals(2, cmd.getParams().length);
		assertEquals("player", cmd.getParams()[0]);
		assertEquals("100", cmd.getParams()[1]);
		assertEquals("", cmd.getRemainder());

		cmd = SlashActionParser.parse("jail player minutes reason");
		assertFalse(cmd.hasError());
		assertEquals("jail", cmd.getName());
		assertNotNull(cmd.getParams());
		assertEquals(2, cmd.getParams().length);
		assertEquals("player", cmd.getParams()[0]);
		assertEquals("minutes", cmd.getParams()[1]);
		assertEquals("reason", cmd.getRemainder());

		cmd = SlashActionParser.parse("/hello, how are you?");
		assertFalse(cmd.hasError());
		assertEquals("/", cmd.getName());
		assertNotNull(cmd.getParams());
		assertEquals(0, cmd.getParams().length);
		assertEquals("hello, how are you?", cmd.getRemainder());

		cmd = SlashActionParser.parse("/ thanks, I'm fine");
		assertFalse(cmd.hasError());
		assertEquals("/", cmd.getName());
		assertNotNull(cmd.getParams());
		assertEquals(0, cmd.getParams().length);
		assertEquals("thanks, I'm fine", cmd.getRemainder());
	}

	@Test
	public final void testQuoting() {
		SlashActionCommand cmd = SlashActionParser.parse("where 'player 2'");
		assertFalse(cmd.hasError());
		assertEquals("where", cmd.getName());
		assertNotNull(cmd.getParams());
		assertEquals(1, cmd.getParams().length);
		assertEquals("player 2", cmd.getParams()[0]);
		assertEquals("", cmd.getRemainder());

		cmd = SlashActionParser.parse("say \"i don't speak\"");
		assertFalse(cmd.hasError());
		assertEquals("say", cmd.getName());
		assertNotNull(cmd.getParams());
		assertEquals(1, cmd.getParams().length);
		assertEquals("i don't speak", cmd.getParams()[0]);
		assertEquals("", cmd.getRemainder());
	}

	@Test
	public final void testError() {
		SlashActionCommand cmd = SlashActionParser.parse("");
		assertTrue(cmd.hasError());
		assertEquals("missing slash command", cmd.getError());

		cmd = SlashActionParser.parse("where 'abc");
		assertTrue(cmd.hasError());
		assertEquals("unterminated quote", cmd.getError());
	}

}
