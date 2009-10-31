package games.stendhal.server.maps.semos.tavern.market;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Expression;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.trade.Market;
import games.stendhal.server.entity.trade.Offer;

/**
 * puts a new offer to the market
 * @author madmetzger
 *
 */
public class PrepareOfferChatAction implements ChatAction {
	
	public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
		if (sentence.hasError()) {
			npc.say("Sorry, I did not understand you. "
					+ sentence.getErrorString());
			npc.setCurrentState(ConversationStates.ATTENDING);
		} else if (sentence.getExpressions().iterator().next().toString().equals("sell")){
			handleSentence(player,sentence,npc);
		}
	}

	private void handleSentence(Player player, Sentence sentence, SpeakerNPC npc) {
		try {
			if(TradingUtility.isPlayerWithinOfferLimit(player)) {
				String itemName = determineItemName(sentence);
				int price = determinePrice(sentence);
				Integer fee = Integer.valueOf(TradingUtility.calculateFee(player, price).intValue());
				if(TradingUtility.substractTradingFee(player, price)) {
					createOffer(player, itemName, price);
					npc.say("I added your offer to the trading center and took the fee of "+fee.toString()+".");
					npc.setCurrentState(ConversationStates.ATTENDING);
					return;
				}
				npc.say("You cannot afford the trading fee of "+fee.toString());
				return;
			}
			npc.say("You may not place more than "+Integer.valueOf(TradingUtility.MAX_NUMBER_OFF_OFFERS).toString()+" offers.");
		} catch (NumberFormatException e) {
			npc.say("I did not understand you. Please say \"sell item price\".");
			npc.setCurrentState(ConversationStates.ATTENDING);
		}
	}
	
	private void createOffer(Player player, String itemName, int price) {
		Market shop = TradeCenterZoneConfigurator.getShopFromZone(player.getZone());
		if(shop != null) {
			Offer o = shop.createOffer(player,itemName,Integer.valueOf(price));
			TradingUtility.addTurnNotifiers(player, o);
			StringBuilder message = new StringBuilder("Offer for ");
			message.append(itemName);
			message.append(" at ");
			message.append(price);
			message.append(" created.");
			player.sendPrivateText(message.toString());
			String messageNumberOfOffers = "You now have put "+Integer.valueOf(shop.countOffersOfPlayer(player)).toString()+" offers.";
			player.sendPrivateText(messageNumberOfOffers);
			return;
		}
	}

	private String determineItemName(Sentence sentence) {
		Expression expression = sentence.getExpression(1,"");
		return expression.getNormalized();
	}

	private int determinePrice(Sentence sentence) {
		Expression expression = sentence.getExpression(2,"");
		String number = expression.getNormalized();
		return Integer.parseInt(number);
	}
	
}
