package games.stendhal.server.maps.semos.tavern.marketChatActions;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.semos.tavern.MarketManagerNPC;
import games.stendhal.server.maps.semos.tavern.TradeCenterZoneConfigurator;
import games.stendhal.server.trade.Market;
import games.stendhal.server.trade.Offer;

public class AcceptOfferChatAction implements ChatAction {

	public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
		if (sentence.hasError()) {
			npc.say("Sorry, I did not understand you. "
					+ sentence.getErrorString());
			npc.setCurrentState(ConversationStates.ATTENDING);
		} else if (sentence.getExpressions().iterator().next().toString().equals("accept")){
			handleSentence(player,sentence,npc);
		}
	}

	private void handleSentence(Player player, Sentence sentence, SpeakerNPC npc) {
		try {
			MarketManagerNPC manager = (MarketManagerNPC) npc;
			String offerNumber = getOfferNumberFromSentence(sentence).toString();
			if(manager.getOfferMap().get(player.getName()).containsKey(offerNumber)) {
				Offer o = manager.getOfferMap().get(player.getName()).get(offerNumber);
				Market m = TradeCenterZoneConfigurator.getShopFromZone(player.getZone());
				m.acceptOffer(o,player);
				player.getZone().add(o, true);
				return;
			}
			npc.say("Sorry, please choose a number from those I told you to accept an offer.");
			npc.setCurrentState(ConversationStates.BUY_PRICE_OFFERED);
		} catch (NumberFormatException e) {
			npc.say("Sorry, please say \"accept number\"");
			npc.setCurrentState(ConversationStates.BUY_PRICE_OFFERED);
		}
	}

	private Integer getOfferNumberFromSentence(Sentence sentence) {
		String offerNumber = sentence.getExpression(1,"").toString();
		return Integer.parseInt(offerNumber);
	}

}
