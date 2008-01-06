package games.stendhal.server.entity.npc.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test the NPC ConversationParser class.
 *
 * @author Martin Fuchs
 */
public class ConversationParserTest {

	@Test
	public final void testAmount() {
		Sentence sentence = ConversationParser.parse("buy 3 cookies");
		assertFalse(sentence.hasError());

		assertEquals("buy", sentence.getVerbString());
		assertEquals("buy", sentence.getTriggerExpression().getNormalized());
		assertEquals(3, sentence.getObject(0).getAmount());
		assertEquals("cookie", sentence.getObjectName());
	}

	@Test
	public final void testVerboseAmount() {
		Sentence sentence = ConversationParser.parse("eat four cookies");
		assertFalse(sentence.hasError());

		assertEquals("eat", sentence.getVerbString());
		assertEquals(4, sentence.getObject(0).getAmount());
		assertEquals("cookie", sentence.getObjectName());
	}

	@Test
	public final void testCase() {
		Sentence sentence = ConversationParser.parse("buy Bread");
		assertFalse(sentence.hasError());

		assertEquals("buy", sentence.getVerbString());
		assertEquals("buy", sentence.getTriggerExpression().getNormalized());
		assertEquals(1, sentence.getObject(0).getAmount());
		assertEquals("bread", sentence.getObjectName());
//		assertEquals("bread", sentence.getItemName());
		assertEquals("buy Bread", sentence.getOriginalText());
		assertEquals("buy bread", sentence.getNormalized());
	}

	@Test
	public final void testSpaceHandling() {
		Sentence sentence = ConversationParser.parse("drop  three \tmeat");
		assertFalse(sentence.hasError());
		assertEquals("drop", sentence.getVerbString());
		assertEquals("drop/VER", sentence.getVerb().getNormalizedWithTypeString());
		assertEquals("drop", sentence.getTriggerExpression().getNormalized());
		assertEquals(3, sentence.getObject(0).getAmount());
		assertEquals("meat", sentence.getObjectName());
		assertEquals("drop three meat", sentence.getOriginalText());
		assertEquals("drop meat", sentence.getNormalized());

		sentence = ConversationParser.parse(" sell house   ");
		assertFalse(sentence.hasError());
		assertEquals("sell", sentence.getVerbString());
		assertEquals("sell", sentence.getTriggerExpression().getNormalized());
		assertEquals(1, sentence.getObject(0).getAmount());
		assertEquals("house", sentence.getObjectName());
//		assertEquals("house", sentence.getItemName());
		assertEquals("sell house", sentence.getNormalized());
	}

	@Test
	public final void testItemName() {
		Sentence sentence = ConversationParser.parse("buy fresh fish");
		assertFalse(sentence.hasError());
		assertEquals("buy", sentence.getVerbString());
		assertEquals("fresh fish", sentence.getObjectName());

// Underscores in item names are now no more required.
//		sentence = ConversationParser.parse("buy fresh_fish");
//		assertEquals("buy", sentence.getVerbString());
//		assertEquals("fresh fish", sentence.getObjectName());
//		assertEquals("fresh fish", sentence.getItemName());
	}

	@Test
	public final void testError() {
		Sentence sentence = ConversationParser.parse("hello world");
		assertFalse(sentence.hasError());

		sentence = ConversationParser.parse("");
		assertFalse(sentence.hasError());

		sentence = ConversationParser.parse("buy -10 cars");
		assertEquals(-10, sentence.getObject(0).getAmount());
		assertTrue(sentence.hasError());
		assertEquals("negative amount: -10", sentence.getErrorString());
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

		assertEquals("put", sentence.getVerbString());
		assertEquals(1, sentence.getObject(0).getAmount());
		assertEquals("dish", sentence.getObject(0).getNormalized());
		assertEquals("on", sentence.getPreposition(0).getNormalized());
		assertEquals("table", sentence.getObject(1).getNormalized());
	}

	@Test
	public final void testPluralAndPrefix() {
		Sentence sentence = ConversationParser.parse("buy seven bananas");
		assertFalse(sentence.hasError());
		assertEquals("buy", sentence.getVerbString());
		assertEquals(7, sentence.getObject(0).getAmount());
		assertEquals("banana", sentence.getObject(0).getNormalized());

		sentence = ConversationParser.parse("buy a bottle of wine");
		assertFalse(sentence.hasError());
		assertEquals("buy", sentence.getVerbString());
		assertEquals(1, sentence.getObject(0).getAmount());
		assertEquals("wine", sentence.getObject(0).getNormalized());

		sentence = ConversationParser.parse("buy two pairs of trousers");
		assertFalse(sentence.hasError());
		assertEquals("buy", sentence.getVerbString());
		assertEquals(2, sentence.getObject(0).getAmount());
		assertEquals("trouser", sentence.getObject(0).getNormalized());
	}

