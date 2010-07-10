package games.stendhal.server.maps.semos.tavern.market;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.trade.Market;
import games.stendhal.server.entity.trade.Offer;

public class AcceptOfferHandler extends OfferHandler {
	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(AcceptOfferChatAction.class);
	private static final List<String> TRIGGERS = Arrays.asList("buy", "accept"); 
	
	@Override
	public void add(SpeakerNPC npc) {
		npc.add(ConversationStates.ATTENDING, TRIGGERS, null, ConversationStates.ATTENDING, null, 
				new AcceptOfferChatAction());
		npc.add(ConversationStates.BUY_PRICE_OFFERED, ConversationPhrases.YES_MESSAGES, 
				ConversationStates.ATTENDING, null, new ConfirmAcceptOfferChatAction());
		npc.add(ConversationStates.BUY_PRICE_OFFERED, ConversationPhrases.NO_MESSAGES, null, 
				ConversationStates.ATTENDING, "Ok, how else may I help you?", null);
	}
	
	class AcceptOfferChatAction extends KnownOffersChatAction {
		public void fire(Player player, Sentence sentence, EventRaiser npc) {
			if (sentence.hasError()) {
				npc.say("Sorry, I did not understand you. "
						+ sentence.getErrorString());
			} else {
				handleSentence(player, sentence, npc);
			}
		}

		private void handleSentence(Player player, Sentence sentence, EventRaiser npc) {
			MarketManagerNPC manager = (MarketManagerNPC) npc.getEntity();
			try {
				String offerNumber = getOfferNumberFromSentence(sentence).toString();
				Map<String,Offer> offerMap = manager.getOfferMap().get(player.getName());
				if (offerMap == null) {
					npc.say("Please take a look at the list of offers first.");
					return;
				}
				if(offerMap.containsKey(offerNumber)) {
					Offer o = offerMap.get(offerNumber);
					setOffer(o);
					int quantity = getQuantity(o.getItem());
					npc.say("Do you want to buy " + Grammar.quantityplnoun(quantity, o.getItem().getName()) + " for " + o.getPrice() + " money?");
					npc.setCurrentState(ConversationStates.BUY_PRICE_OFFERED);
					return;
				}
				npc.say("Sorry, please choose a number from those I told you to accept an offer.");
			} catch (NumberFormatException e) {
				npc.say("Sorry, please say #accept #number");
			}
		}
	}

	class ConfirmAcceptOfferChatAction implements ChatAction {
		public void fire (Player player, Sentence sentence, EventRaiser npc) {
			Offer offer = getOffer();
			Market m = TradeCenterZoneConfigurator.getShopFromZone(player.getZone());
			if (m.acceptOffer(offer,player)) {
				// Succesful trade. Tell the offerer
				StringBuilder earningToFetchMessage = new StringBuilder();
				earningToFetchMessage.append("Harold tells you: tell ");
				earningToFetchMessage.append(offer.getOfferer());
				earningToFetchMessage.append(" Your ");
				earningToFetchMessage.append(offer.getItem().getName());
				earningToFetchMessage.append(" was sold. You can now fetch your earnings from me.");

				Player postman = SingletonRepository.getRuleProcessor().getPlayer("postman");
				if (postman != null) {
					postman.sendPrivateText(earningToFetchMessage.toString());
				} else {
					earningToFetchMessage.insert(0, "Could not use postman for the following message: ");
					logger.warn(earningToFetchMessage.toString());
				}

				npc.say("Thanks.");
				// Obsolete the offers, since the list has changed
				((MarketManagerNPC) npc.getEntity()).getOfferMap().put(player.getName(), null);
			} else {
				// Trade failed for some reason. Check why, and inform the player
				if (!m.getOffers().contains(offer)) {
					int quantity = getQuantity(offer.getItem());
					npc.say("I'm sorry, but " + Grammar.thatthose(quantity) + " "
							+ Grammar.quantityplnoun(quantity, offer.getItem().getName())
							+ " " + Grammar.isare(quantity)
							+ " no longer for sale.");
				} else {
					npc.say("Sorry, you don't have enough money!");
				}
			}
		}
	}
}
