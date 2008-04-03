package games.stendhal.server.entity.npc.parser;

/**
 * JokerExprMatcher creates an ExpressionMatcher for joker matching. This mode uses the
 * Expression.sentenceMatchExpression() function of sentence matching, which chooses automatically
 * between word and type matching.
 *
 * @author Martin Fuchs
 */
public final class JokerExprMatcher extends ExpressionMatcher {

    public JokerExprMatcher() {
        jokerMatching = true;
    }

}
