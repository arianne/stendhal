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

import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Expression;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.dbcommand.LogTradeEventCommand;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.OwnedItem;
import games.stendhal.server.entity.item.RingOfLife;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.LevelGreaterThanCondition;
import games.stendhal.server.entity.npc.condition.LevelLessThanCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.trade.Market;
import games.stendhal.server.entity.trade.Offer;
import games.stendhal.server.util.AsynchronousProgramExecutor;
import marauroa.server.db.command.DBCommandQueue;


public class PrepareOfferHandler {
	private Item item;
	private int price;
	private int quantity;

	public void add(SpeakerNPC npc) {
		npc.add(ConversationStates.ATTENDING, "sell",
				new LevelLessThanCondition(6),
				ConversationStates.ATTENDING,
				"I am sorry, I only accept offers from people who have a good reputation. You can gain experience by helping people with their tasks or defending the city from evil creatures.", null);
		npc.add(ConversationStates.ATTENDING, "sell",
				new LevelGreaterThanCondition(5),
				ConversationStates.ATTENDING, null,
				new PrepareOfferChatAction());
		npc.add(ConversationStates.ATTENDING, "sell", null, ConversationStates.ATTENDING, null,
				new PrepareOfferChatAction());
		npc.add(ConversationStates.SELL_PRICE_OFFERED, ConversationPhrases.YES_MESSAGES,
				ConversationStates.ATTENDING, null, new ConfirmPrepareOfferChatAction());
		npc.add(ConversationStates.SELL_PRICE_OFFERED, ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.ATTENDING, "Ok, how else may I help you?", null);
	}

	private void setData(Item item, int price, int quantity) {
		this.item = item;
		this.price = price;
		this.quantity = quantity;
	}

	/**
	 * Builds the message for the tweet to be posted
	 * @param i the offered item
	 * @param q the quantity of the offered item
	 * @param p the price for the item
	 * @return the message to be posted in the tweet
	 */
	public String buildTweetMessage(Item i, int q, int p) {
		StringBuilder message = new StringBuilder();
		message.append("New offer for ");
		message.append(Grammar.quantityplnoun(q, i.getName(), "a"));
		message.append(" at ");
		message.append(p);
		message.append(" money. ");
		String stats = "";
		String description = i.describe();
		int start = description.indexOf("Stats are (");
		if(start > -1) {
			stats = description.substring(start);
		}
		message.append(stats);
		return message.toString();
	}

	private class PrepareOfferChatAction implements ChatAction {
		@Override
		public void fire(Player player, Sentence sentence, EventRaiser npc) {
			if (sentence.hasError()) {
				npc.say("Sorry, I did not understand that strange offer.");
				npc.setCurrentState(ConversationStates.ATTENDING);
			} else if (sentence.getExpressions().iterator().next().toString().equals("sell")){
				handleSentence(player, sentence, npc);
			}
		}

