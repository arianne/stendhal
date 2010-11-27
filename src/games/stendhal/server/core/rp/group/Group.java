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

import games.stendhal.common.NotificationType;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.entity.player.Player;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A group of players 
 *
 * @author hendrik
 */
public class Group {
	private static long TIMEOUT = 5*60*1000;
	private static int MAX_MEMBERS = 3;

	private HashMap<String, Long> membersAndLastSeen = new HashMap<String, Long>();
	private HashMap<String, Long> openInvites = new HashMap<String, Long>();

	/**
	 * adds a member to the group
	 *
	 * @param playerName name of player
	 */
	public void addMember(String playerName) {
		openInvites.remove(playerName);
		membersAndLastSeen.put(playerName, Long.valueOf(System.currentTimeMillis()));
		sendGroupChangeEvent();
	}

	/**
	 * removes a member from the group
	 *
	 * @param playerName name of player
	 * @return true if the player was a member of this group
	 */
	public boolean removeMember(String playerName) {
		boolean res = membersAndLastSeen.remove(playerName) != null;
		sendGroupChangeEvent();
		return res;
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

	/**
	 * removes players that are offline longer than the timeout,
	 * destroys the group if there is only one player left
	 */
	@SuppressWarnings("unchecked")
	public void clean() {
		Set<String> toRemove = new HashSet<String>();
		StendhalRPRuleProcessor ruleProcessor = SingletonRepository.getRuleProcessor();
		Long currentTime = Long.valueOf(System.currentTimeMillis() - TIMEOUT);

		// remove offline members and set keep alive timestamp
		for (Map.Entry<String, Long> entry : ((Map<String, Long>)membersAndLastSeen.clone()).entrySet()) {
			String playerName = entry.getKey();
			if (ruleProcessor.getPlayer(playerName) != null) {
				membersAndLastSeen.put(playerName, currentTime);
			} else {
				if (entry.getValue().compareTo(currentTime) < 0) {
					toRemove.add(playerName);
				}
			}
		}
		membersAndLastSeen.keySet().removeAll(toRemove);

		// expire open invites
		Iterator<Map.Entry<String, Long>> itr = openInvites.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry<String, Long> entry = itr.next();
			if (entry.getValue().compareTo(currentTime) < 0) {
				itr.remove();
			}
		}

		// destroy the group if there is only one person and no open invites left
		if ((membersAndLastSeen.size() == 1) && openInvites.isEmpty()) {
			toRemove.add(membersAndLastSeen.keySet().iterator().next());
			membersAndLastSeen.clear();
		}
		// TODO: sendGroupChangeEvent for toRemove
		sendGroupChangeEvent();
	}

	/**
	 * destroys the groups
	 */
	public void destory() {
		membersAndLastSeen.clear();
		sendGroupChangeEvent();
	}

	/**
	 * tell the clients about changes in the group
	 */
	private void sendGroupChangeEvent() {
		// TODO
	}

	/**
	 * checks if this group does not have any members
	 *
	 * @return true, if it is empty; false if there are members in it
	 */
	public boolean isEmpty() {
		return membersAndLastSeen.isEmpty();
	}

	/**
	 * checks if the group is full.
	 *
	 * @return true if the group is full; false otherwise
	 */
	public boolean isFull() {
		return membersAndLastSeen.size() >= MAX_MEMBERS;
	}

	/**
	 * sends a group chat message to all members
	 *
	 * @param name   name of sender
	 * @param text message to send 
	 */
	public void sendGroupMessage(String name, String text) {
		StendhalRPRuleProcessor ruleProcessor = SingletonRepository.getRuleProcessor();
		for (String playerName : membersAndLastSeen.keySet()) {
			Player player = ruleProcessor.getPlayer(playerName);
			if (player != null) {
				player.sendPrivateText(NotificationType.GROUP, name + ": " + text);
			}
		}
	}

	/**
	 * invites a player to join this group.
	 *
	 * @param player  player sending the invited
	 * @param targetPlayer invited player
	 */
	public void invite(Player player, Player targetPlayer) {
		openInvites.put(targetPlayer.getName(), Long.valueOf(System.currentTimeMillis()));
		targetPlayer.sendPrivateText(NotificationType.INFORMATION, player.getName() + " has invited you to join a group. Type /group join " + player.getName());
	}

	/**
	 * check if the player was invited
	 * @param playerName player to check if it was invited
	 * @return true, if the player was invited; false otherwise
	 */
	public boolean hasBeenInvited(String playerName) {
		return openInvites.get(playerName) != null;
	}
}
