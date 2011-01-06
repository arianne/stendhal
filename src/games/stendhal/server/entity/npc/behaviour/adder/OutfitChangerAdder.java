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

import games.stendhal.common.Grammar;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.impl.OutfitChangerBehaviour;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

import org.apache.log4j.Logger;

public class OutfitChangerAdder {
	private static Logger logger = Logger.getLogger(OutfitChangerAdder.class);

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
	 * @param behaviour
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
			final OutfitChangerBehaviour behaviour, final String action,
			final boolean offer, final boolean canReturn) {

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
							+ Grammar.enumerateCollection(behaviour.dealtItems())
							+ ".", null);
		}

		engine.add(ConversationStates.ATTENDING, action, null,
				false, ConversationStates.BUY_PRICE_OFFERED,
				null, new ChatAction() {

					public void fire(final Player player, final Sentence sentence,
							final EventRaiser raiser) {
						if (sentence.hasError()) {
							raiser.say("Sorry, I did not understand you. "
									+ sentence.getErrorString());
						}

						// find out what the player wants to wear
						boolean found = behaviour.parseRequest(sentence);
						String chosenItemName = behaviour.getChosenItemName();

						boolean success = true;

						if (found) {
							// We ignore any amounts.
							behaviour.setAmount(1);

							final int price = behaviour.getUnitPrice(chosenItemName)
									* behaviour.getAmount();

							raiser.say("To " + action + " a " + chosenItemName + " will cost " + price
									+ ". Do you want to " + action + " it?");

							success = true;
						} else {
							if (chosenItemName == null) {
								raiser.say("Please tell me what you want to " + action + ".");
							} else {
								raiser.say("Sorry, I don't offer " + Grammar.plural(chosenItemName) + ".");
							}
						}

						if (!success) {
							raiser.setCurrentState(ConversationStates.ATTENDING);
						}
					}
				});

		engine.add(ConversationStates.BUY_PRICE_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				false, ConversationStates.ATTENDING,
				null, new ChatAction() {
					public void fire(final Player player, final Sentence sentence,
							final EventRaiser npc) {
						final String itemName = behaviour.getChosenItemName();
						logger.debug("Selling a " + itemName + " to player "
								+ player.getName());

						if (behaviour.transactAgreedDeal(npc, player)) {
							if (canReturn) {
								npc.say("Thanks, and please don't forget to #return it when you don't need it anymore!");
								// -1 is also the public static final int NEVER_WEARS_OFF = -1; 
								// but it doesn't recognise it here ...
							} else if (behaviour.endurance != -1) {
								npc.say("Thanks! This will wear off in " +  TimeUtil.timeUntil((int) (behaviour.endurance * 0.3)) + ".");
							} else {
								npc.say("Thanks!");
							}
						}
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
						public void fire(final Player player, final Sentence sentence,
								final EventRaiser npc) {
							if (behaviour.returnToOriginalOutfit(player)) {
								npc.say("Thank you!");
							} else {
								npc.say("I can't remember that I gave you anything.");
							}
						}
					});
		}
	}

}
