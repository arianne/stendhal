package games.stendhal.server.entity.npc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class ConversationParserTest {

	@Test
	public final void testAmount() {
		ConversationParser parser = new ConversationParser("buy 3 cookies");

		assertFalse(parser.getError());
		assertEquals(3, parser.readAmount());
		assertEquals("cookies", parser.readObjectName());
	}

	@Test
	public final void testCase() {
		ConversationParser parser = new ConversationParser("buy Bread");

		assertFalse(parser.getError());
		assertEquals(1, parser.readAmount());
		assertEquals("bread", parser.readObjectName());
	}

	@Test
	public final void testItemName() {
		ConversationParser parser = new ConversationParser("buy fresh_fish");

		assertFalse(parser.getError());
		assertEquals("fresh_fish", parser.readObjectName());
	}

	@Test
	public final void testError() {
		ConversationParser parser = new ConversationParser("hello world");
		assertFalse(parser.getError());

		parser = new ConversationParser("");
		assertFalse(parser.getError());

		parser = new ConversationParser("buy -10 cars");
		assertEquals(-10, parser.readAmount());
		assertTrue(parser.getError());
	}

}
