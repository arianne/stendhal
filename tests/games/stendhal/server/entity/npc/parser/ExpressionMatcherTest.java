package games.stendhal.server.entity.npc.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test the ExpressionMatcher class.
 * 
 * @author Martin Fuchs
 */
public class ExpressionMatcherTest {

	@Test
	public final void testMatcher() {
		ExpressionMatcher matcher = new ExpressionMatcher();

		assertTrue(matcher.isEmpty());
		assertFalse(matcher.isAnyFlagSet());

		Sentence sentence = matcher.parseSentence("", new ConversationContext());
		assertFalse(sentence.hasError());
		assertEquals("", sentence.toString());

		String str = matcher.readMatchingFlags("Lazy dog");
		assertEquals("Lazy dog", str);
		assertTrue(matcher.isEmpty());

		sentence = matcher.parseSentence(str, new ConversationContext());
		assertFalse(sentence.hasError());
		assertEquals("lazy dog/SUB-ANI", sentence.toString());

		str = matcher.readMatchingFlags("|TYPE|abcdef/OBJ");
		assertEquals("abcdef/OBJ", str);
		assertTrue(matcher.isAnyFlagSet());
		assertFalse(matcher.getExactMatching());
		assertTrue(matcher.getTypeMatching());

		sentence = matcher.parseSentence(str, new ConversationContext());
		assertFalse(sentence.hasError());
		assertEquals("abcdef/OBJ", sentence.toString());

		str = matcher.readMatchingFlags("|EXACT|Hello world!");
		assertEquals("Hello world!", str);
		assertTrue(matcher.isAnyFlagSet());
		assertTrue(matcher.getExactMatching());
		assertFalse(matcher.getTypeMatching());

		sentence = matcher.parseSentence(str, new ConversationContext());
		assertFalse(sentence.hasError());
		assertEquals("Hello world!", sentence.toString());
	}

}
