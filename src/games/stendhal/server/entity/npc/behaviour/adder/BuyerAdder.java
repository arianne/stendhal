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
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.BehaviourAction;
import games.stendhal.server.entity.npc.action.ComplainAboutSentenceErrorAction;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;
import games.stendhal.server.entity.npc.behaviour.journal.MerchantsRegister;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.SentenceHasErrorCondition;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.SoundEvent;

import org.apache.log4j.Logger;

public class BuyerAdder {
	private static Logger logger = Logger.getLogger(BuyerAdder.class);
	
    private final MerchantsRegister merchantsRegister = SingletonRepository.getMerchantsRegister();

	/**
	 * Behaviour parse result in the current conversation.
	 * Remark: There is only one conversation between a player and the NPC at any time.
	 */
	private ItemParserResult currentBehavRes;

	public void addBuyer(final SpeakerNPC npc, final BuyerBehaviour buyerBehaviour, final boolean offer) {
		final Engine engine = npc.getEngine();
		
		merchantsRegister.add(npc, buyerBehaviour);

		if (offer) {
			engine.add(
					ConversationStates.ATTENDING,
					ConversationPhrases.OFFER_MESSAGES,
					null,
					false,
					ConversationStates.ATTENDING, 
					"I buy " + Grammar.enumerateCollectionPlural(buyerBehaviour.dealtItems()) + ".",
					null);
		}
		engine.add(ConversationStates.ATTENDING, "sell", new SentenceHasErrorCondition(),
				false, ConversationStates.ATTENDING,
				null, new ComplainAboutSentenceErrorAction());

		engine.add(ConversationStates.ATTENDING, "sell",
			new AndCondition(
					new NotCondition(new SentenceHasErrorCondition()),
					new NotCondition(buyerBehaviour.getTransactionCondition())),
			false, ConversationStates.ATTENDING,
			null, buyerBehaviour.getRejectedTransactionAction());

		engine.add(ConversationStates.ATTENDING, "sell",
				new AndCondition(
						new NotCondition(new SentenceHasErrorCondition()),
						buyerBehaviour.getTransactionCondition()),
				false, ConversationStates.ATTENDING,
				null,
				new BehaviourAction(buyerBehaviour, "sell", "buy") {
					@Override
					public void fireRequestOK(final ItemParserResult res, final Player player, final Sentence sentence, final EventRaiser raiser) {
						if (player.isBadBoy()) {
							// don't buy from player killers at all
							raiser.say("Sorry, but I just can't trust you. You look too dangerous to deal with. Please go away.");
							raiser.setCurrentState(ConversationStates.IDLE);
							return;
						}

						String chosenItemName = res.getChosenItemName();

						if (res.getAmount() > 1000) {
							logger.warn("Refusing to buy very large amount of "
									+ res.getAmount()
									+ " " + chosenItemName
									+ " from player "
									+ player.getName() + " talking to "
									+ raiser.getName() + " saying "
									+ sentence);
							raiser.say("Sorry, the maximum number of " 
									+ chosenItemName 
									+ " which I can buy at once is 1000.");
						} else if (res.getAmount() > 0) {
							final String itemName = chosenItemName;
							// will check if player have claimed amount of items
							if (itemName.equals("sheep")) {
								// player have no sheep...
								if (!player.hasSheep()) {
									raiser.say("You don't have any sheep, " + player.getTitle() + "! What are you trying to pull?");
									return;
								}
							} else {
								// handle other items as appropriate
							}

							final int price = buyerBehaviour.getCharge(res, player);

							if (price != 0) {
    							raiser.say(Grammar.quantityplnoun(res.getAmount(), chosenItemName, "A")
    									+ " " + Grammar.isare(res.getAmount()) + " worth "
    									+ price + ". Do you want to sell "
    									+ Grammar.itthem(res.getAmount()) + "?");

    							currentBehavRes = res;
    							npc.setCurrentState(ConversationStates.SELL_PRICE_OFFERED); // success
							} else {
								raiser.say("Sorry, " 
										+ Grammar.thatthose(res.getAmount()) + " " 
										+ Grammar.plnoun(res.getAmount(), chosenItemName)
    									+ " " + Grammar.isare(res.getAmount()) + " worth nothing.");
							}
						} else {
							raiser.say("Sorry, how many " + Grammar.plural(chosenItemName) + " do you want to sell?!");
						}
					}
				});

		engine.add(ConversationStates.SELL_PRICE_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				false, ConversationStates.ATTENDING,
				null, new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						logger.debug("Buying something from player " + player.getName());

						boolean success = buyerBehaviour.transactAgreedDeal(currentBehavRes, raiser, player);
						if (success) {
							raiser.addEvent(new SoundEvent("coins-1", SoundLayer.CREATURE_NOISE));
						}

						currentBehavRes = null;
					}
				});

		engine.add(ConversationStates.SELL_PRICE_OFFERED,
				ConversationPhrases.NO_MESSAGES, null,
				false,
				ConversationStates.ATTENDING, "Ok, then how else may I help you?", null);
	}

}
