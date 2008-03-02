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
	public final void testInit() {
		ExpressionMatcher matcher = new ExpressionMatcher();

		assertTrue(matcher.isEmpty());
		assertFalse(matcher.isAnyFlagSet());
	}

	@Test
	public final void testParsing() {
		ExpressionMatcher matcher = new ExpressionMatcher();

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

	@Test
	public final void testTypeMatching() {
		ExpressionMatcher matcher = new ExpressionMatcher();

		Expression e1 = new Expression("abc", "VER");
		Expression e2 = new Expression("abc", "VER");
		Expression e3 = new Expression("ab", "VER");
		Expression e4 = new Expression("abc", "SUB");
		Expression e5 = new Expression("X", "SUB");

		matcher.setTypeMatching(false);
		matcher.setExactMatching(false);
		assertTrue(matcher.match(e1, e2));
		assertFalse(matcher.match(e1, e3));
		assertTrue(matcher.match(e1, e4));
		assertFalse(matcher.match(e1, e5));
		assertFalse(matcher.match(e4, e5));

		matcher.setTypeMatching(true);
		matcher.setExactMatching(false);
		assertTrue(matcher.match(e1, e2));
		assertFalse(matcher.match(e1, e3));
		assertFalse(matcher.match(e1, e4));
		assertFalse(matcher.match(e1, e5));
		assertFalse(matcher.match(e4, e5));

		matcher.setTypeMatching(true);
		matcher.setExactMatching(true);
		assertTrue(matcher.match(e1, e2));
		assertFalse(matcher.match(e1, e3));
		assertFalse(matcher.match(e1, e4));
		assertFalse(matcher.match(e1, e5));
		assertFalse(matcher.match(e4, e5));

		matcher.setTypeMatching(false);
		matcher.setExactMatching(true);
		assertTrue(matcher.match(e1, e2));
		assertFalse(matcher.match(e1, e3));
		assertTrue(matcher.match(e1, e4));
		assertFalse(matcher.match(e1, e5));
		assertFalse(matcher.match(e4, e5));
	}

}
