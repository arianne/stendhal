package games.stendhal.server.maps.semos.tavern;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.semos.tavern.marketChatActions.AddOfferChatAction;
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
		ChatAction action = new AddOfferChatAction();
		add(ConversationStates.ATTENDING,
			"sell",null,null,
			ConversationStates.ATTENDING,
			null,action);
		ChatAction showAction = new ShowOffersChatAction();
		add(ConversationStates.ATTENDING,"show",null,ConversationStates.ATTENDING,null,showAction);
		addGoodbye("Visit me again to see players offers or put a new offer!");
	}
	public Map<String, Map<String, Offer>> getOfferMap() {
		return offerMap;
	}
}