		private void handleSentence(Player player, Sentence sentence, EventRaiser npc) {
			if(TradingUtility.isPlayerWithinOfferLimit(player)) {
				if (sentence.getExpressions().size() < 3 || sentence.getNumeralCount() != 1) {
					npc.say("I did not understand you. Please say \"sell item price\".");
					npc.setCurrentState(ConversationStates.ATTENDING);
					return;
				}
				String itemName = determineItemName(sentence);
				int number = determineNumber(sentence);
				int price = determinePrice(sentence);
				Integer fee = Integer.valueOf(TradingUtility.calculateFee(player, price).intValue());
				if(TradingUtility.canPlayerAffordTradingFee(player, price)) {
					Item item = player.getFirstEquipped(itemName);
					if (item == null) {
						// Some items are in plural. look for those
						item = player.getFirstEquipped(Grammar.plural(itemName));
					}

					if (item == null) {
						npc.say("Sorry, but I don't think you have any "
								+ Grammar.plural(itemName)+ ".");
						return;
					}
					// The item name might not be what was used for looking it up (plurals)
					itemName = item.getName();

					String owner = null;
					if (item.isBound()) {
						owner = player.getName();
					} else if (item instanceof OwnedItem) {
						owner = ((OwnedItem) item).getOwner();
					}

					if (itemName.equals("money")) {
						npc.say("Oh, offering money for money? That sounds rather fishy. I am sorry, I cannot do that");
						return;
					} else if ((number > 1) && !(item instanceof StackableItem)) {
						npc.say("Sorry, you can only put those for sale as individual items.");
						return;
					} else if (owner != null) {
						if (owner.equals(player.getName())) {
							owner = "you";
						}
						npc.say("That " + itemName + " can be used only by " + owner + ". I cannot sell it.");
						return;
					} else if (item.getDeterioration() > 0) {
						npc.say("That " + itemName + " is damaged. I cannot sell it.");
						return;
					} else if (number > 1000) {
						npc.say("Sorry, my storage is not large enough for such a huge amout of " + Grammar.plural(itemName) + ".");
						return;
					} else if (price > 1000000) {
						npc.say("That is a huge amount of money you want for your " + Grammar.plural(itemName) + ". I am sorry I cannot accept this offer.");
						return;
					} else if (item.hasSlot("content") && item.getSlot("content").size() > 0) {
						npc.say("Please empty your " + itemName + " first.");
						return;
					} else if (item instanceof RingOfLife) {
					    // broken ring of life should not be sold via Harold
					    if(((RingOfLife) item).isBroken()) {
					        npc.say("Please repair your " + itemName + " before trying to sell it.");
					        return;
					    }
					}

					// All looks ok so far. Ask confirmation from the player.
					setData(item, price, number);
					StringBuilder msg = new StringBuilder();
					msg.append("Do you want to sell ");
					msg.append(Grammar.quantityplnoun(number, itemName, "a"));
					msg.append(" for ");
					if (number != 1) {
						msg.append("total ");
					}
					msg.append(price);
					msg.append(" money? It would cost you ");
					msg.append(fee);
					msg.append(" money.");
					npc.say(msg.toString());

					npc.setCurrentState(ConversationStates.SELL_PRICE_OFFERED);
					return;
				}
				npc.say("You cannot afford the trading fee of " + fee.toString());
				return;
			}
			npc.say("You may not place more than " + TradingUtility.MAX_NUMBER_OFF_OFFERS + " offers.");
		}

		private int determineNumber(Sentence sentence) {
			Expression expression = sentence.getExpression(1,"");
			return expression.getAmount();
		}

		private String determineItemName(Sentence sentence) {
			Expression expression = sentence.getExpression(1,"");
			return expression.getNormalized();
		}

		private int determinePrice(Sentence sentence) {
			return sentence.getNumeral().getAmount();
		}
	}

	private class ConfirmPrepareOfferChatAction implements ChatAction {
		@Override
		public void fire(Player player, Sentence sentence, EventRaiser npc) {
			int fee = TradingUtility.calculateFee(player, price).intValue();
			if (TradingUtility.canPlayerAffordTradingFee(player, price)) {
				if (createOffer(player, item, price, quantity)) {
					TradingUtility.substractTradingFee(player, price);
					new AsynchronousProgramExecutor("trade", buildTweetMessage(item, quantity, price)).start();
					DBCommandQueue.get().enqueue(new LogTradeEventCommand(player, item, quantity, price));
					npc.say("I added your offer to the trading center and took the fee of "+ fee +".");
					npc.setCurrentState(ConversationStates.ATTENDING);
				} else {
					npc.say("You don't have " + Grammar.quantityplnoun(quantity, item.getName(), "a") + ".");
				}
				return;
			}
			npc.say("You cannot afford the trading fee of " + fee);
		}

		/**
		 * Try creating an offer.
		 *
		 * @param player the player who makes the offer
		 * @param item item for sale
		 * @param price price for the item
		 * @param number number of items to sell
		 * @return true if making the offer was successful, false otherwise
		 */
		private boolean createOffer(Player player, Item item, int price, int number) {
			Market shop = TradeCenterZoneConfigurator.getShopFromZone(player.getZone());
			if(shop != null) {
				Offer o = shop.createOffer(player, item, Integer.valueOf(price), Integer.valueOf(number));
				if (o == null) {
					return false;
				}

				StringBuilder message = new StringBuilder("Offer for some ");
				message.append(item.getName());
				message.append(" at ");
				message.append(price);
				message.append(" created. ");
				String messageNumberOfOffers = "You have now made "
					+ Grammar.quantityplnoun(Integer.valueOf(shop.countOffersOfPlayer(player)), "offer", "one") + ".";
				player.sendPrivateText(message.toString() + messageNumberOfOffers);
				return true;
			}
			return false;
		}
	}
}
