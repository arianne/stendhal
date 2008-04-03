package games.stendhal.server.entity.npc.parser;

/**
 * CaseInsensitiveExprMatcher creates an ExpressionMatcher with exact and case insensitive matching.
 *
 * @author Martin Fuchs
 */
public final class CaseInsensitiveExprMatcher extends ExpressionMatcher {

    public CaseInsensitiveExprMatcher() {
        exactMatching = true;
        caseInsensitive = true;
    }

}
