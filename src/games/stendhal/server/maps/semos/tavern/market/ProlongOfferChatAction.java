package games.stendhal.server.maps.semos.tavern.market;

import java.util.Map;

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
			
			Map<String,Offer> offerMap = manager.getOfferMap().get(player.getName());
			if (offerMap == null) {
				npc.say("Please check your offers first.");
				return;
			}
			if(offerMap.containsKey(offerNumber)) {
				Offer o = offerMap.get(offerNumber);
				if(o.getOfferer().equals(player.getName())) {
					if (!wouldOverflowMaxOffers(player, o)) {
						Integer fee = Integer.valueOf(TradingUtility.calculateFee(player, o.getPrice()).intValue());
						if (player.isEquipped("money", fee)) { 
							if (prolongOffer(player, o)) {
								TradingUtility.substractTradingFee(player, o.getPrice());
								npc.say("I prolonged your offer and took the fee of "+fee.toString()+" again.");
							} else {
								npc.say("Sorry, that offer has already been removed from the market.");
							}
							// Changed the status, or it has been changed by expiration. Obsolete the offers
							manager.getOfferMap().put(player.getName(), null);
						} else {
							npc.say("You cannot afford the trading fee of "+fee.toString());
						}
					} else {
						npc.say("Sorry, you can have only " + TradingUtility.MAX_NUMBER_OFF_OFFERS
								+ " active offers at a time.");
					}
				} else {
					npc.say("You can only prolong your own offers. Please say #show #mine to see only your offers.");
				}
			} else {
				npc.say("Sorry, please choose a number from those I told you to prolong your offer.");
				return;
			}
		} catch (NumberFormatException e) {
			npc.say("Sorry, please say #remove #number");
		}
	}
	
	/**
	 * Check if prolonging an offer would result the player having too many active offers on market.
	 * 
	 * @param player the player to be checked
	 * @param offer the offer the player wants to prolong
	 * @return true if prolonging the offer should be denied
	 */
	private boolean wouldOverflowMaxOffers(Player player, Offer offer) {
		Market market = TradeCenterZoneConfigurator.getShopFromZone(player.getZone());
		
		if ((market.countOffersOfPlayer(player) == TradingUtility.MAX_NUMBER_OFF_OFFERS)
				&& market.getExpiredOffers().contains(offer)) {
			return true;
		}
		
		return false;
	}

	private boolean prolongOffer(Player player, Offer o) {
		Market market = TradeCenterZoneConfigurator.getShopFromZone(player.getZone());
		if (market != null) {
			if (market.prolongOffer(o) != null) {
				String messageNumberOfOffers = "You now have put "+Integer.valueOf(market.countOffersOfPlayer(player)).toString()+" offers.";
				player.sendPrivateText(messageNumberOfOffers);
				
				return true;
			}
		}
		
		return false;
	}

}
