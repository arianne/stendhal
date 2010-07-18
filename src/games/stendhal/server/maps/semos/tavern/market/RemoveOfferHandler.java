package games.stendhal.server.maps.semos.tavern.market;

import java.util.Map;

import games.stendhal.common.Grammar;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.trade.Market;
import games.stendhal.server.entity.trade.Offer;

public class RemoveOfferHandler extends OfferHandler {
	@Override
	public void add(SpeakerNPC npc) {
		npc.add(ConversationStates.ATTENDING, "remove", null, ConversationStates.ATTENDING, null, 
				new RemoveOfferChatAction());
		npc.add(ConversationStates.QUESTION_1, ConversationPhrases.YES_MESSAGES, 
				ConversationStates.ATTENDING, null, new ConfirmRemoveOfferChatAction());
		npc.add(ConversationStates.QUESTION_1, ConversationPhrases.NO_MESSAGES, null, 
				ConversationStates.ATTENDING, "Ok, how else may I help you?", null);
	}

	protected class RemoveOfferChatAction extends KnownOffersChatAction {
		public void fire(Player player, Sentence sentence, EventRaiser npc) {
			if (sentence.hasError()) {
				npc.say("Sorry, I did not understand you. "
						+ sentence.getErrorString());
			} else if (sentence.getExpressions().iterator().next().toString().equals("remove")){
				handleSentence(player, sentence, npc);
			}
		}

		private void handleSentence(Player player, Sentence sentence, EventRaiser npc) {
			MarketManagerNPC manager = (MarketManagerNPC) npc.getEntity();
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
						setOffer(o);
						// Ask for confirmation only if the offer is still active
						if (TradeCenterZoneConfigurator.getShopFromZone(player.getZone()).getOffers().contains(o)) {
							int quantity = getQuantity(o.getItem());
							npc.say("Do you want to remove your offer of " + Grammar.quantityplnoun(quantity, o.getItem().getName(), "one") + "?");
							npc.setCurrentState(ConversationStates.QUESTION_1);
						} else {
							removeOffer(player, npc);
						}
						return;
					}
					npc.say("You can only remove your own offers. Please say #show #mine to see only your offers.");
					return;
				}
				npc.say("Sorry, please choose a number from those I told you to remove your offer.");
			} catch (NumberFormatException e) {
				npc.say("Sorry, please say #remove #number");
			}
		}
	}
	
	protected class ConfirmRemoveOfferChatAction implements ChatAction {
		public void fire(Player player, Sentence sentence, EventRaiser npc) {
			removeOffer(player, npc);
		}
	}
	
	private void removeOffer(Player player, EventRaiser npc) {
		Offer offer = getOffer();
		Market m = TradeCenterZoneConfigurator.getShopFromZone(player.getZone());
		m.removeOffer(offer,player);
		npc.say("Ok.");
		// Obsolete the offers, since the list has changed
		((MarketManagerNPC) npc.getEntity()).getOfferMap().put(player.getName(), null);
	}
}
