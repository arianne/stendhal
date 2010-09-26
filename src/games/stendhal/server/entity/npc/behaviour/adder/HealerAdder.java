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
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.impl.HealerBehaviour;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

public class HealerAdder {

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
	 *            SpeakerNPC
	 * @param cost
	 *            The price which can be positive for a lump sum cost, 0 for free healing
	 *            or negative for a price dependent on level of player.
	 */
	public void addHealer(final SpeakerNPC npc, final int cost) {
		final HealerBehaviour healerBehaviour = new HealerBehaviour(cost);
		final Engine engine = npc.getEngine();

		engine.add(ConversationStates.ATTENDING,
				ConversationPhrases.OFFER_MESSAGES, null,
				ConversationStates.ATTENDING, "I can #heal you.", null);

		engine.add(ConversationStates.ATTENDING, "heal", null,
				ConversationStates.HEAL_OFFERED, null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence,
							final EventRaiser raiser) {
						healerBehaviour.setChosenItemName("heal");
						healerBehaviour.setAmount(1);
						int cost = healerBehaviour.getCharge(player);
						if (player.isBadBoy()) {
							// don't heal player killers at all
							raiser.say("You killed another soul recently, giving you an aura of evil. I cannot, and will not, heal you.");
							raiser.setCurrentState(ConversationStates.ATTENDING);
						} else {
							if (cost > 0) {
								raiser.say("Healing costs " + cost
										+ ". Do you have that much?");
							} else if (cost < 0) {
								// price depends on level if cost was set at -1
								// where the factor is |cost| and we have a +1
								// to avoid 0 charge.
								cost = player.getLevel() * Math.abs(cost) + 1;
								raiser.say("Healing someone of your abilities costs "
												+ cost
												+ " money. Do you have that much?");
							} else {
								if ((player.getATK() > 35) || (player.getDEF() > 35)) {
									raiser.say("Sorry, I cannot heal you because you are way too strong for my limited powers.");
								} else if (!player.isNew()
										&& (player.getLastPVPActionTime() > System
												.currentTimeMillis()
												- 2
												* MathHelper.MILLISECONDS_IN_ONE_HOUR)) {
									// ignore the PVP flag for very young
									// characters
									// (low atk, low def AND low level)
									raiser.say("Sorry, but you have a bad aura, so that I am unable to heal you right now.");
								} else {
									raiser.say("There, you are healed. How else may I help you?");
									healerBehaviour.heal(player);
								}
								raiser.setCurrentState(ConversationStates.ATTENDING);
							}
						}
					}
				});

		engine.add(ConversationStates.HEAL_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.ATTENDING, null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence,
							final EventRaiser raiser) {
						int cost = healerBehaviour.getCharge(player);
						if (cost < 0) {
							cost = player.getLevel() * Math.abs(cost) + 1;
						}
						if (player.drop("money",
								cost)) {
							healerBehaviour.heal(player);
							raiser.say("There, you are healed. How else may I help you?");
						} else {
							raiser.say("I'm sorry, but it looks like you can't afford it.");
						}
					}
				});

		engine.add(ConversationStates.HEAL_OFFERED,
				ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.ATTENDING, "OK, how else may I help you?",
				null);
	}

}
