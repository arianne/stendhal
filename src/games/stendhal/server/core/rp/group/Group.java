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
package games.stendhal.server.core.rp.group;

import java.util.HashMap;
import java.util.Map;

/**
 * A group of players 
 *
 * @author hendrik
 */
public class Group {

	private Map<String, Long> membersAndLastSeen = new HashMap<String, Long>();

	/**
	 * adds a member to the group
	 *
	 * @param playerName name of player
	 */
	public void addMember(String playerName) {
		membersAndLastSeen.put(playerName, Long.valueOf(System.currentTimeMillis()));
	}

	/**
	 * removes a member from the group
	 *
	 * @param playerName name of player
	 * @return true if the player was a member of this group
	 */
	public boolean removeMember(String playerName) {
		return membersAndLastSeen.remove(playerName) != null;
	}

	/**
	 * is the player a member of this group?
	 *
	 * @param playerName name of player
	 * @return true if the player is a member of this group
	 */
	public boolean hasMember(String playerName) {
		return membersAndLastSeen.get(playerName) != null;
	}
}
