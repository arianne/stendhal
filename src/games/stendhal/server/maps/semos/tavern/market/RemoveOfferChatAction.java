package games.stendhal.server.maps.semos.tavern.market;

import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.trade.Market;
import games.stendhal.server.entity.trade.Offer;
/**
 * removes a certain offer from the market
 * 
 * @author madmetzger
 *
 */
public class RemoveOfferChatAction extends KnownOffersChatAction {

	public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
		if (sentence.hasError()) {
			npc.say("Sorry, I did not understand you. "
					+ sentence.getErrorString());
			npc.setCurrentState(ConversationStates.ATTENDING);
		} else if (sentence.getExpressions().iterator().next().toString().equals("remove")){
			handleSentence(player,sentence,npc);
		}
	}

	private void handleSentence(Player player, Sentence sentence, SpeakerNPC npc) {
		MarketManagerNPC manager = (MarketManagerNPC) npc;
		try {
			String offerNumber = getOfferNumberFromSentence(sentence).toString();
			if(manager.getOfferMap().get(player.getName()).containsKey(offerNumber)) {
				Offer o = manager.getOfferMap().get(player.getName()).get(offerNumber);
				if(o.getOfferer().equals(player.getName())) {
					Market m = TradeCenterZoneConfigurator.getShopFromZone(player.getZone());
					m.removeOffer(o,player);
					player.getZone().add(o, true);
					return;
				}
				npc.say("You can only remove your own offers. Please say #show #mine to see only your offers.");
				npc.setCurrentState(ConversationStates.ATTENDING);
				return;
			}
			npc.say("Sorry, please choose a number from those I told you to remove your offer.");
			npc.setCurrentState(ConversationStates.BUY_PRICE_OFFERED);
		} catch (NumberFormatException e) {
			npc.say("Sorry, please say #remove #number");
			npc.setCurrentState(ConversationStates.BUY_PRICE_OFFERED);
		}
		manager.getOfferMap().clear();
	}
}
