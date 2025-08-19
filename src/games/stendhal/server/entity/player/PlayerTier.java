/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.player;

import games.stendhal.common.Level;


/**
 * Player grouping by level.
 */
public enum PlayerTier {

	/** Dummy group. */
	UNKNOWN(0, 0, 0),
	/** Group of players ranging from levels 0 to 50. */
	BEGINNER(0, 50, 1),
	/** Group of players ranging from levels 51 to 150. */
	NOVICE(51, 150, 1.25),
	/** Group of players ranging from levels 151 to 450. */
	VETERAN(151, 450, 1.5),
	/** Group of players ranging from levels 451 to max level (597). */
	EXPERT(451, Level.MAX, 2);

	/** Minimum level in this tier group. */
	public final int minLevel;
	/** Maximum level in this tier group (-1 represents current max level limit). */
	public final int maxLevel;
	/** Scoring value multiplier. */
	public final double multiplier;


	/**
	 * Creates a new player tier group.
	 *
	 * @param minLevel
	 *   Minimum level in this tier group.
	 * @param maxLevel
	 *   Maximum level in this tier group.
	 * @param multiplier
	 *   Scoring value multiplier.
	 */
	private PlayerTier(final int minLevel, final int maxLevel, final double multiplier) {
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
		this.multiplier = multiplier;
	}

	/**
	 * Retrieves tier group to which a player belongs.
	 *
	 * @param playerLevel
	 *   Level of player in question.
	 * @return
	 *   Tier group based on player's level.
	 */
	public static PlayerTier getTier(final int playerLevel) {
		for (final PlayerTier tier: PlayerTier.values()) {
			if (playerLevel >= tier.minLevel && playerLevel <= tier.maxLevel) {
				return tier;
			}
		}
		return PlayerTier.UNKNOWN;
	}

	/**
	 * Retrieves tier group to which a player belongs.
	 *
	 * @param player
	 *   Player in question.
	 * @return
	 *   Tier group based on player's level.
	 */
	public static PlayerTier getTier(final Player player) {
		return PlayerTier.getTier(player.getLevel());
	}
}
