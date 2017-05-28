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

import java.util.Set;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.trade.Earning;
import games.stendhal.server.entity.trade.Market;
/**
 * chat action to let a player fetch his earnings from the market
 *
 * @author madmetzger
 *
 */
public class FetchEarningsChatAction implements ChatAction {

	@Override
	public void fire(Player player, Sentence sentence, EventRaiser npc) {
		if (sentence.hasError()) {
			npc.say("Sorry, I did not understand you. "
					+ sentence.getErrorString());
			npc.setCurrentState(ConversationStates.ATTENDING);
		} else {
			handleSentence(player, npc);
		}
	}

	private void handleSentence(Player player, EventRaiser npc) {
		Market market = TradeCenterZoneConfigurator.getShopFromZone(player.getZone());
		Set<Earning> earnings = market.fetchEarnings(player);
		int collectedSum = 0;
		for (Earning earning : earnings) {
			collectedSum += earning.getValue().intValue();
		}
		if (collectedSum > 0) {
			player.sendPrivateText("You collected "+Integer.valueOf(collectedSum).toString()+" money.");
			npc.say("Welcome to Semos trading center. I gave your pending earnings to you. What else can I do?");
		} else {
			//either you have no space in your bag or there isn't anything to collect
			npc.say("Welcome to Semos trading center. How can I #help you?");
		}
		npc.setCurrentState(ConversationStates.ATTENDING);
	}
}
