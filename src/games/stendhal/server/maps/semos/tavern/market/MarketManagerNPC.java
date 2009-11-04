package games.stendhal.server.maps.semos.tavern.market;

import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.trade.Offer;

import java.util.HashMap;
import java.util.Map;

public final class MarketManagerNPC extends SpeakerNPC {
	
	private Map<String,Map<String,Offer>> offerMap = new HashMap<String, Map<String, Offer>>();
	
	private Offer chosenOffer;
	
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
		addHelp("I help you in selling your items. Say #sell #item #price to put an offer at the market." +
				" If you want to buy something, say #show and I will list current open offers with an " +
				"offer number. If you want to accept one of the offers, say #accept #number to buy the " +
				"item offered with that number. If you want to remove an offer from the market, tell me " +
				"#show mine, so you will see only your offers. Say #remove #number afterward to remove a " +
				"certain offer. If you have expired offers, you can ask for them by saying #show #expired." +
				" You can prolong an expired offer by saying #prolong #number. If you already sold some items" +
				"you can say fetch to me and I will pay out your earnings.");
		add(ConversationStates.ATTENDING, "sell", null, null, ConversationStates.ATTENDING,	null, new PrepareOfferChatAction());
		add(ConversationStates.ATTENDING, "show", null, ConversationStates.BUY_PRICE_OFFERED, null, new ShowOffersChatAction());
		add(ConversationStates.ATTENDING, "fetch", null, ConversationStates.ATTENDING, null, new FetchEarningsChatAction());
		add(ConversationStates.BUY_PRICE_OFFERED, "accept", null, ConversationStates.ATTENDING, null, new AcceptOfferChatAction());
		add(ConversationStates.BUY_PRICE_OFFERED, "remove", null, ConversationStates.ATTENDING, null, new RemoveOfferChatAction());
		add(ConversationStates.BUY_PRICE_OFFERED, "prolong", null, ConversationStates.ATTENDING, null, new ProlongOfferChatAction());
		addGoodbye("Visit me again to see players offers, put a new offer or fetch your earnings!");
	}
	public Map<String, Map<String, Offer>> getOfferMap() {
		return offerMap;
	}

	public void setChosenOffer(Offer chosenOffer) {
		this.chosenOffer = chosenOffer;
	}

	public Offer getChosenOffer() {
		return chosenOffer;
	}
}