	@Test
	public final void testPlease() {
		Sentence sentence = ConversationParser.parse("please open chest");
		assertFalse(sentence.hasError());
		assertEquals("open", sentence.getVerbString());
		assertEquals(1, sentence.getObject(0).getAmount());
		assertEquals("chest", sentence.getObject(0).getNormalized());

		sentence = ConversationParser.parse("please please do me a favour");
		assertFalse(sentence.hasError());
		assertEquals("you", sentence.getSubject(0).getNormalized());
		assertEquals("do", sentence.getVerbString());
		assertEquals("i", sentence.getSubject(1).getNormalized());
		assertEquals(1, sentence.getObject(0).getAmount());
		assertEquals("favour", sentence.getObject(0).getNormalized());
	}

	@Test
	public final void testMe() {
		Sentence sentence = ConversationParser.parse("me");
		assertFalse(sentence.hasError());
		assertEquals("i", sentence.getSubject(0).getNormalized());
		assertNull(sentence.getVerbString());
		assertNull(sentence.getSubject(1));
	}

	@Test
	public final void testTwoSubjects() {
		Sentence sentence = ConversationParser.parse("i love you");
		assertFalse(sentence.hasError());
		assertEquals("i", sentence.getSubject(0).getNormalized());
		assertEquals("love", sentence.getVerbString());
		assertEquals("you", sentence.getSubject(1).getNormalized());
		assertEquals("i love you", sentence.getNormalized());

		sentence = ConversationParser.parse("give me 4 fishes");
		assertFalse(sentence.hasError());
		assertEquals("buy", sentence.getVerbString());
		assertNull(sentence.getSubject(1));
		assertEquals(4, sentence.getObject(0).getAmount());
		assertEquals("fish", sentence.getObjectName());
		assertEquals("buy fish!", sentence.getNormalized());

		sentence = ConversationParser.parse("i would like to have an ice cream");
		assertFalse(sentence.hasError());
		assertEquals("i", sentence.getSubject(0).getNormalized());
		assertEquals("buy", sentence.getVerbString());
		assertNull(sentence.getSubject(1));
		assertEquals(1, sentence.getObject(0).getAmount());
		assertEquals("ice cream", sentence.getObjectName());
		assertEquals("i buy ice cream!", sentence.getNormalized());
	}

	@Test
	public final void testPunctuation() {
		//TODO mf - also handle "if i may ask, give me 4 fishes, please"
		Sentence sentence = ConversationParser.parse("give me 4 fishes, please");
		assertFalse(sentence.hasError());
		assertEquals("buy", sentence.getVerbString());
		assertNull(sentence.getSubject(1));
		assertEquals(4, sentence.getObject(0).getAmount());
		assertEquals("fish", sentence.getObjectName());
		assertEquals("buy fish!", sentence.getNormalized());
	}

	@Test
	public final void testCompoundWords() {
		Sentence sentence = ConversationParser.parse("take golden ring");
		assertFalse(sentence.hasError());
		assertEquals("take", sentence.getVerbString());
		assertEquals("golden ring", sentence.getObjectName());
		assertEquals("take golden ring", sentence.getNormalized());
		assertEquals("take/VER golden ring/OBJ-COL", sentence.toString());

		sentence = ConversationParser.parse("take ring golden");
		assertFalse(sentence.hasError());
		assertEquals("take", sentence.getVerbString());
		assertEquals("ring", sentence.getObjectName());
		assertEquals("take ring golden", sentence.getNormalized());
		assertEquals("take/VER ring/OBJ golden/ADJ-COL", sentence.toString());
	}

	@Test
	public final void testSmilies() {
		Sentence sentence = ConversationParser.parse(":-)");
		assertFalse(sentence.hasError());
		assertEquals("smile", sentence.getVerbString());
		assertEquals("smile", sentence.getNormalized());
		assertEquals("smile/VER", sentence.toString());

		sentence = ConversationParser.parse(":)");
		assertFalse(sentence.hasError());
		assertEquals("smile", sentence.getVerbString());
		assertEquals("smile", sentence.getNormalized());
		assertEquals("smile/VER", sentence.toString());

		sentence = ConversationParser.parse(":*");
		assertFalse(sentence.hasError());
		assertEquals("kiss", sentence.getVerbString());
		assertEquals("kiss", sentence.getNormalized());
		assertEquals("kiss/VER", sentence.toString());
	}

	@Test
	public final void testNullPointer() {
		Sentence sentence = ConversationParser.parse(null);
		assertNotNull(sentence);
		assertEquals(0, sentence.getSubjectCount());
		assertNull(sentence.getSubject(0));
		assertEquals(0, sentence.getVerbCount());
		assertNull(sentence.getVerb(0));
		assertEquals(0, sentence.getObjectCount());
		assertNull(sentence.getObject(0));
		assertNull(sentence.getObjectName());
		assertFalse(sentence.hasError());
	}

}
