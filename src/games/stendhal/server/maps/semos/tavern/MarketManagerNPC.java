package games.stendhal.server.maps.semos.tavern;

import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.semos.tavern.marketChatActions.AcceptOfferChatAction;
import games.stendhal.server.maps.semos.tavern.marketChatActions.AddOfferChatAction;
import games.stendhal.server.maps.semos.tavern.marketChatActions.RemoveOfferChatAction;
import games.stendhal.server.maps.semos.tavern.marketChatActions.ShowOffersChatAction;
import games.stendhal.server.trade.Offer;

import java.util.HashMap;
import java.util.Map;

public final class MarketManagerNPC extends SpeakerNPC {
	
	private Map<String,Map<String,Offer>> offerMap = new HashMap<String, Map<String, Offer>>();
	
	MarketManagerNPC(String name) {
		super(name);
	}

	@Override
	protected void createPath() {
		
	}

	@Override
	protected void createDialog() {
		addGreeting("Welcome to Semos trading center. How can I help you?");
		addJob("I am here to assist you in selling items.");
		addHelp("Add a nice help text here.");
		add(ConversationStates.ATTENDING, "sell", null, null, ConversationStates.ATTENDING,	null, new AddOfferChatAction());
		add(ConversationStates.ATTENDING, "show", null, ConversationStates.BUY_PRICE_OFFERED, null, new ShowOffersChatAction());
		add(ConversationStates.BUY_PRICE_OFFERED, "accept", null, ConversationStates.ATTENDING, null, new AcceptOfferChatAction());
		add(ConversationStates.BUY_PRICE_OFFERED, "remove", null, ConversationStates.ATTENDING, null, new RemoveOfferChatAction());
		addGoodbye("Visit me again to see players offers or put a new offer!");
	}
	public Map<String, Map<String, Offer>> getOfferMap() {
		return offerMap;
	}
}
