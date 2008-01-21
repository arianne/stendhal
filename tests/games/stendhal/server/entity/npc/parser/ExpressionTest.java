package games.stendhal.server.entity.npc.parser;

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
		assertTrue(exp.equals(exp));
		assertTrue(exp.equals("blabla"));
		assertFalse(exp.equals(null));
		assertTrue("should not break equals contract","blabla".equals(exp));
	}

}
