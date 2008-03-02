package games.stendhal.server.entity.npc.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test the NPC conversation parser Expression class.
 *
 * @author Martin Fuchs
 */
public class ExpressionTest {

	@Test
	public final void testMatch() {
		Expression expr1 = ConversationParser.createTriggerExpression("cloak");
		Expression expr2 = ConversationParser.createTriggerExpression("cloaks");
		Expression expr3 = ConversationParser.createTriggerExpression("trousers");

		assertTrue(expr1.matches(expr1));
		assertFalse(expr1.matches(expr2));
		assertFalse(expr1.matches(expr3));

		assertTrue(expr1.matchesNormalized(expr2));
		assertFalse(expr1.matchesNormalized(expr3));

		assertTrue(expr1.matchesNormalizedBeginning(expr2));
		assertTrue(expr2.matchesNormalizedBeginning(expr1));
		assertFalse(expr1.matchesNormalizedBeginning(expr3));
	}
	
	@Test
	public final void testEquals() {
		Expression exp = new Expression("blabla");

		// compare with the same object
		assertTrue(exp.equals(exp));

		// check equals() with null parameter
		assertFalse(exp.equals(null));

		// negative equal() tests
		assertFalse(exp.equals("blabla"));

		Object x = "abc";
		Object y = new Expression("abc");
		assertFalse(y.equals(x));
		assertFalse(x.equals(y));

		assertFalse("should not break equals contract", "blabla".equals(exp));

		// positive equals() test
		x = new Expression("abc");
		y = new Expression("abc");
		assertTrue(y.equals(x));
		assertTrue(x.equals(y));
	}

	@Test
	public final void testTriggerMatching() {
		Sentence s1 = ConversationParser.parse("spade");
		Expression e1 = s1.getTriggerExpression();
		Sentence s2 = ConversationParser.parse("a spade");
		Expression e2 = s2.getTriggerExpression();
		assertFalse(s1.hasError());
		assertFalse(s2.hasError());
		assertTrue(e1.matchesNormalized(e2));
		assertTrue(e2.matchesNormalized(e1));
	}

	@Test
	public final void testTypeTriggerMatching() {
		// First show, that "do" without the exactMatching flag matches "done".
		Sentence s1 = ConversationParser.parse("done");
		assertFalse(s1.hasError());
		assertEquals("do/VER-PAS", s1.toString());
		Expression e1 = s1.getTriggerExpression();

		Sentence s2 = ConversationParser.parse("do");
		assertFalse(s2.hasError());
		Expression e2 = s2.getTriggerExpression();
		assertTrue(e2.matchesNormalized(e1));
		assertEquals("do/VER", s2.toString());

		// Using the typeMatching flag, it doesn't match any more...
		s1 = ConversationParser.parse("|TYPE|done/VER-PAS");
		assertFalse(s1.hasError());
		assertEquals("done/VER-PAS", s1.toString());
		e1 = s1.getTriggerExpression();

		assertFalse(e2.matches(e1));
		assertFalse(e2.matchesNormalized(e1));

		// ...but "done" matches the given type string pattern.
		s2 = ConversationParser.parse("done");
		assertFalse(s2.hasError());
		assertEquals("do/VER-PAS", s2.toString());
		e2 = s2.getTriggerExpression();
		assertTrue(e2.matches(e1));
		assertTrue(e2.matchesNormalized(e1));
	}

	@Test
	public final void testExactTriggerMatching() {
		// First show, that "do" without the exactMatching flag matches "done".
		Sentence s1 = ConversationParser.parse("done");
		assertFalse(s1.hasError());
		assertEquals("do/VER-PAS", s1.toString());
		Expression e1 = s1.getTriggerExpression();

		Sentence s2 = ConversationParser.parse("do");
		assertFalse(s2.hasError());
		Expression e2 = s2.getTriggerExpression();
		assertTrue(e2.matchesNormalized(e1));
		assertEquals("do/VER", s2.toString());

		// Using the exactMatching flag, it doesn't match any more...
		s1 = ConversationParser.parse("|EXACT|dONe");
		assertFalse(s1.hasError());
		assertEquals("dONe", s1.toString());
		e1 = s1.getTriggerExpression();

		assertFalse(e2.matches(e1));
		assertFalse(e2.matchesNormalized(e1));

		// ...but "done" matches the given exact matching pattern.
		s2 = ConversationParser.parse("dONe");
		assertFalse(s2.hasError());
		assertEquals("do/VER-PAS", s2.toString());
		e2 = s2.getTriggerExpression();
		assertTrue(e2.matches(e1));
		assertTrue(e2.matchesNormalized(e1));
	}

}
