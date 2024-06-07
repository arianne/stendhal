/***************************************************************************
 *                 Copyright © 2003-2024 - Faiumoni e. V.                  *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests.piedpiper;


import java.util.LinkedList;

import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.player.Player;

public class TPPQuestHelperFunctions implements ITPPQuestConstants {

	private static LinkedList<Creature> rats = new LinkedList<Creature>();

	public static final String MAIN_NPC_NAME = "Mayor Chalmers";

	public static SpeakerNPC getMainNPC() {
		return SingletonRepository.getNPCList().get(MAIN_NPC_NAME);
	}

	/**
	 * function for calculating reward's moneys for player
	 *
	 * @param player
	 * 			- player which must be rewarded
	 * @return
	 * 			gold amount for hunting rats.
	 */
	public static int calculateReward(Player player) {
		if (player.isQuestInState(QUEST_SLOT, 0, "done")) {
			// information for previous raid is no longer available
			return 0;
		}

		int moneys = 0;
		int kills = 0;
		for(int i=0; i<RAT_TYPES.size(); i++) {
			try {
				final String killed = player.getQuest(QUEST_SLOT,i+1);
				// have player quest slot or not yet?
				if (killed != null) {
					kills=Integer.decode(killed);
				}
			} catch (NumberFormatException nfe) {
				// player's quest slot don't contain valid number
				// so he didn't killed such creatures.
			}
			moneys = moneys + kills*RAT_REWARDS.get(i);
		}
		return(moneys);
	}


	public void setRats(LinkedList<Creature> rats) {
		TPPQuestHelperFunctions.rats = rats;
	}

	public static LinkedList<Creature> getRats() {
		return rats;
	}

	/**
	 * Get the amount of rats.
	 *
	 * @return rat count
	 */
	public static int getRatsCount() {
		return(getRats().size());
	}

	public static void setupPiper(SpeakerNPC piedpiper) {
		piedpiper.setEntityClass("holidaymakernpc");
		piedpiper.initHP(1000);
		piedpiper.setResistance(0);
		piedpiper.setVisibility(100);
		piedpiper.setAllowToActAlone(true);
		piedpiper.add(ConversationStates.IDLE,
							ConversationPhrases.GREETING_MESSAGES,
							new GreetingMatchesNameCondition(piedpiper.getName()), true,
							ConversationStates.IDLE,
							"hello",
							null);
		piedpiper.addEmotionReply("hugs", "smile to");
	}

	/**
	 * return random rat from allowed list
	 * @return a random rat creature
	 */
	public static Creature getRandomRat() {
		// Gaussian distribution
		int tc=Rand.randGaussian(0,RAT_TYPES.size());
		if ((tc>(RAT_TYPES.size()-1)) || (tc<0)) {
			tc=0;
		}
		final Creature tempCreature =
			new Creature((Creature) SingletonRepository.getEntityManager().getEntity(RAT_TYPES.get(tc)));
		return tempCreature;
	}
}
