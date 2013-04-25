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
import games.stendhal.server.entity.npc.behaviour.impl.OutfitChangerBehaviour;
import games.stendhal.server.entity.npc.behaviour.journal.ServicersRegister;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

import org.apache.log4j.Logger;

public class OutfitChangerAdder {
	private static Logger logger = Logger.getLogger(OutfitChangerAdder.class);
	
    private final ServicersRegister servicersRegister = SingletonRepository.getServicersRegister();
    
	/**
	 * Behaviour parse result in the current conversation.
	 * Remark: There is only one conversation between a player and the NPC at any time.
	 */
	private ItemParserResult currentBehavRes;

	/**
	 * Makes this NPC an outfit changer, i.e. someone who can give players
	 * special outfits.
	 * 
	 * @param npc
	 *            SpeakerNPC
	 * @param behaviour
	 *            The behaviour (which includes a pricelist).
	 * @param command
	 *            The action needed to get the outfit, e.g. "buy", "lend".
	 */
	public void addOutfitChanger(final SpeakerNPC npc,
			final OutfitChangerBehaviour behaviour, final String command) {
		addOutfitChanger(npc, behaviour, command, true, true);
	}

	/**
	 * Makes this NPC an outfit changer, i.e. someone who can give players
	 * special outfits.
	 * 
	 * @param npc
	 *            SpeakerNPC
	 * @param outfitBehaviour
	 *            The behaviour (which includes a pricelist).
	 * @param action
	 *            The action needed to get the outfit, e.g. "buy", "lend".
	 * @param offer
	 *            Defines if the NPC should react to the word "offer".
	 * @param canReturn
	 *            If true, a player can say "return" to get his original outfit
	 *            back.
	 */
	public void addOutfitChanger(final SpeakerNPC npc,
			final OutfitChangerBehaviour outfitBehaviour, final String action,
			final boolean offer, final boolean canReturn) {
		
		servicersRegister.add(npc.getName(), outfitBehaviour);

		final Engine engine = npc.getEngine();
		if (offer) {
			engine.add(
					ConversationStates.ATTENDING,
					ConversationPhrases.OFFER_MESSAGES,
					null,
					false,
					ConversationStates.ATTENDING,
					"You can #"
							+ action
							+ " "
							+ Grammar.enumerateCollection(outfitBehaviour.dealtItems())
							+ ".", null);
		}

		engine.add(ConversationStates.ATTENDING, action, null, false,
				ConversationStates.ATTENDING, null,
				new BehaviourAction(outfitBehaviour, action, "offer") {
					@Override
					public void fireRequestOK(final ItemParserResult res, Player player, Sentence sentence, EventRaiser raiser) {
						// find out what the player wants to wear

						// We ignore any amounts.
						res.setAmount(1);

						final int price = outfitBehaviour.getUnitPrice(res.getChosenItemName()) * res.getAmount();

						raiser.say("To " + action + " a " + res.getChosenItemName() + " will cost " + price
								+ ". Do you want to " + action + " it?");

						currentBehavRes = res;
						raiser.setCurrentState(ConversationStates.BUY_PRICE_OFFERED); // success
					}
				});

		engine.add(ConversationStates.BUY_PRICE_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				false, ConversationStates.ATTENDING,
				null, new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence,
							final EventRaiser npc) {
						final String itemName = currentBehavRes.getChosenItemName();
						logger.debug("Selling a " + itemName + " to player " + player.getName());

						if (outfitBehaviour.transactAgreedDeal(currentBehavRes, npc, player)) {
							if (canReturn) {
								npc.say("Thanks, and please don't forget to #return it when you don't need it anymore!");
								// -1 is also the public static final int NEVER_WEARS_OFF = -1; 
								// but it doesn't recognise it here ...
							} else if (outfitBehaviour.getEndurance() != -1) {
								// timeUntil takes a parameter in seconds so we multiply the endurance in minutes by 60
								npc.say("Thanks! You can wear this for " +  TimeUtil.timeUntil(60 * outfitBehaviour.getEndurance()) + ".");
							} else {
								npc.say("Thanks!");
							}
						}

						currentBehavRes = null;
					}
				});

		engine.add(ConversationStates.BUY_PRICE_OFFERED,
				ConversationPhrases.NO_MESSAGES, null,
				false, ConversationStates.ATTENDING,
				"Ok, how else may I help you?", null);

		if (canReturn) {
			engine.add(ConversationStates.ATTENDING, "return", null,
					false, ConversationStates.ATTENDING,
					null, new ChatAction() {
						@Override
						public void fire(final Player player, final Sentence sentence,
								final EventRaiser npc) {
							if (outfitBehaviour.returnToOriginalOutfit(player)) {
								npc.say("Thank you!");
							} else {
								npc.say("I can't remember that I gave you anything.");
							}

							currentBehavRes = null;
						}
					});
		}
	}

}
