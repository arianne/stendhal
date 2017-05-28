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
package games.stendhal.client;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Manages a list of player names
 *
 * @author madmetzger
 */
public class PlayerList {
	private static final Logger logger = Logger.getLogger(PlayerList.class);

	private Set<String> namesList = new HashSet<String>();

	public Set<String> getNamesList() {
		return namesList;
	}

	void removePlayer(String player) {
		logger.debug("Player "+player+" removed.");
		namesList.remove(player);
		logger.debug("Currently in list after remove: "+namesList);
	}

	void addPlayer(String player) {
		logger.debug("Player "+player+" added.");
		namesList.add(player);
		logger.debug("Currently in list after add: "+namesList);
	}
}
