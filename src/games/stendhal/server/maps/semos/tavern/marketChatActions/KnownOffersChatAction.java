package games.stendhal.server.maps.semos.tavern.marketChatActions;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.parser.Sentence;

public abstract class KnownOffersChatAction implements ChatAction {

	protected Integer getOfferNumberFromSentence(Sentence sentence) {
		String offerNumber = sentence.getExpression(1,"").toString();
		return Integer.parseInt(offerNumber);
	}

}
