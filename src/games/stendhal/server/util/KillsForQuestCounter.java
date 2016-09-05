/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.util;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.server.entity.player.Player;

/**
 * Helps counting the number of kills needed for a quest.
 */
public class KillsForQuestCounter {

	private static final Logger LOGGER = Logger.getLogger(KillsForQuestCounter.class);

	private final String questState;

	/**
	 * Initializes a kill counter for a quest, based on the recorded quest
	 * state.
	 *
	 * @param questState
	 *            the recorded quest state, in the form:
	 *            "creatureName,requiredSoloKills,requiredSharedKills,totalSoloKillsWhenQuestStarted,totalSharedKillsWhenQuestStarted"
	 */
	public KillsForQuestCounter(String questState) {
		this.questState = questState;
	}

	/**
	 * Tells the number of times the specified player still needs to kill the
	 * specified kind of creature in order to fulfill the quest's requirements.
	 *
	 * @param player
	 *            the player to count for
	 * @param creature
	 *            the creature to be killed
	 * @return a positive number representing the number of kills still needed,
	 *         or {@code -1} in case of an error
	 * @see {@link games.stendhal.server.entity.npc.condition.KilledForQuestCondition}
	 */
	public int remainingKills(Player player, String creature) {
		final List<String> tokens = Arrays.asList(questState.split(","));
		// check for size - it should be able to divide by 5 without remainder.
		if ((tokens.size() % 5) != 0) {
			LOGGER.error("Wrong record in quest slot of player " + player.getName() + ": [" + questState + "]");
			return -1;
		}

		for (int i = 0; i < tokens.size() / 5; i++) {
			final String creatureName = tokens.get(i * 5);
			if (creature.equals(creatureName)) {
				List<String> tokensForCreature = tokens.subList(i * 5, i * 5 + 5);
				return remainingKills(player, tokensForCreature);
			}
		}
		LOGGER.warn("Player " + player.getName() + " was not requested to kill any " + creature);
		return -1;
	}

	private int remainingKills(Player player, List<String> tokensForCreature) {
		final String creatureName = tokensForCreature.get(0);
		int toKillSolo;
		int toKillShared;
		int killedSolo;
		int killedShared;
		try {
			toKillSolo = Integer.parseInt(tokensForCreature.get(1));
			toKillShared = Integer.parseInt(tokensForCreature.get(2));
			killedSolo = Integer.parseInt(tokensForCreature.get(3));
			killedShared = Integer.parseInt(tokensForCreature.get(4));
		} catch (NumberFormatException nfe) {
			LOGGER.error("NumberFormatException while parsing numbers in quest slot " + questState + " of player "
					+ player.getName() + ", creature " + creatureName);
			return Integer.MAX_VALUE;
		}
		final int killedSoloSinceQuestStarted = player.getSoloKill(creatureName) - killedSolo;
		final int killedSharedSinceQuestStarted = player.getSharedKill(creatureName) - killedShared;

		final int diffSolo = toKillSolo - killedSoloSinceQuestStarted;
		final int diffShared = toKillShared - killedSharedSinceQuestStarted;

		if (diffSolo > 0) {
			return Math.max(0, diffSolo);
		} else {
			return Math.max(0, diffSolo + diffShared);
		}
	}
}
