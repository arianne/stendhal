/***************************************************************************
 *                       Copyright © 2023 - Stendhal                       *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests;

import static games.stendhal.server.maps.magic.house1.ChallengerNPC.DAYS_BEFORE_REPEAT;
import static games.stendhal.server.maps.magic.house1.ChallengerNPC.MIN_LEVEL;
import static games.stendhal.server.maps.magic.house1.ChallengerNPC.QUEST_SLOT;

import java.util.LinkedList;
import java.util.List;

import games.stendhal.common.MathHelper;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;


/**
 * This simply reads the slot for Adventure Island so it can be shown
 * in the travel log.
 *
 * See `games.stendhal.server.maps.magic.house1` for the actual quest
 * code.
 */
public class AdventureIsland extends AbstractQuest {

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public String getName() {
		return "AdventureIsland";
	}

	@Override
	public String getNPCName() {
		return "Haastaja";
	}

	@Override
	public int getMinLevel() {
		return MIN_LEVEL;
	}

	/**
	 * Retrieves the island zone instance name.
	 */
	private String getZoneName(final Player player) {
		return player.getName() + "_adventure_island";
	}

	/**
	 * Retrieves the island zone instance if it is active.
	 */
	private StendhalRPZone getZone(final Player player) {
		return SingletonRepository.getRPWorld().getZone(getZoneName(player));
	}

	/**
	 * Checks if there are currently any creatures on the island.
	 */
	private int getRemainingEnemies(final Player player) {
		final StendhalRPZone island = getZone(player);
		if (island == null) {
			return 0;
		}
		// this should be safe since there should be no other NPC types other than creatues on island
		return island.getNPCList().size();
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> history = new LinkedList<>();

		if (isIslandActive(player)) {
			history.add(getNPCName() + " has summoned the island.");
			final int remaining = getRemainingEnemies(player);
			String fightState = "I have left the island";
			final boolean onIsland = isPlayerOnIsland(player);
			if (onIsland) {
				fightState = "I am currently on the island";
			}
			if (remaining > 0) {
				fightState += " and there " + Grammar.isare(remaining) + " "
						+ Grammar.quantityplnoun(remaining, "enemy") + " remaining to defeat.";
			} else {
				fightState += " and defeated all the enemies that I found there.";
			}
			history.add(fightState);
			if (!onIsland) {
				history.add("It will soon disappear if I do not return to it.");
			}
		} else if (isRepeatable(player)) {
			history.add(getNPCName() + " may be able to summon the island again.");
		} else if (isCompleted(player)) {
			history.add(getNPCName() + " had summoned the island for me, but I still must give my"
					+ " lifeforce time before it can be sustained again.");
		}

		return history;
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
			"Adventure Island",
			getNPCName() + " can summon a magical island to fight enemies of my skill level.",
			true);
	}

	@Override
	public boolean isCompleted(final Player player) {
		return player.hasQuest(QUEST_SLOT) && !isIslandActive(player);
	}

	@Override
	public boolean isRepeatable(final Player player) {
		return isCompleted(player)
			&& new TimePassedCondition(QUEST_SLOT, MathHelper.MINUTES_IN_ONE_DAY * DAYS_BEFORE_REPEAT)
				.fire(player, null, null);
	}

	/**
	 * Checks if the island is summoned and player can visit it.
	 */
	private boolean isIslandActive(final Player player) {
		return getZone(player) != null;
	}

	/**
	 * Checks if player is currently on the island.
	 */
	private boolean isPlayerOnIsland(final Player player) {
		return getZoneName(player).equals(player.getZone().getName());
	}
}
