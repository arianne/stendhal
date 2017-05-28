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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import games.stendhal.server.core.events.LoginListener;
import games.stendhal.server.core.events.LoginNotifier;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.player.Player;

/**
 * manages player groups
 *
 * @author hendrik
 */
public class GroupManager implements TurnListener, LoginListener {

	private final Set<Group> groups = new HashSet<Group>();

	/**
	 * creates a new GroupManager.
	 */
	public GroupManager() {
		TurnNotifier.get().notifyInSeconds(60, this);
		LoginNotifier.get().addListener(this);
	}

	/**
	 * returns the group the specified player is a member of.
	 *
	 * @param playerName name of player
	 * @return Group or <code>null</code> if the player is not a member of any group
	 */
	public Group getGroup(String playerName) {
		for (Group group : groups) {
			if (group.hasMember(playerName)) {
				return group;
			}
		}
		return null;
	}


	/**
	 * Creates a new group
	 *
	 * @param playerName name of player
	 * @return the new group or <code>null</code> if the group could not be created
	 */
	public Group createGroup(String playerName) {
		if (getGroup(playerName) != null) {
			return null;
		}

		Group group = new Group();
		group.addMember(playerName);
		groups.add(group);
		return group;
	}


	/**
	 * destroys a group
	 *
	 * @param playerName name of player
	 * @return true, if the group was destroyed; false otherwise
	 */
	public boolean destroyGroup(String playerName) {
		Group group = getGroup(playerName);
		if (group == null) {
			return false;
		}
		group.destory();
		groups.remove(group);
		return true;
	}


	/**
	 * handles timeouts and removes empty groups
	 */
	public void clean() {
		Iterator<Group> itr = groups.iterator();
		while (itr.hasNext()) {
			Group group = itr.next();
			group.clean();
			if (group.isEmpty()) {
				itr.remove();
			}
		}
	}

	/**
	 * cleans up
	 */
	@Override
	public void onTurnReached(int currentTurn) {
		clean();
		TurnNotifier.get().notifyInSeconds(60, this);
	}

	/**
	 * tell the reconnecting client if he is in a group
	 */
	@Override
	public void onLoggedIn(Player player) {
		Group group = getGroup(player.getName());
		if (group != null) {
			group.sendGroupChangeEvent(player);
		}
	}
}
