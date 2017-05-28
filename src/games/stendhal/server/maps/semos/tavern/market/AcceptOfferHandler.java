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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.dbcommand.StoreMessageCommand;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.trade.Market;
import games.stendhal.server.entity.trade.Offer;
import marauroa.server.db.command.DBCommandQueue;

public class AcceptOfferHandler extends OfferHandler {
	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(AcceptOfferChatAction.class);
	private static final List<String> TRIGGERS = Arrays.asList("buy", "accept");

	@Override
	public void add(SpeakerNPC npc) {
		npc.add(ConversationStates.ATTENDING, TRIGGERS, null, ConversationStates.ATTENDING, null,
				new AcceptOfferChatAction());
		npc.add(ConversationStates.BUY_PRICE_OFFERED, ConversationPhrases.YES_MESSAGES,
				ConversationStates.ATTENDING, null, new ConfirmAcceptOfferChatAction());
		npc.add(ConversationStates.BUY_PRICE_OFFERED, ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.ATTENDING, "Ok, how else may I help you?", null);
	}

	class AcceptOfferChatAction extends KnownOffersChatAction {
		@Override
		public void fire(Player player, Sentence sentence, EventRaiser npc) {
			if (sentence.hasError()) {
				npc.say("Sorry, I did not understand you. "
						+ sentence.getErrorString());
			} else {
				handleSentence(sentence, npc);
			}
		}

		private void handleSentence(Sentence sentence, EventRaiser npc) {
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
						setOffer(o);
						int quantity = getQuantity(o.getItem());
						npc.say("Do you want to buy " + Grammar.quantityplnoun(quantity, o.getItem().getName(), "a") + " for " + o.getPrice() + " money?");
						npc.setCurrentState(ConversationStates.BUY_PRICE_OFFERED);
						return;
					}
				}
				npc.say("Sorry, please choose a number from those I told you to accept an offer.");
			} catch (NumberFormatException e) {
				npc.say("Sorry, please say #accept #number");
			}
		}
	}

	class ConfirmAcceptOfferChatAction implements ChatAction {
		@Override
		public void fire (Player player, Sentence sentence, EventRaiser npc) {
			Offer offer = getOffer();
			Market m = TradeCenterZoneConfigurator.getShopFromZone(player.getZone());
			String itemname = offer.getItemName();
			if (m.acceptOffer(offer,player)) {
				// Successful trade. Tell the offerer
				StringBuilder earningToFetchMessage = new StringBuilder();
				earningToFetchMessage.append("Your ");
				earningToFetchMessage.append(itemname);
				earningToFetchMessage.append(" was sold. You can now fetch your earnings from me.");

				logger.debug("sending a notice to '" + offer.getOfferer() + "': " + earningToFetchMessage.toString());
				DBCommandQueue.get().enqueue(new StoreMessageCommand("Harold", offer.getOfferer(), earningToFetchMessage.toString(), "N"));

				npc.say("Thanks.");
				// Obsolete the offers, since the list has changed
				((MarketManagerNPC) npc.getEntity()).getOfferMap().clear();
			} else {
				// Trade failed for some reason. Check why, and inform the player
				if (!m.contains(offer)) {
					int quantity = getQuantity(offer.getItem());
					npc.say("I'm sorry, but " + Grammar.thatthose(quantity) + " "
							+ Grammar.quantityplnoun(quantity, offer.getItem().getName(), "the")
							+ " " + Grammar.isare(quantity)
							+ " no longer for sale.");
				} else {
					npc.say("Sorry, you don't have enough money!");
				}
			}
		}
	}
}
