/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc.behaviour.adder;

import games.stendhal.common.constants.SoundLayer;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.BehaviourAction;
import games.stendhal.server.entity.npc.action.ComplainAboutSentenceErrorAction;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;
import games.stendhal.server.entity.npc.behaviour.journal.MerchantsRegister;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.SentenceHasErrorCondition;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.SoundEvent;

import org.apache.log4j.Logger;

public class SellerAdder {
	private static Logger logger = Logger.getLogger(SellerAdder.class);

    private final MerchantsRegister merchantsRegister = SingletonRepository.getMerchantsRegister();

    /**
	 * Behaviour parse result in the current conversation.
	 * Remark: There is only one conversation between a player and the NPC at any time.
	 */
	private ItemParserResult currentBehavRes;
	
	public void addSeller(final SpeakerNPC npc, final SellerBehaviour behaviour) {
		addSeller(npc, behaviour, true);
	}

	public void addSeller(final SpeakerNPC npc, final SellerBehaviour sellerBehaviour, final boolean offer) {
		final Engine engine = npc.getEngine();

		merchantsRegister.add(npc, sellerBehaviour);
		
		if (offer) {
			engine.add(
					ConversationStates.ATTENDING,
					ConversationPhrases.OFFER_MESSAGES,
					null,
					false,
					ConversationStates.ATTENDING, "I sell "
									+ Grammar.enumerateCollection(sellerBehaviour.dealtItems())
									+ ".", null);
		}

		engine.add(ConversationStates.ATTENDING, "buy", new SentenceHasErrorCondition(),
				false, ConversationStates.ATTENDING,
				null, new ComplainAboutSentenceErrorAction());

		ChatCondition condition = new AndCondition(
			new NotCondition(new SentenceHasErrorCondition()),
			new NotCondition(sellerBehaviour.getTransactionCondition()));
		engine.add(ConversationStates.ATTENDING, "buy", condition,
			false, ConversationStates.ATTENDING,
			null, sellerBehaviour.getRejectedTransactionAction());

		condition = new AndCondition(
			new NotCondition(new SentenceHasErrorCondition()),
			sellerBehaviour.getTransactionCondition());

		engine.add(ConversationStates.ATTENDING, "buy", condition, false,
				ConversationStates.ATTENDING, null,
				new BehaviourAction(sellerBehaviour, "buy", "sell") {
					@Override
					public void fireRequestOK(final ItemParserResult res, final Player player, final Sentence sentence, final EventRaiser raiser) {
						String chosenItemName = res.getChosenItemName();

						// find out if the NPC sells this item, and if so,
						// how much it costs.
						if (res.getAmount() > 1000) {
							logger.warn("Refusing to sell very large amount of "
									+ res.getAmount()
									+ " " + chosenItemName
									+ " to player "
									+ player.getName() + " talking to "
									+ raiser.getName() + " saying "
									+ sentence);
							raiser.say("Sorry, the maximum number of " 
									+ chosenItemName 
									+ " which I can sell at once is 1000.");
						} else if (res.getAmount() > 0) {
							StringBuilder builder = new StringBuilder();

							// When the user tries to buy several of a non-stackable
							// item, he is forced to buy only one.
							if (res.getAmount() != 1) {
								final Item item = sellerBehaviour.getAskedItem(chosenItemName);

								if (item == null) {
									logger.error("Trying to sell a nonexistent item: " + chosenItemName);
								} else if (!(item instanceof StackableItem)) {
									builder.append("You can only buy one " + chosenItemName + " at a time. ");
									res.setAmount(1);
								}
							}

							int price = sellerBehaviour.getUnitPrice(chosenItemName) * res.getAmount();
							if (player.isBadBoy()) {
								price = (int) (SellerBehaviour.BAD_BOY_BUYING_PENALTY * price);

								builder.append("To friends I charge less, but you seem like you have played unfairly here. So,  ");
								builder.append(Grammar.quantityplnoun(res.getAmount(), chosenItemName, "a"));
							} else {
								builder.append(Grammar.quantityplnoun(res.getAmount(), chosenItemName, "A"));
							}

							builder.append(" will cost ");
							builder.append(price);
							builder.append(". Do you want to buy ");
							builder.append(Grammar.itthem(res.getAmount()));
							builder.append("?");

							raiser.say(builder.toString());

							currentBehavRes = res;
							npc.setCurrentState(ConversationStates.BUY_PRICE_OFFERED); // success
						} else {
							raiser.say("Sorry, how many " + Grammar.plural(chosenItemName) + " do you want to buy?!");
						}
					}
				});

		engine.add(ConversationStates.BUY_PRICE_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				false, ConversationStates.ATTENDING,
				null, new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						final String itemName = currentBehavRes.getChosenItemName();
						logger.debug("Selling a " + itemName + " to player " + player.getName());

						boolean success = sellerBehaviour.transactAgreedDeal(currentBehavRes, raiser, player);
						if (success) {
							raiser.addEvent(new SoundEvent("coins-1", SoundLayer.CREATURE_NOISE));
						}

						currentBehavRes = null;
					}
				});

		engine.add(ConversationStates.BUY_PRICE_OFFERED,
				ConversationPhrases.NO_MESSAGES, null,
				false, ConversationStates.ATTENDING,
				"Ok, how else may I help you?", null);
	}

}
