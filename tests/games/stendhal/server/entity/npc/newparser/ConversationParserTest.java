package games.stendhal.server.entity.npc.newparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
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
		assertEquals("cookie", sentence.getObjectName());
	}

	@Test
	public final void testVerboseAmount() {
		Sentence sentence = ConversationParser.parse("eat four cookies");
		assertFalse(sentence.hasError());

		assertEquals("eat", sentence.getVerb());
		assertEquals(4, sentence.getAmount());
		assertEquals("cookie", sentence.getObjectName());
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

	@Ignore
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
	public final void testPluralAndPrefix() {
		Sentence sentence = ConversationParser.parse("buy seven bananas");
		assertFalse(sentence.hasError());
		assertEquals("buy", sentence.getVerb());
		assertEquals(7, sentence.getAmount());
		assertEquals("banana", sentence.getItemName());

		sentence = ConversationParser.parse("buy a bottle of wine");
		assertFalse(sentence.hasError());
		assertEquals("buy", sentence.getVerb());
		assertEquals(1, sentence.getAmount());
		assertEquals("wine", sentence.getItemName());

		sentence = ConversationParser.parse("buy two pairs of trousers");
		assertFalse(sentence.hasError());
		assertEquals("buy", sentence.getVerb());
		assertEquals(2, sentence.getAmount());
		assertEquals("trouser", sentence.getItemName());
	}

	@Test
	public final void testPlease() {
		Sentence sentence = ConversationParser.parse("please open chest");
		assertFalse(sentence.hasError());
		assertEquals("open", sentence.getVerb());
		assertEquals(1, sentence.getAmount());
		assertEquals("chest", sentence.getObjectName());

		sentence = ConversationParser.parse("please please do me a favour");
		assertFalse(sentence.hasError());
		//TODO assertEquals("you", sentence.getSubject());
		assertEquals("do", sentence.getVerb());
		assertEquals("i", sentence.getSubject2());
		assertEquals(1, sentence.getAmount());
		assertEquals("favour", sentence.getObjectName());
	}

	@Test
	public final void testMe() {
		Sentence sentence = ConversationParser.parse("me");
		assertFalse(sentence.hasError());
		assertEquals("i", sentence.getSubject());
		assertNull(sentence.getVerb());
		assertNull(sentence.getSubject2());
	}

	@Test
	public final void testSubject2() {
		Sentence sentence = ConversationParser.parse("i love you");
		assertFalse(sentence.hasError());
		assertEquals("i", sentence.getSubject());
		assertEquals("love", sentence.getVerb());
		assertEquals("you", sentence.getSubject2());

		sentence = ConversationParser.parse("give me 4 fishes");
		assertFalse(sentence.hasError());
		assertEquals("i", sentence.getSubject());
		assertEquals("buy", sentence.getVerb());
		assertNull(sentence.getSubject2());
		assertEquals(4, sentence.getAmount());
		assertEquals("fish", sentence.getObjectName());

		sentence = ConversationParser.parse("i would like to have an ice");
		assertFalse(sentence.hasError());
		assertEquals("i", sentence.getSubject());
		assertEquals("buy", sentence.getVerb());
		assertNull(sentence.getSubject2());
		assertEquals(1, sentence.getAmount());
		assertEquals("ice cream", sentence.getObjectName());
	}

	@Test
	public final void testPunctuation() {
		Sentence sentence = ConversationParser.parse("give me 4 fishes, please");
		assertFalse(sentence.hasError());
		assertEquals("i", sentence.getSubject());
		assertEquals("buy", sentence.getVerb());
		assertNull(sentence.getSubject2());
		assertEquals(4, sentence.getAmount());
		assertEquals("fish", sentence.getObjectName());
	}
}
