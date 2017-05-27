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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Expression;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.trade.Market;
import games.stendhal.server.entity.trade.Offer;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * shows all current offers to the asking player
 *
 * @author madmetzger, kiheru, hendrik
 */
public class ShowOffersChatAction implements ChatAction {
	/** Maximum list length that is shown to the players */
	private static final int MAX_SHOWN_OFFERS = 20;

	@Override
	public void fire(Player player, Sentence sentence, EventRaiser npc) {
		if (sentence.hasError()) {
			npc.say("Sorry, I did not understand you. "
					+ sentence.getErrorString());
			npc.setCurrentState(ConversationStates.ATTENDING);
		} else if (sentence.getExpressions().iterator().next().toString().equals("show")){
			handleSentence(player, sentence, npc);
		}
	}

	private void handleSentence(Player player, Sentence sentence, EventRaiser npc) {

		boolean onlyMyOffers = checkForMineFilter(sentence);
		boolean onlyMyExpiredOffers = checkForMyExpiredFilter(sentence);
		boolean filterForMine = false;

		Market market = TradeCenterZoneConfigurator.getShopFromZone(player.getZone());

		// Figure out what to look for
		RPSlot offersSlot = market.getSlot(Market.OFFERS_SLOT_NAME);
		if (onlyMyExpiredOffers) {
			offersSlot = market.getSlot(Market.EXPIRED_OFFERS_SLOT_NAME);
			filterForMine = true;
		}
		if (onlyMyOffers) {
			filterForMine = true;
		}
		String wordFilter = null;
		if (!filterForMine) {
			wordFilter = getWordFilter(sentence);
		}

		//if the wordFilter is "offers" delegate back to ShowOfferItemsChatAction to prevent
		//the messages saying there are no offers in the market
		if(wordFilter != null && wordFilter.startsWith("offer")) {
			new ShowOfferItemsChatAction().fire(player, sentence, npc);
			return;
		}

		// Get the list of offers, and filter out all that we don't need
		List<Offer> offers = getOffers(offersSlot);
		if (filterForMine) {
			filterForMine(offers, player);
		}
		if (wordFilter != null) {
			filterForWord(offers, wordFilter);
		}

		StringBuilder offersMessage = new StringBuilder();
		MarketManagerNPC marketNPC = (MarketManagerNPC) npc.getEntity();

		boolean usingFilter = filterForMine || (wordFilter != null);
		int counter = buildMessage(offersMessage, offers, marketNPC.getOfferMap(), usingFilter);
		if (counter > 0) {
			player.sendPrivateText(offersMessage.toString());
		}
		if (counter == 0) {
			String expiredAddition = onlyMyExpiredOffers ? "expired " : "";
			player.sendPrivateText("There are currently no " + expiredAddition + "offers in the market.");
		}
	}

	private boolean checkForMineFilter(Sentence sentence) {
		for (Expression expression : sentence) {
			if(expression.toString().equals("mine")) {
				return true;
			}
		}
		return false;
	}

	private boolean checkForMyExpiredFilter(Sentence sentence) {
		for (Expression expression : sentence) {
			if (expression.getNormalized().equals("expire")) {
				return true;
			}
		}
		return false;
	}

	private String getWordFilter(Sentence sentence) {
		if (sentence.getObjectCount() > 0) {
			// A proper recognised item name. Look for those
			return sentence.getObject(0).getNormalized();
		} else {
			List<Expression> expressions = sentence.getExpressions();
			// Filter for the first other word
			if (expressions.size() > 1) {
				return expressions.get(1).getNormalized();
			}
		}

		return null;
	}

	private List<Offer> getOffers(RPSlot slot) {
		LinkedList<Offer> offers = new LinkedList<Offer>();
		for (RPObject rpObject : slot) {
			offers.add((Offer) rpObject);
		}
		return offers;
	}

	/**
	 * Filter out offers that do not belong to a given player.
	 *
	 * @param offers list of offers to be filtered
	 * @param player player whose offers should be retained on the list
	 */
	private void filterForMine(List<Offer> offers, Player player) {
		Iterator<Offer> it = offers.iterator();
		while (it.hasNext()) {
			if (!it.next().getOfferer().equals(player.getName())) {
				it.remove();
			}
		}
	}

	/**
	 * Filter out offers that do not match a given word.
	 *
	 * @param offers list of offers to be filtered
	 * @param word a word to check in item name or type
	 */
	private void filterForWord(List<Offer> offers, String word) {
		Iterator<Offer> it = offers.iterator();
		while (it.hasNext()) {
			Offer o = it.next();
			if (o.hasItem()) {
				Item item = o.getItem();
				if (!(item.getName().indexOf(word) != -1 || word.equals(item.getItemClass()))) {
					it.remove();
				}
			}
		}
	}

	/**
	 * Format a message out of an offer list, and update an offermap to match it.
	 *
	 * @param message message to fill with the offer list
	 * @param offers list of offers to format
	 * @param map offermap to be filled
	 * @param usingFilter was a filter used?
	 *
	 * @return number of offers added to the list and map
	 */
	private int buildMessage(StringBuilder message, List<Offer> offers, Map<String, Offer> map, boolean usingFilter) {
		int counter = 0;

		for (Offer offer : offers) {
			counter++;
			if (counter > MAX_SHOWN_OFFERS) {
				message.append("Only " + MAX_SHOWN_OFFERS + " first offers shown.");
				if (!usingFilter) {
					message.append(" You can filter the offer list. For example #show #meat will only show meat related offers.");
				}
				return counter;
			}

			Item item = offer.getItem();
			int quantity = 1;
			if (item instanceof StackableItem) {
				quantity = ((StackableItem) item).getQuantity();
			}

			message.append(counter);
			message.append(": ");
			message.append(Grammar.quantityplnoun(quantity, offer.getItemName(), "a"));
			message.append(" for ");
			message.append(offer.getPrice());
			message.append(" money");
			message.append("\n");
			map.put(Integer.toString(counter), offer);
		}

		return counter;
	}
}
