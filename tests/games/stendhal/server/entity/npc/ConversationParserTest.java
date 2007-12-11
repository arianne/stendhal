package games.stendhal.server.entity.npc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * test ConversationParser class
 *
 * @author Martin Fuchs
 */
public class ConversationParserTest {

	@Test
	public final void testAmount() {
		Sentence sentence = ConversationParser.parse("buy 3 cookies");
		assertFalse(sentence.hasError());

		assertEquals("buy", sentence.getVerb());
		assertEquals(3, sentence.getAmount());
		assertEquals("cooky", sentence.getObjectName());
	}

	@Test
	public final void testVerboseAmount() {
		Sentence sentence = ConversationParser.parse("eat four cookies");
		assertFalse(sentence.hasError());

		assertEquals("eat", sentence.getVerb());
		assertEquals(4, sentence.getAmount());
		assertEquals("cooky", sentence.getObjectName());
	}

	@Test
	public final void testCase() {
		Sentence sentence = ConversationParser.parse("buy Bread");
		assertFalse(sentence.hasError());

		assertEquals("buy", sentence.getVerb());
		assertEquals(1, sentence.getAmount());
		assertEquals("bread", sentence.getObjectName());
		assertEquals("bread", sentence.getItemName());
		assertEquals("buy Bread", sentence.getOriginalText());
	}

	@Test
	public final void testSpaceHandling() {
		Sentence sentence = ConversationParser.parse("drop  three \tchicken");
		assertFalse(sentence.hasError());
		assertEquals("drop", sentence.getVerb());
		assertEquals(3, sentence.getAmount());
		assertEquals("chicken", sentence.getObjectName());
		assertEquals("drop three chicken", sentence.getOriginalText());

		sentence = ConversationParser.parse(" sell house   ");
		assertFalse(sentence.hasError());
		assertEquals("sell", sentence.getVerb());
		assertEquals(1, sentence.getAmount());
		assertEquals("house", sentence.getObjectName());
		assertEquals("house", sentence.getItemName());
	}

	@Test
	public final void testItemName() {
		Sentence sentence = ConversationParser.parse("buy fresh_fish");
		assertFalse(sentence.hasError());
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
		assertFalse(sentence.hasError());

		sentence = ConversationParser.parse("");
		assertFalse(sentence.hasError());

		sentence = ConversationParser.parse("buy -10 cars");
		assertEquals(-10, sentence.getAmount());
		assertTrue(sentence.hasError());
		assertEquals("negative amount: -10", sentence.getError());
	}

	@Test
	public final void testIsEmpty() {
		Sentence sentence = ConversationParser.parse("");
		assertFalse(sentence.hasError());
		assertTrue(sentence.isEmpty());

		sentence = ConversationParser.parse("hello");
		assertFalse(sentence.hasError());
		assertFalse(sentence.isEmpty());
	}

	@Test
	public final void testPrepositions() {
		Sentence sentence = ConversationParser.parse("put dish on table");
		assertFalse(sentence.hasError());

		assertEquals("put", sentence.getVerb());
		assertEquals(1, sentence.getAmount());
		assertEquals("dish", sentence.getObjectName());
		assertEquals("on", sentence.getPreposition());
		assertEquals("table", sentence.getObjectName2());
	}

	@Test
	public final void testPlural() {
		Sentence sentence = ConversationParser.parse("buy seven bananas");
		assertFalse(sentence.hasError());
		assertEquals("buy", sentence.getVerb());
		assertEquals(7, sentence.getAmount());
		assertEquals("banana", sentence.getItemName());
	}

}
