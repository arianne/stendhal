package games.stendhal.server.maps.semos.tavern.market;

import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.trade.Market;
import games.stendhal.server.entity.trade.Offer;
/**
 * chat action to accept an offer on the market
 * 
 * @author madmetzger
 *
 */
public class AcceptOfferChatAction extends KnownOffersChatAction {
	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(AcceptOfferChatAction.class);

	public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
		if (sentence.hasError()) {
			npc.say("Sorry, I did not understand you. "
					+ sentence.getErrorString());
		} else if (sentence.getExpressions().iterator().next().toString().equals("accept")){
			handleSentence(player,sentence,npc);
		}
	}

	private void handleSentence(Player player, Sentence sentence, SpeakerNPC npc) {
		MarketManagerNPC manager = (MarketManagerNPC) npc;
		try {
			String offerNumber = getOfferNumberFromSentence(sentence).toString();
			Map<String,Offer> offerMap = manager.getOfferMap().get(player.getName());
			if (offerMap == null) {
				npc.say("Please take a look at the list of offers first.");
				return;
			}
			if(offerMap.containsKey(offerNumber)) {
				Offer o = offerMap.get(offerNumber);
				Market m = TradeCenterZoneConfigurator.getShopFromZone(player.getZone());
				if (m.acceptOffer(o,player)) {
					// Succesful trade. Tell the offerer
					StringBuilder earningToFetchMessage = new StringBuilder();
					earningToFetchMessage.append("Harold tells you: tell ");
					earningToFetchMessage.append(o.getOfferer());
					earningToFetchMessage.append(" Your ");
					earningToFetchMessage.append(o.getItem().getName());
					earningToFetchMessage.append(" was sold. You can now fetch your earnings from me.");

					Player postman = SingletonRepository.getRuleProcessor().getPlayer("postman");
					if (postman != null) {
						postman.sendPrivateText(earningToFetchMessage.toString());
					} else {
						earningToFetchMessage.insert(0, "Could not use postman for the following message: ");
						logger.warn(earningToFetchMessage.toString());
					}

					npc.say("The offer has been accepted.");
					// Obsolete the offers, since the list has changed
					manager.getOfferMap().put(player.getName(), null);
				} else {
					// Trade failed for some reason. Check why, and inform the player
					if (!m.getOffers().contains(o)) {
						int quantity = 1;
						if (o.getItem() instanceof StackableItem) {
							quantity = ((StackableItem) o.getItem()).getQuantity(); 
						}
						Grammar.thatthose(quantity);
						npc.say("I'm sorry, but " + Grammar.thatthose(quantity) + " "
								+ Grammar.quantityplnoun(quantity, o.getItem().getName())
								+ " " + Grammar.isare(quantity)
								+ " no longer for sale.");
					} else {
						npc.say("Sorry, you don't have enough money!");
					}
				}
				
				return;
			}
			npc.say("Sorry, please choose a number from those I told you to accept an offer.");
		} catch (NumberFormatException e) {
			npc.say("Sorry, please say #accept #number");
		}
	}
}
