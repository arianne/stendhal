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
package games.stendhal.server.entity.npc.behaviour.adder;

import games.stendhal.common.Grammar;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.ComplainAboutSentenceErrorAction;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.SentenceHasErrorCondition;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.SoundEvent;

import org.apache.log4j.Logger;

public class SellerAdder {
	private static Logger logger = Logger.getLogger(SellerAdder.class);
	

	
	public void addSeller(final SpeakerNPC npc, final SellerBehaviour behaviour) {
		addSeller(npc, behaviour, true);
	}

	public void addSeller(final SpeakerNPC npc, final SellerBehaviour behaviour,
			final boolean offer) {
		final Engine engine = npc.getEngine();

		if (offer) {
			engine.add(
					ConversationStates.ATTENDING,
					ConversationPhrases.OFFER_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					"I sell "
							+ Grammar.enumerateCollection(behaviour.dealtItems())
							+ ".", null);
		}

		engine.add(ConversationStates.ATTENDING, "buy", new SentenceHasErrorCondition(),
				ConversationStates.ATTENDING, null,
				new ComplainAboutSentenceErrorAction());

		ChatCondition condition = new AndCondition(
			new NotCondition(new SentenceHasErrorCondition()),
			new NotCondition(behaviour.getTransactionCondition()));
		engine.add(ConversationStates.ATTENDING, "buy", condition,
			ConversationStates.ATTENDING, null,
			behaviour.getRejectedTransactionAction());

		condition = new AndCondition(
			new NotCondition(new SentenceHasErrorCondition()),
			behaviour.getTransactionCondition());
		engine.add(ConversationStates.ATTENDING, "buy", condition,
				ConversationStates.BUY_PRICE_OFFERED, null,
				new ChatAction() {

					public void fire(final Player player, final Sentence sentence,
							final EventRaiser raiser) {
						// find out what the player wants to buy, and how much of it
						if (behaviour.parseRequest(sentence)) {
							// find out if the NPC sells this item, and if so,
							// how much it costs.
							if (behaviour.getAmount() > 1000) {
								logger.warn("Refusing to sell very large amount of "
										+ behaviour.getAmount()
										+ " " + behaviour.getChosenItemName()
										+ " to player "
										+ player.getName() + " talking to "
										+ raiser.getName() + " saying "
										+ sentence);
								raiser.say("Sorry, the maximum number of " 
										+ behaviour.getChosenItemName() 
										+ " which I can sell at once is 1000.");
								raiser.setCurrentState(ConversationStates.ATTENDING);
							} else if (behaviour.getAmount() > 0) {
								int price = behaviour.getUnitPrice(behaviour.getChosenItemName())
									* behaviour.getAmount();

								StringBuilder builder = new StringBuilder();
								if (player.isBadBoy()) {
									price = (int) (SellerBehaviour.BAD_BOY_BUYING_PENALTY * price);

									builder.append("To friends I charge less, but you seem like you have played unfairly here. So,  ");
									builder.append(Grammar.quantityplnoun(behaviour.getAmount(), behaviour.getChosenItemName(), "a"));
								} else {
									builder.append(Grammar.quantityplnoun(behaviour.getAmount(), behaviour.getChosenItemName(), "A"));
								}
								builder.append(" will cost ");
								builder.append(price);
								builder.append(". Do you want to buy ");
								builder.append(Grammar.itthem(behaviour.getAmount()));
								builder.append("?");
								raiser.say(builder.toString());
							} else {
								raiser.say("Sorry, how many " + Grammar.plural(behaviour.getChosenItemName()) + " do you want to buy?!");

								raiser.setCurrentState(ConversationStates.ATTENDING);
							}
						} else {
							if (behaviour.getChosenItemName() == null) {
								raiser.say("Please tell me what you want to buy.");
							} else {
								raiser.say("Sorry, I don't sell "
										+ Grammar.plural(behaviour.getChosenItemName()) + ".");
							}

							raiser.setCurrentState(ConversationStates.ATTENDING);
						}
					}
				});

		engine.add(ConversationStates.BUY_PRICE_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.ATTENDING, null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						final String itemName = behaviour.getChosenItemName();
						logger.debug("Selling a " + itemName + " to player " + player.getName());

						boolean success = behaviour.transactAgreedDeal(raiser, player);
						if (success) {
							raiser.addEvent(new SoundEvent("coins-1", SoundLayer.CREATURE_NOISE));
						}
					}
				});

		engine.add(ConversationStates.BUY_PRICE_OFFERED,
				ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.ATTENDING, "Ok, how else may I help you?",
				null);
	}

}
