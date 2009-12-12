package games.stendhal.server.maps.semos.tavern.market;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Expression;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.trade.Market;
import games.stendhal.server.entity.trade.Offer;

import java.util.HashMap;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * shows all current offers to the asking player
 * @author madmetzger
 *
 */
public class ShowOffersChatAction implements ChatAction {
	/** Maximum list length that is shown to the players */
	private static final int MAX_SHOWN_OFFERS = 20;
	
	public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
		if (sentence.hasError()) {
			npc.say("Sorry, I did not understand you. "
					+ sentence.getErrorString());
			npc.setCurrentState(ConversationStates.ATTENDING);
		} else if (sentence.getExpressions().iterator().next().toString().equals("show")){
			handleSentence(player, sentence, npc);
		}
	}

	private void handleSentence(Player player, Sentence sentence, SpeakerNPC npc) {
		
		boolean onlyMyOffers = checkForMineFilter(sentence);
		boolean onlyMyExpiredOffers = checkForMyExpiredFilter(sentence);
		boolean filterForMine = false;
		
		Market market = TradeCenterZoneConfigurator.getShopFromZone(player.getZone());
		RPSlot offersSlot = market.getSlot(Market.OFFERS_SLOT_NAME);
		if (onlyMyExpiredOffers) {
			offersSlot = market.getSlot(Market.EXPIRED_OFFERS_SLOT_NAME);
			filterForMine = true;
		}
		if (onlyMyOffers) {
			filterForMine = true;
		}
		
		StringBuilder offersMessage = new StringBuilder();
		MarketManagerNPC marketNPC = (MarketManagerNPC) npc;
		marketNPC.getOfferMap().put(player.getName(),new HashMap<String, Offer>());
		
		int counter = 0;
		counter = filterSlotContent(player, filterForMine, offersMessage,
				counter, offersSlot, marketNPC);
		if (counter > 0) {
			player.sendPrivateText(offersMessage.toString());
		}
		if (counter == 0) {
			String expiredAddition = onlyMyExpiredOffers ? "expired " : "";
			player.sendPrivateText("There are currently no "+expiredAddition+"offers in the market.");
		}
	}

	private int filterSlotContent(Player player, boolean onlyMyOffers,
			StringBuilder offersMessage, int counter, RPSlot offersSlot,
			MarketManagerNPC marketNPC) {
		for (RPObject rpObject : offersSlot) {
			if (rpObject.getRPClass().getName().equals(Offer.OFFER_RPCLASS_NAME)) {
				Offer o = (Offer) rpObject;
				if (onlyMyOffers && !o.getOfferer().equals(player.getName())) {
					continue;
				}
				counter += 1;
				if (counter > MAX_SHOWN_OFFERS) {
					offersMessage.append("Only " + MAX_SHOWN_OFFERS + " first offers shown.");
					return counter;
				}
				offersMessage.append(counter);
				offersMessage.append(": ");
				offersMessage.append(o.getItem().getName());
				offersMessage.append(" for ");
				offersMessage.append(o.getPrice());
				offersMessage.append(" money");
				offersMessage.append("\n");
				marketNPC.getOfferMap().get(player.getName()).put(Integer.valueOf(counter).toString(), o);
			}
		}
		return counter;
	}

	private boolean checkForMineFilter(Sentence sentence) {
		for (Expression expression : sentence) {
			if(expression.toString().equals("mine")) {
				return true;
			}
		}
		return false;
	}
	
	private boolean checkForMyExpiredFilter(Sentence sentence) {
		for (Expression expression : sentence) {
			if(expression.toString().equals("expired")) {
				return true;
			}
		}
		return false;
	}
	
}
