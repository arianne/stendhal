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

import games.stendhal.common.MathHelper;
import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.impl.HealerBehaviour;
import games.stendhal.server.entity.npc.behaviour.journal.ServicersRegister;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;

public class HealerAdder {

	private final ServicersRegister servicersRegister = SingletonRepository.getServicersRegister();

	/**
	 * Behaviour parse result in the current conversation.
	 * Remark: There is only one conversation between a player and the NPC at any time.
	 */
	private ItemParserResult currentBehavRes;


	/**
	 *<p>Makes this NPC a healer, i.e. someone who sets the player's hp to
	 * the value of their base hp.
	 *
	 *<p>Player killers are not healed at all even by healers who charge.
	 *
	 *<p>Too strong players (atk >35 or def > 35) cannot be healed for free.
	 *
	 *<p>Players who have done PVP in the last 2 hours cannot be healed free,
	 * unless they are very new to the game.
	 *
	 * @param npc
	 * 		SpeakerNPC
	 * @param cost
	 * 		The price which can be positive for a lump sum cost, 0 for free healing
	 * 		or negative for a price dependent on level of player.
	 */
	public void addHealer(final SpeakerNPC npc, final int cost) {
		final HealerBehaviour healerBehaviour = new HealerBehaviour(cost);
		servicersRegister.add(npc.getName(), healerBehaviour);

		final ChatAction calculateCostAction = new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				ItemParserResult res = new ItemParserResult(true, "heal", 1, null);

				int cost = healerBehaviour.getCharge(res, player);
				currentBehavRes = res;

				String badboymsg = "";
				if (player.isBadBoy()) {
					cost = cost * 2;
					currentBehavRes.setAmount(2);
					badboymsg = " Healing costs more for those who slay others.";
				}

				if (cost > 0) {
					raiser.say("Healing costs " + cost
							+ "." + badboymsg + " Do you have that much?");

					raiser.setCurrentState(ConversationStates.HEAL_OFFERED); // success
				} else if (cost < 0) {
					// price depends on level if cost was set at -1
					// where the factor is |cost| and we have a +1
					// to avoid 0 charge.
					cost = player.getLevel() * Math.abs(cost) + 1;
					raiser.say("Healing someone of your abilities costs "
							+ cost
							+ " money." + badboymsg + " Do you have that much?");

					raiser.setCurrentState(ConversationStates.HEAL_OFFERED); // success
				} else {
					if ((player.getAtk() > 35) || (player.getDef() > 35)) {
						raiser.say("Sorry, I cannot heal you because you are way too strong for my limited powers.");
					} else if ((!player.isNew()
							&& (player.getLastPVPActionTime() > System
									.currentTimeMillis()
									- 2
									* MathHelper.MILLISECONDS_IN_ONE_HOUR) || player.isBadBoy())) {
						// ignore the PVP flag for very young
						// characters
						// (low atk, low def AND low level)
						raiser.say("Sorry, but you have a bad aura, so that I am unable to heal you right now.");
					} else {
						raiser.say("There, you are healed. How else may I help you?");
						healerBehaviour.heal(player);
					}
				}
			}
		};

		final ChatAction healAction = new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				int cost = healerBehaviour.getCharge(currentBehavRes, player);
				if (cost < 0) {
					cost = player.getLevel() * Math.abs(cost) + 1;
				}
				if (player.drop("money", cost)) {
					healerBehaviour.heal(player);
					raiser.say("There, you are healed. How else may I help you?");
				} else {
					raiser.say("I'm sorry, but it looks like you can't afford it.");
				}

				currentBehavRes = null;
			}
		};

		addHealer(npc, calculateCostAction, healAction);
	}

	/**
	 * Makes the NPC a healer.
	 *
	 * @param npc
	 * 		SpeakerNPC who does the healing.
	 * @param calculateCostAction
	 * 		Action to take to determine cost.
	 * @param healAction
	 * 		Action to take when player is healed.
	 */
	public void addHealer(final SpeakerNPC npc, final ChatAction calculateCostAction, final ChatAction healAction) {
		// Give attribute to healers
		npc.put("job_healer", "");

		final Engine engine = npc.getEngine();

		engine.add(ConversationStates.ATTENDING,
				ConversationPhrases.OFFER_MESSAGES,
				null,
				false,
				ConversationStates.ATTENDING,
				"I can #heal you.",
				null);

		engine.add(ConversationStates.ATTENDING,
				"heal",
				null,
				false,
				ConversationStates.ATTENDING,
				null,
				calculateCostAction);

		engine.add(ConversationStates.HEAL_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				false,
				ConversationStates.ATTENDING,
				null,
				healAction);

		engine.add(ConversationStates.HEAL_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				false,
				ConversationStates.ATTENDING,
				"OK, how else may I help you?",
				null);
	}
}
