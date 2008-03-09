package games.stendhal.server.entity.npc.parser;

/**
 *  CaseInsensitiveExprMatcher creates an ExpressionMatcher with exact and
 *  case insensitive matching.
 *
 * @author Martin Fuchs
 */
final public class CaseInsensitiveExprMatcher extends ExpressionMatcher {

	public CaseInsensitiveExprMatcher() {
		exactMatching = true;
		caseInsensitive = true;
	}

}
