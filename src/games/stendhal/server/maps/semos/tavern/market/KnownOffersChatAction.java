package games.stendhal.server.maps.semos.tavern.market;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.parser.Expression;
import games.stendhal.server.entity.npc.parser.Sentence;
/**
 * abstract class for handling offer numbers in sentences
 *  
 * @author madmetzger
 */
public abstract class KnownOffersChatAction implements ChatAction {

	protected Integer getOfferNumberFromSentence(Sentence sentence) {
		Expression expression = sentence.getExpression(1,"");
		if (expression == null) {
			throw new NumberFormatException("no number provided");
		}
		String offerNumber = expression.toString();
		return Integer.parseInt(offerNumber);
	}

}
