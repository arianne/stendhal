package games.stendhal.server.maps.semos.tavern;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Expression;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.trade.Shop;

import java.util.Map;

import marauroa.common.game.RPObject;

public class TradeCenter implements ZoneConfigurator {

	private static final String TRADE_ADVISOR_NAME = "clerk";
	private static final int COORDINATE_Y = 13;
	private static final int COORDINATE_X = 10;

	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		addShopToZone(zone);
		buildTradeCenterAdvisor(zone);
	}

	private void addShopToZone(StendhalRPZone zone) {
		Shop shop = Shop.createShop();
		zone.add(shop);
	}

	private void buildTradeCenterAdvisor(StendhalRPZone zone) {
		SpeakerNPC speaker = new SpeakerNPC(TRADE_ADVISOR_NAME) {
			@Override
			protected void createPath() {
				
			}
			@Override
			protected void createDialog() {
				addGreeting("Welcome to Semos trading center. How can I help you?");
				addJob("I am here to assist you in selling items.");
				addHelp("Add a nice help text here.");
				ChatAction action = new AddOfferChatAction();
				add(ConversationStates.ATTENDING,
					"sell",null,null,
					ConversationStates.SELL_PRICE_OFFERED,
					null,action);
				addGoodbye();
			}
		};
		speaker.setPosition(COORDINATE_X,COORDINATE_Y);
		speaker.setEntityClass("tradecenteradvisornpc");
		speaker.setOutfit(new Outfit(10,10,10,10));
		speaker.initHP(100);
		zone.add(speaker);
	}

}
class AddOfferChatAction implements ChatAction {
	
	private static final int MAX_NUMBER_OFF_OFFERS = 5;

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
				createOffer(player, itemName, price);
				npc.say("I added your offer to the trading center.");
				npc.setCurrentState(ConversationStates.ATTENDING);
				return;
			}
			npc.say("You may not place more than "+Integer.valueOf(MAX_NUMBER_OFF_OFFERS).toString()+" offers.");
		} catch (NumberFormatException e) {
			npc.say("I did not understand you. Please say \"sell item price\".");
			npc.setCurrentState(ConversationStates.ATTENDING);
		}
	}

	private int countOffers(Player player) {
		Shop shopFromZone = getShopFromZone(player.getZone());
		if(shopFromZone != null) {
			int numberOfOffers = shopFromZone.countOffersOfPlayer(player);
			return numberOfOffers;
		}
		return 0;
	}

	private void createOffer(Player player, String itemName, int price) {
		Shop shop = getShopFromZone(player.getZone());
		if(shop != null) {
			Item item = SingletonRepository.getEntityManager().getItem(itemName);
			shop.createOffer(player,item,Integer.valueOf(price));
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

	private Shop getShopFromZone(StendhalRPZone zone) {
		for (RPObject rpObject : zone) {
			if(rpObject.getRPClass().getName().equals("shop")) {
				return (Shop) rpObject;
			}
		}
		return null;
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