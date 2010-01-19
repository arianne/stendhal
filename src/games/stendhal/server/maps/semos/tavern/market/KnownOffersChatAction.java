package games.stendhal.server.maps.semos.tavern.market;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.parser.Expression;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.npc.parser.SimilarExprMatcher;
/**
 * abstract class for handling offer numbers in sentences
 *  
 * @author madmetzger
 */
public abstract class KnownOffersChatAction implements ChatAction {
	protected Integer getOfferNumberFromSentence(Sentence sentence) {
		SimilarExprMatcher matcher = new SimilarExprMatcher();
		final int last = sentence.getExpressions().size();
		for (Expression expr : sentence.getExpressions().subList(1, last)) {
			if (matcher.match(expr, new Expression("number"))) {
				/*
				 * The player wrote either "command number", "command number <number>",
				 * or something along those lines. Ignore the "number" parts until
				 * we get to the actual numeral.
				 */
			} else {
				return Integer.parseInt(expr.toString());
			}
		}
		throw new NumberFormatException("no number provided");
	}
}
