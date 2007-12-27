package games.stendhal.server.entity.npc.newparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

/**
 * test ConversationParser class
 *
 * @author Martin Fuchs
 */
public class SentenceTest {

	@Test
	public final void testGrammar() {
		Sentence sentence = new Sentence();
		String text = ConversationParser.getSentenceType("The quick brown fox jumps over the lazy dog.", sentence);
		ConversationParser parser = new ConversationParser(text);
		sentence.parse(parser);
		sentence.classifyWords(parser);
		assertFalse(sentence.hasError());
		assertEquals("the/IGN quick/ADJ brown/ADJ-COL fox/NOU-ANI jump/VER over/PRE the/IGN lazy/ADJ dog/NOU-ANI .", sentence.toString());

		sentence.mergeWords();
		assertEquals("fox/NOU-ANI-COL jump/VER over/PRE dog/NOU-ANI .", sentence.toString());
		assertEquals(Sentence.ST_STATEMENT, sentence.getType());

		sentence = new Sentence();
		parser = new ConversationParser("does it fit");
		sentence.parse(parser);
		sentence.classifyWords(parser);
		assertFalse(sentence.hasError());
		assertEquals("do/VER it/NOU-OBJ fit/VER", sentence.toString());
		assertEquals(Sentence.ST_QUESTION, sentence.evaluateSentenceType());
		assertEquals("it/NOU-OBJ fit/VER ?", sentence.toString());
	}

	@Test
	public final void testSentenceType() {
		Sentence sentence = ConversationParser.parse("buy banana!");
		assertFalse(sentence.hasError());
		assertEquals(Sentence.ST_IMPERATIVE, sentence.getType());
		assertEquals("buy", sentence.getVerb());
		assertEquals("banana", sentence.getObjectName());

		sentence = ConversationParser.parse("do you have a banana for me?");
		assertFalse(sentence.hasError());
		assertEquals(Sentence.ST_QUESTION, sentence.getType());
		assertEquals("have", sentence.getVerb());
		assertEquals("banana", sentence.getObjectName());

		sentence = ConversationParser.parse("how are you?");
		assertFalse(sentence.hasError());
		assertEquals("is/VER-PLU-QUE you/NOU-PER ?", sentence.toString());
		assertEquals(Sentence.ST_QUESTION, sentence.getType());

		sentence = ConversationParser.parse("this is a banana.");
		assertFalse(sentence.hasError());
		assertEquals(Sentence.ST_STATEMENT, sentence.getType());
		assertEquals("is", sentence.getVerb());
		assertEquals("banana", sentence.getObjectName());
	}

}
