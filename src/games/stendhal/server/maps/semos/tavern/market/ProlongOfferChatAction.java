package games.stendhal.server.maps.semos.tavern.market;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.trade.Market;
import games.stendhal.server.entity.trade.Offer;

public class ProlongOfferChatAction extends KnownOffersChatAction {

	public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
		if (sentence.hasError()) {
			npc.say("Sorry, I did not understand you. "
					+ sentence.getErrorString());
		} else if (sentence.getExpressions().iterator().next().toString().equals("prolong")){
			handleSentence(player, sentence, npc);
		}
	}

	private void handleSentence(Player player, Sentence sentence, SpeakerNPC npc) {
		MarketManagerNPC manager = (MarketManagerNPC) npc;
		try {
			String offerNumber = getOfferNumberFromSentence(sentence).toString();
			if(manager.getOfferMap().get(player.getName()).containsKey(offerNumber)) {
				Offer o = manager.getOfferMap().get(player.getName()).get(offerNumber);
				if(o.getOfferer().equals(player.getName())) {
					Integer fee = Integer.valueOf(TradingUtility.calculateFee(player, o.getPrice()).intValue());
					if(TradingUtility.substractTradingFee(player, o.getPrice())) {
						prolongOffer(player, o);
						npc.say("I prolonged your offer and took the fee of "+fee.toString()+" again.");
						return;
					}
					npc.say("You cannot afford the trading fee of "+fee.toString());
					return;
				}
				npc.say("You can only prolong your own offers. Please say #show #mine to see only your offers.");
				return;
			}
			npc.say("Sorry, please choose a number from those I told you to prolong your offer.");
		} catch (NumberFormatException e) {
			npc.say("Sorry, please say #remove #number");
		}
		manager.getOfferMap().clear();
	}

	private void prolongOffer(Player player, Offer o) {
		Market market = TradeCenterZoneConfigurator.getShopFromZone(player.getZone());
		if (market != null) {
			Offer newOffer = market.prolongOffer(o);
			TradingUtility.addTurnNotifiers(player, newOffer);
			String messageNumberOfOffers = "You now have put "+Integer.valueOf(market.countOffersOfPlayer(player)).toString()+" offers.";
			player.sendPrivateText(messageNumberOfOffers);
		}
	}

}
