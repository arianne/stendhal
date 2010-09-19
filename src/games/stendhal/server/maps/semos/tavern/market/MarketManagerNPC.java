/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.semos.tavern.market;

import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.TextHasParameterCondition;
import games.stendhal.server.entity.trade.Offer;

import java.util.HashMap;
import java.util.Map;

public final class MarketManagerNPC extends SpeakerNPC {
	private Map<String,Map<String,Offer>> offerMap = new HashMap<String, Map<String, Offer>>();
	
	MarketManagerNPC(String name) {
		super(name);
	}

	@Override
	protected void createPath() {
		// npcs is lazy and does not move
	}

	@Override
	protected void createDialog() {
		addGreeting("Welcome to Semos trading center. How can I #help you?");
		addJob("I am here to #help you sell items.");
		addOffer("To put an offer on the market, say #sell #item #price - and then anyone else can buy it, " +
				"even if you are not here. For more details just ask for #help.");
		addHelp("Would you like help in #buying or help in #selling?");
		addReply("buying", "If you want to buy something, say #show and I will list current open offers with an " +
				 "offer number. If you want to accept one of the offers, say #accept #number to buy the " +
				 "item offered with that number. I am happy to filter the offer list for you, just tell me for " +
				 "example #show #meat to only see meat related offers.");
		addReply("selling", "Say #sell #item #price to put an offer on the market. If you want to remove an " +
				 "offer from the market, tell me #'show mine', so you will see only your offers. Say #remove " +
				 "#number afterward to remove a certain offer. If you have expired offers, you can ask for them " +
				 "by saying #show #expired. You can prolong an expired offer by saying #prolong #number. If you already sold some items " +
				 "you can say #fetch to me and I will pay out your earnings.");
		new PrepareOfferHandler().add(this);
		add(ConversationStates.ATTENDING, "show", new NotCondition(new TextHasParameterCondition()),
				ConversationStates.ATTENDING, null, new ShowOfferItemsChatAction());
		add(ConversationStates.ATTENDING, "show", new TextHasParameterCondition(), ConversationStates.ATTENDING, null, new ShowOffersChatAction());
		add(ConversationStates.ATTENDING, "fetch", null, ConversationStates.ATTENDING, null, new FetchEarningsChatAction());
		new AcceptOfferHandler().add(this);
		new RemoveOfferHandler().add(this);
		new ProlongOfferHandler().add(this);
		add(ConversationStates.ATTENDING, "examine", null, ConversationStates.ATTENDING, null, new ExamineOfferChatAction());
		addGoodbye("Visit me again to see available offers, make a new offer or fetch your earnings!");
	}

	public Map<String, Map<String, Offer>> getOfferMap() {
		return offerMap;
	}
}
