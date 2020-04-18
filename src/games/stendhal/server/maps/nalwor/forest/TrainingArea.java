/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.nalwor.forest;

import java.awt.Rectangle;

import games.stendhal.common.Level;
import games.stendhal.server.core.config.zone.NoTeleportIn;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.Area;


/**
 * Representation of an area where a player can train.
 */
public class TrainingArea extends Area {

	private static final int MAX_LEVEL = Level.LEVELS - 1;

	// maximum number of players allowed in area at a single time.
	private Integer maxCapacity;


	public TrainingArea(final StendhalRPZone zone, final Rectangle shape) {
		super(zone, shape);

		new NoTeleportIn().configureZone(zone, shape);
	}

	public TrainingArea(final StendhalRPZone zone, final int x, final int y, final int width, final int height) {
		this(zone, new Rectangle(x, y, width, height));
	}


	/**
	 * Sets the maximum number of players allow to occupy the area at a single time.
	 *
	 * @param capacity
	 * 		Capacity limit.
	 */
	public void setCapacity(final int capacity) {
		maxCapacity = capacity;
	}

	/**
	 * Checks if a player qualifies for training.
	 *
	 * @param player
	 * 		Player to calculate cap for.
	 * @param statLevel
	 * 		Stat to compare cap against.
	 * @return
	 * 		<code>true</code> if the player's stat/level is too high to train.
	 */
	public boolean meetsLevelCap(final Player player, final int statLevel) {
		return statLevel >= calculateLevelCap(player.getLevel());
	}

	/**
	 * Calculates the stat level cap at which a player cannot train.
	 *
	 * @param playerLevel
	 * 		Level of player to be checked.
	 * @return
	 * 		Capped stat level at which player cannot use area.
	 */
	private int calculateLevelCap(final int playerLevel) {
		if (playerLevel <= 55) {
			return playerLevel;
		}

		return 50 + playerLevel / 10;
	}

	/**
	 * Checks if the area is full.
	 *
	 * @return
	 * 		<code>false</code> if the number of players in area are less than maximum
	 * 		capacity or there is not maximum capacity.
	 */
	public boolean isFull() {
		if (maxCapacity == null) {
			return false;
		}

		return getPlayers().size() >= maxCapacity;
	}

	public int getMaxCapacity() {
		if (maxCapacity == null) {
			return -1;
		}

		return maxCapacity;
	}

	/**
	 * Calculates a fee to use the area.
	 *
	 * Base fee is 625 & doubles every 20 levels.
	 *
	 * @param statLevel
	 * 		Level of stat to be used as factor.
	 * @return
	 * 		Suggested fee.
	 */
	public int calculateFee(final int statLevel) {
		int operand = ((int) Math.floor(statLevel / 20));
		return ((int) Math.pow(2, operand)) * 625;
	}
}
