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

import java.util.Map;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.trade.Offer;
import games.stendhal.server.events.ExamineEvent;

public class ExamineOfferChatAction extends KnownOffersChatAction {
	@Override
	public void fire(Player player, Sentence sentence, EventRaiser npc) {
		if (sentence.hasError()) {
			npc.say("Sorry, I did not understand you. "
					+ sentence.getErrorString());
		} else if (sentence.getExpressions().iterator().next().toString().equals("examine")){
			handleSentence(player,sentence,npc);
		}
	}

	private void handleSentence(Player player, Sentence sentence, EventRaiser npc) {
		MarketManagerNPC manager = (MarketManagerNPC) npc.getEntity();
		try {
			String offerNumber = getOfferNumberFromSentence(sentence).toString();
			Map<String,Offer> offerMap = manager.getOfferMap();
			if (offerMap == null) {
				npc.say("Please take a look at the list of offers first.");
				return;
			}
			if(offerMap.containsKey(offerNumber)) {
				Offer o = offerMap.get(offerNumber);
				if (o.hasItem()) {
					player.sendPrivateText(o.getItem().describe());
					showImage(player, o.getItem());
					return;
				}
			}
			npc.say("Sorry, please choose a number from those I told you.");
		} catch (NumberFormatException e) {
			npc.say("Sorry, please say #accept #number");
		}
	}

	private void showImage(Player player, Item item) {
		String caption = item.getName();
		String image = "items/" + item.getItemClass() + "/" + item.getItemSubclass() + ".png";
		ExamineEvent event = new ExamineEvent(image, caption, "");
		player.addEvent(event);
		player.notifyWorldAboutChanges();
	}
}
