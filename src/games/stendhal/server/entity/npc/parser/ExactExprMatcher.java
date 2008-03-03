package games.stendhal.server.entity.npc.parser;

/**
 *  ExactExpressionMatcher creates an ExpressionMatcher with exact matching.
 *
 * @author Martin Fuchs
 */
public class ExactExprMatcher extends ExpressionMatcher {

	public ExactExprMatcher() {
		exactMatching = true;
	}

}
