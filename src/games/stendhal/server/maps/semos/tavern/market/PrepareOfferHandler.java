package games.stendhal.server.maps.semos.tavern.market;

import games.stendhal.common.Grammar;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Expression;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.trade.Market;
import games.stendhal.server.entity.trade.Offer;


public class PrepareOfferHandler {
	private Item item;
	private int price;
	private int quantity;
	
	public void add(SpeakerNPC npc) {
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
	
	
	private class PrepareOfferChatAction implements ChatAction {
		public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
			if (sentence.hasError()) {
				npc.say("Sorry, I did not understand that strange offer.");
				npc.setCurrentState(ConversationStates.ATTENDING);
			} else if (sentence.getExpressions().iterator().next().toString().equals("sell")){
				handleSentence(player,sentence,npc);
			}
		}

		private void handleSentence(Player player, Sentence sentence, SpeakerNPC npc) {
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
					
					if (item.isBound()) {
						npc.say("That " + itemName + " can be used only by you. I can not sell it.");
						return;
					} else if (number > 1000) {
						npc.say("Sorry, my storage is not large enough for such a huge amout of " + Grammar.plural(itemName) + ".");
						return;
					} else if (price > 1000000) {
						npc.say("That is a huge amount of money you want for your " + Grammar.plural(itemName) + ". I am sorry I cannot accept this offer.");
						return;
					}

					// All looks ok so far. Ask confirmation from the player.
					setData(item, price, number);
					StringBuilder msg = new StringBuilder();
					msg.append("Do you want to sell ");
					msg.append(Grammar.quantityplnoun(number, itemName));
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
			npc.say("I am new on this job, i used to be a gatekeeper in Fado. I currently only accept "
					+ TradingUtility.MAX_NUMBER_OFF_OFFERS
					+ " offers per person because I am still learning and afraid that I might mess something up.");
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
		public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
			int fee = TradingUtility.calculateFee(player, price).intValue();
			if (TradingUtility.canPlayerAffordTradingFee(player, price)) {
				if (createOffer(player, item, price, quantity)) {
					TradingUtility.substractTradingFee(player, price);
					npc.say("I added your offer to the trading center and took the fee of "+ fee +".");
					npc.setCurrentState(ConversationStates.ATTENDING);
				} else {
					npc.say("You don't have " + Grammar.quantityplnoun(quantity, item.getName()) + ".");
				}
				return;
			}
			npc.say("You cannot afford the trading fee of " + fee);
		}
		
		/**
		 * Try creating an offer.
		 * 
		 * @param player the player who makes the offer
		 * @param itemName name of the item for sale
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

				StringBuilder message = new StringBuilder("Offer for ");
				message.append(item.getName());
				message.append(" at ");
				message.append(price);
				message.append(" created. ");
				String messageNumberOfOffers = "You have now made "
					+ Grammar.quantityplnoun(Integer.valueOf(shop.countOffersOfPlayer(player)),"offer") + ".";
				player.sendPrivateText(message.toString() + messageNumberOfOffers);
				return true;
			}
			return false;
		}
	}
}
