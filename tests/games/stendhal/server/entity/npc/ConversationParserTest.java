package games.stendhal.server.entity.npc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * test ConversationParser class
 *
 * @author martinf
 */
public class ConversationParserTest {

	@Test
	public final void testAmount() {
		Sentence sentence = ConversationParser.parse("buy 3 cookies");
		assertFalse(sentence.getError());

		assertEquals("buy", sentence.getVerb());
		assertEquals(3, sentence.getAmount());
		assertEquals("cookies", sentence.getObjectName());
	}

	@Test
	public final void testVerboseAmount() {
		Sentence sentence = ConversationParser.parse("eat four cookies");
		assertFalse(sentence.getError());

		assertEquals("eat", sentence.getVerb());
		assertEquals(4, sentence.getAmount());
		assertEquals("cookies", sentence.getObjectName());
	}

	@Test
	public final void testCase() {
		Sentence sentence = ConversationParser.parse("buy Bread");
		assertFalse(sentence.getError());

		assertEquals("buy", sentence.getVerb());
		assertEquals(1, sentence.getAmount());
		assertEquals("bread", sentence.getObjectName());
		assertEquals("bread", sentence.getItemName());
	}

	@Test
	public final void testSpaceHandling() {
		Sentence sentence = ConversationParser.parse("drop  three \tchicken");
		assertFalse(sentence.getError());
		assertEquals("drop", sentence.getVerb());
		assertEquals(3, sentence.getAmount());
		assertEquals("chicken", sentence.getObjectName());

		sentence = ConversationParser.parse(" sell house   ");
		assertFalse(sentence.getError());
		assertEquals("sell", sentence.getVerb());
		assertEquals(1, sentence.getAmount());
		assertEquals("house", sentence.getObjectName());
		assertEquals("house", sentence.getItemName());
	}

	@Test
	public final void testItemName() {
		Sentence sentence = ConversationParser.parse("buy fresh_fish");
		assertFalse(sentence.getError());
		assertEquals("buy", sentence.getVerb());
		assertEquals("fresh_fish", sentence.getItemName());

		sentence = ConversationParser.parse("buy fresh fish");
		assertEquals("buy", sentence.getVerb());
		assertEquals("fresh fish", sentence.getObjectName());
		assertEquals("fresh_fish", sentence.getItemName());
	}

	@Test
	public final void testError() {
		Sentence sentence = ConversationParser.parse("hello world");
		assertFalse(sentence.getError());

		sentence = ConversationParser.parse("");
		assertFalse(sentence.getError());

		sentence = ConversationParser.parse("buy -10 cars");
		assertEquals(-10, sentence.getAmount());
		assertTrue(sentence.getError());
	}

}
