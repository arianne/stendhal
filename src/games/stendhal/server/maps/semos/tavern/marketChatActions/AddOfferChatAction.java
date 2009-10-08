package games.stendhal.server.maps.semos.tavern.marketChatActions;

import java.math.BigDecimal;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Expression;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.semos.tavern.TradeCenterZoneConfigurator;
import games.stendhal.server.trade.Market;
import games.stendhal.server.trade.Offer;

/**
 * puts a new offer to the market
 * @author madmetzger
 *
 */
public class AddOfferChatAction implements ChatAction {
	
	private static final double TRADING_FEE_PERCENTAGE = 0.01;
	private static final double TRADING_FEE_PLAYER_KILLER_PENALTY = 0.5;
	private static final int MAX_NUMBER_OFF_OFFERS = 3;
	private static final int DAYS_TO_OFFER_EXPIRING = 3;

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
			if(countOffers(player)<=MAX_NUMBER_OFF_OFFERS) {
				String itemName = determineItemName(sentence);
				int price = determinePrice(sentence);
				if(substractTradingFee(player, price)) {
					createOffer(player, itemName, price);
					npc.say("I added your offer to the trading center and took the fee.");
					npc.setCurrentState(ConversationStates.ATTENDING);
					return;
				}
				npc.say("You cannot afford the trading fee of "+Integer.valueOf(calculateFee(player, price).intValue()).toString());
				return;
			}
			npc.say("You may not place more than "+Integer.valueOf(MAX_NUMBER_OFF_OFFERS).toString()+" offers.");
		} catch (NumberFormatException e) {
			npc.say("I did not understand you. Please say \"sell item price\".");
			npc.setCurrentState(ConversationStates.ATTENDING);
		}
	}
	
	private boolean substractTradingFee(Player player, int price) {
		BigDecimal fee = calculateFee(player, price);
		return player.drop("money", fee.intValue());
	}

	private BigDecimal calculateFee(Player p, int price) {
		BigDecimal fee  = BigDecimal.valueOf(price);
		fee = fee.multiply(BigDecimal.valueOf(TRADING_FEE_PERCENTAGE));
		if(p.isBadBoy()) {
			fee = fee.multiply(BigDecimal.valueOf(TRADING_FEE_PLAYER_KILLER_PENALTY));
		}
		return fee;
	}

	private int countOffers(Player player) {
		Market shopFromZone = TradeCenterZoneConfigurator.getShopFromZone(player.getZone());
		if(shopFromZone != null) {
			int numberOfOffers = shopFromZone.countOffersOfPlayer(player);
			return numberOfOffers;
		}
		return 0;
	}

	private void createOffer(Player player, String itemName, int price) {
		Market shop = TradeCenterZoneConfigurator.getShopFromZone(player.getZone());
		if(shop != null) {
			Item item = SingletonRepository.getEntityManager().getItem(itemName);
			Offer o = shop.createOffer(player,item,Integer.valueOf(price));
			TurnListener offerExpirer = new OfferExpirerer(o);
			TurnNotifier.get().notifyInTurns(DAYS_TO_OFFER_EXPIRING * MathHelper.SECONDS_IN_ONE_DAY, offerExpirer);
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
