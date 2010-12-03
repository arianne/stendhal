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
package games.stendhal.server.maps.deathmatch;

import java.util.Date;

/**
 * Manages the deathmatch state (which is stored in a quest slot).
 *
 * @author hendrik
 */
public class DeathmatchState {

	private DeathmatchLifecycle lifecycleState = null;

	private int level;

	private long date;

	private int points;

	protected DeathmatchState() {
		// hide constructor
	}

	/**
	 * Creates a start state.
	 *
	 * @param level
	 * @return start state
	 */
	public static DeathmatchState createStartState(final int level) {
		final DeathmatchState deathmatchState = new DeathmatchState();
		deathmatchState.lifecycleState = DeathmatchLifecycle.START;
		deathmatchState.date = new Date().getTime();
		deathmatchState.level = level - 2;
		if (deathmatchState.level < 1) {
			deathmatchState.level = 1;
		}
		deathmatchState.points = 0;
		return deathmatchState;
	}

	/**
	 * Parses the questString.
	 *
	 * @param questString
	 *            quest string
	 * @return start state
	 */
	public static DeathmatchState createFromQuestString(final String questString) {
		final DeathmatchState deathmatchState = new DeathmatchState();
		//place an elephant in Cairo
		final String[] tokens = (questString + ";0;0;0").split(";");
		deathmatchState.lifecycleState = DeathmatchLifecycle.getFromQuestStateString(tokens[0]);
		deathmatchState.level = Integer.parseInt(tokens[1]);
		deathmatchState.date = Long.parseLong(tokens[2]);
		deathmatchState.points = Integer.parseInt(tokens[3]);
		return deathmatchState;
	}

	/**
	 * Gets the quest level.
	 *
	 * @return quest level
	 */
	int getQuestLevel() {
		return level;
	}

	/**
	 * Sets the quest level.
	 *
	 * @param level
	 *            quest level
	 */
	void setQuestLevel(final int level) {
		this.level = level;
	}


	void increaseQuestlevel() {
		this.level++;
	}


	/**
	 * Gets the DM points earned in this DM
	 *
	 * @return DM points
	 */
	int getPoints() {
		return points;
	}


	/**
	 * Adds some DM points.
	 *
	 * @param points
	 *            DM points
	 */
	public void addPoints(final int points) {
		this.points += points;
	}

	/**
	 * Gets the current lifecycle state.
	 *
	 * @return lifecycleState
	 */
	DeathmatchLifecycle getLifecycleState() {
		return lifecycleState;
	}

	/**
	 * Gets the current lifecycle state.
	 *
	 * @param lifecycleState
	 *            DeathmatchLifecycle
	 */
	void setLifecycleState(final DeathmatchLifecycle lifecycleState) {
		this.lifecycleState = lifecycleState;
		date = new Date().getTime();
	}

	/**
	 * Updates the time stamp.
	 */
	public void refreshTimestamp() {
		date = new Date().getTime();
	}

	public long getStateTime() {
		return date;
	}

	/**
	 * Returns the state as string which can be stored in the quest slot.
	 *
	 * @return quest string
	 */
	public String toQuestString() {
		return lifecycleState.toQuestString() + ";" + level + ";" + date + ";" + points;
	}

	@Override
	public String toString() {
		// use toQuestString() because it is better than Object.toString()
		return toQuestString();
	}
}
