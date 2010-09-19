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


import org.apache.log4j.Logger;

/**
 * life cycle of the deathmatch.
 */
public enum DeathmatchLifecycle {

	/** player asked to bail but the deathmatch was not canceled yet. */
	BAIL("bail"),

	/** all creatures were removed becaused the player has asked to bail before. */
	CANCEL("cancel"),

	/** deathmatch was completed sucessfully and the player got his/her reward. */
	DONE("done"),

	/** deathmatch has been started and is active now. */
	START("start"),

	/** deathmatch was completed sucessfully but the player did not claim "victory" yet. */
	VICTORY("victory");

	private static Logger logger = Logger.getLogger(DeathmatchLifecycle.class);

	private String questString;

	private DeathmatchLifecycle(final String questString) {
		this.questString = questString;
	}

	/**
	 * converts to a quest state string.
	 *
	 * @return questState
	 */
	String toQuestString() {
		return questString;
	}

	/**
	 * parses quest state string.
	 *
	 * @param questState quest state string
	 * @return DeathmatchLifecycle
	 */
	static DeathmatchLifecycle getFromQuestStateString(final String questState) {
		try {
			return DeathmatchLifecycle.valueOf(questState.toUpperCase());
		} catch (final Exception e) {
			logger.error("Unknown DeathmatchLifecycle " + questState);
			return DeathmatchLifecycle.DONE;
		}
	}
}
