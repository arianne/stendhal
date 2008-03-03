package games.stendhal.server.entity.npc.parser;

/**
 *  ExactExpressionMatcher creates an ExpressionMatcher with exact and
 *  case insensitive matching.
 *
 * @author Martin Fuchs
 */
public class CaseInsensitiveExprMatcher extends ExpressionMatcher {

	public CaseInsensitiveExprMatcher() {
		exactMatching = true;
		caseInsensitive = true;
	}

}
