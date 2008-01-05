package games.stendhal.server.entity.npc.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test the NPC conversation Sentence class.
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
		sentence.classifyWords(parser, false);
		assertFalse(sentence.hasError());
		assertEquals("quick/ADJ brown/ADJ-COL fox/SUB-ANI jump/VER over/PRE lazy/ADJ dog/SUB-ANI.",
				sentence.toString());

		sentence.mergeWords();
		assertEquals("quick brown fox/SUB-ANI-COL jump/VER over/PRE lazy dog/SUB-ANI.", sentence.toString());
		assertEquals(Sentence.ST_STATEMENT, sentence.getType());

		sentence = new Sentence();
		parser = new ConversationParser("does it fit");
		sentence.parse(parser);
		sentence.classifyWords(parser, false);
		assertFalse(sentence.hasError());
		assertEquals("do/VER it/OBJ-PRO fit/VER", sentence.toString());
		assertEquals(Sentence.ST_QUESTION, sentence.evaluateSentenceType());
		assertEquals("it/OBJ-PRO fit/VER?", sentence.toString());
	}

	@Test
	public final void testSentenceType() {
		Sentence sentence = ConversationParser.parse("buy banana!");
		assertFalse(sentence.hasError());
		assertEquals(Sentence.ST_IMPERATIVE, sentence.getType());
		assertEquals("buy", sentence.getVerb(0).getNormalized());
		assertEquals("banana", sentence.getObject(0).getNormalized());

		// TODO mf - transfer into "buy banana"
		sentence = ConversationParser.parse("do you have a banana for me?");
		assertFalse(sentence.hasError());
		assertEquals(Sentence.ST_QUESTION, sentence.getType());
		assertEquals("have", sentence.getVerb(0).getNormalized());
		assertEquals("banana", sentence.getObject(0).getNormalized());

		sentence = ConversationParser.parse("how are you?");
		assertFalse(sentence.hasError());
		assertEquals("is/VER-PLU-QUE you/SUB-PRO?", sentence.toString());
		assertEquals(Sentence.ST_QUESTION, sentence.getType());

		sentence = ConversationParser.parse("this is a banana.");
		assertFalse(sentence.hasError());
		assertEquals("this/OBJ is/VER banana/OBJ-FOO.", sentence.toString());
		assertEquals(Sentence.ST_STATEMENT, sentence.getType());
		assertEquals("this", sentence.getObject(0).getNormalized());
		assertEquals("is", sentence.getVerb(0).getNormalized());
		assertEquals("banana", sentence.getObject(1).getNormalized());
	}

	@Test
	public final void testEnumerations() {
		Sentence sentence = ConversationParser.parse("it is raining cats and dogs");
		assertFalse(sentence.hasError());
		assertEquals("rain/VER-GER cat/SUB-ANI-PLU, dog/SUB-ANI-PLU.", sentence.toString());
		assertEquals(Sentence.ST_STATEMENT, sentence.getType());
		assertEquals("rain", sentence.getVerb(0).getNormalized());
		assertEquals("cat", sentence.getSubject(0).getNormalized());
		assertEquals("dog", sentence.getSubject(1).getNormalized());
	}

	@Test
	public final void testComparison() {
		Sentence s1 = ConversationParser.parse("it is raining cats and dogs");
		Sentence s2 = ConversationParser.parse("it is raining cats, dogs");
		Sentence s3 = ConversationParser.parse("it is raining cats but no dogs");
		assertFalse(s1.hasError());
		assertFalse(s2.hasError());
		assertFalse(s3.hasError());

		assertTrue(s1.equalsNormalized(s1));
		assertTrue(s1.equalsNormalized(s2));
		assertFalse(s1.equalsNormalized(s3));
	}

	@Test
	public final void testMatching() {
		Sentence s1 = ConversationParser.parse("buy banana");
		assertFalse(s1.hasError());

		Sentence m1 = ConversationParser.parseForMatching("buy OBJ");
		Sentence m2 = ConversationParser.parseForMatching("buy SUB");
		assertFalse(m1.hasError());
		assertFalse(m2.hasError());

		assertTrue(s1.matches(m1));
		assertFalse(s1.matches(m2));

		s1 = ConversationParser.parse("make apple pie");
		assertFalse(s1.hasError());

		m1 = ConversationParser.parseForMatching("VER *pie");
		m2 = ConversationParser.parseForMatching("VER *cookie");
		assertFalse(m1.hasError());
		assertFalse(m2.hasError());

		assertTrue(s1.matches(m1));
		assertFalse(s1.matches(m2));
	}

}
