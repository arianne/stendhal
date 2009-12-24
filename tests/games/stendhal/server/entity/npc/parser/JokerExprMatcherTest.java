package games.stendhal.server.entity.npc.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test the JokerExprMatcher class.
 * 
 * @author Martin Fuchs
 */
public class JokerExprMatcherTest {

	@Test
	public final void testJokerMatching() {
		final ExpressionMatcher matcher = new JokerExprMatcher();

		final Expression e1 = new Expression("abc", "VER");
		final Expression e2 = new Expression("a*c", "VER");
		final Expression e3 = new Expression("ab", "VER");
		final Expression e4 = new Expression("ab*", "SUB");
		final Expression e5 = new Expression("X", "SUB");

		assertTrue(matcher.match(e1, e2));
		assertFalse(matcher.match(e1, e3));
		assertTrue(matcher.match(e1, e4));
		assertFalse(matcher.match(e1, e5));
		assertFalse(matcher.match(e4, e5));
	}

	/**
	 * Tests for sentenceMatching.
	 */
	@Test
	public final void testSentenceMatching() {
		final Sentence m1 = ConversationParser.parseForMatching("|JOKER|h*llo");
		assertFalse(m1.hasError());
		assertEquals("|JOKER|h*llo", m1.toString());

		assertEquals(true, ConversationParser.parse("hello").matchesFull(m1));
		assertEquals(true, ConversationParser.parse("hallo").matchesFull(m1));
		assertEquals(false, ConversationParser.parse("hailo").matchesFull(m1));
	}

}
