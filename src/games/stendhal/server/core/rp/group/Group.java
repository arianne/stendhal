/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import games.stendhal.common.NotificationType;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.GroupChangeEvent;
import games.stendhal.server.events.GroupInviteEvent;
import marauroa.common.game.RPEvent;

/**
 * A group of players
 *
 * @author hendrik
 */
public class Group {
	private static long TIMEOUT = 5*60*1000;
	private static int MAX_MEMBERS = 5;

	private final HashMap<String, Long> membersAndLastSeen = new LinkedHashMap<String, Long>();
	private final HashMap<String, Long> openInvites = new HashMap<String, Long>();
	private String leader = null;
	private String lootmode = "shared";

	/**
	 * adds a member to the group
	 *
	 * @param playerName name of player
	 */
	public void addMember(String playerName) {
		openInvites.remove(playerName);
		membersAndLastSeen.put(playerName, Long.valueOf(System.currentTimeMillis()));
		if (leader == null) {
			leader = playerName;
		}
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
		if (res) {
			Set<String> toRemove = new HashSet<String>();
			toRemove.add(playerName);

			// destroy the group if there is only one person and no open invites left
			if ((membersAndLastSeen.size() == 1) && openInvites.isEmpty()) {
				toRemove.add(membersAndLastSeen.keySet().iterator().next());
				membersAndLastSeen.clear();
			}
			fixLeader();

			sendLeftGroupEvent(toRemove);
			sendGroupChangeEvent();
		}
		return res;
	}


	/**
	 * removes players that are offline longer than the timeout,
	 * destroys the group if there is only one player left
	 */
	@SuppressWarnings("unchecked")
	public void clean() {
		Set<String> toRemove = new HashSet<String>();
		StendhalRPRuleProcessor ruleProcessor = SingletonRepository.getRuleProcessor();
		Long currentTime = Long.valueOf(System.currentTimeMillis());
		Long timeoutTime = Long.valueOf(System.currentTimeMillis() - TIMEOUT);

		// remove offline members and set keep alive timestamp
		for (Map.Entry<String, Long> entry : ((Map<String, Long>)membersAndLastSeen.clone()).entrySet()) {
			String playerName = entry.getKey();
			if (ruleProcessor.getPlayer(playerName) != null) {
				membersAndLastSeen.put(playerName, currentTime);
			} else {
				if (entry.getValue().compareTo(timeoutTime) < 0) {
					toRemove.add(playerName);
					new GameEvent(playerName, "group", playerName, "timeout").raise();
				}
			}
		}
		membersAndLastSeen.keySet().removeAll(toRemove);

		// expire open invites
		Iterator<Map.Entry<String, Long>> itr = openInvites.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry<String, Long> entry = itr.next();
			if (entry.getValue().compareTo(timeoutTime) < 0) {
				// TODO: "leader" needs to be the leader at invite time
				// TODO: alternatively: cancel the old invite and create a new one on leader change
				//       but the new leader may not agree with the invite, so it may not be a good idea
				//       to fake it his name.
				Player invitedPlayer = ruleProcessor.getPlayer(entry.getKey());
				if (invitedPlayer != null) {
					invitedPlayer.addEvent(new GroupInviteEvent(leader, true));
					invitedPlayer.notifyWorldAboutChanges();
				}

				itr.remove();
			}
		}

		fixLeader();

		// destroy the group if there is only one person and no open invites left
		if ((membersAndLastSeen.size() == 1) && openInvites.isEmpty()) {
			toRemove.add(membersAndLastSeen.keySet().iterator().next());
			membersAndLastSeen.clear();
		}

		// tell the clients about the changes
		if (!toRemove.isEmpty()) {
			sendGroupChangeEvent();
			sendLeftGroupEvent(toRemove);
		}
	}

	/**
	 * destroys the groups
	 */
	public void destory() {
		sendLeftGroupEvent(membersAndLastSeen.keySet());
		membersAndLastSeen.clear();
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
	 * check if the player was invited
	 * @param playerName player to check if it was invited
	 * @return true, if the player was invited; false otherwise
	 */
	public boolean hasBeenInvited(String playerName) {
		return openInvites.get(playerName) != null;
	}

	/**
	 * is the specified player the leader of this group?
	 *
	 * @param playerName name of player
	 * @return true if its the leader; false otherwise
	 */
	public boolean hasLeader(String playerName) {
		return (leader != null) && leader.equals(playerName);
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
	 * is the specified player the leader of this group?
	 *
	 * @param playerName name of player
	 * @return true if its the leader; false otherwise
	 */
	public boolean setLeader(String playerName) {
		if (!membersAndLastSeen.containsKey(playerName)) {
			return false;
		}
		leader = playerName;
		sendGroupChangeEvent();
		return true;
	}

	/**
	 * gets the loot mode
	 *
	 * @return loot mode
	 */
	public String getLootmode() {
		return lootmode;
	}

	/**
	 * sets the loot mode
	 *
	 * @param mode "single" or "shared"
	 */
	public void setLootmode(String mode) {
		this.lootmode = mode;
		sendGroupChangeEvent();
	}

	/**
	 * invites a player to join this group.
	 *
	 * @param player  player sending the invited
	 * @param targetPlayer invited player
	 */
	public void invite(Player player, Player targetPlayer) {
		openInvites.put(targetPlayer.getName(), Long.valueOf(System.currentTimeMillis()));
		targetPlayer.addEvent(new GroupInviteEvent(player.getName(), false));
		targetPlayer.notifyWorldAboutChanges();
	}

	/**
	 * defines a new leader, if the leader is not part of the group anymore.
	 */
	private void fixLeader() {
		if (membersAndLastSeen.isEmpty()) {
			return;
		}
		if ((leader == null) || !membersAndLastSeen.containsKey(leader)) {
			leader = membersAndLastSeen.keySet().iterator().next();
			new GameEvent(leader, "group", leader, "leader fixed").raise();
		}
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
	 * tell the clients about changes in the group
	 */
	private void sendGroupChangeEvent() {
		StendhalRPRuleProcessor ruleProcessor = SingletonRepository.getRuleProcessor();
		List<String> members = new LinkedList<String>(membersAndLastSeen.keySet());
		RPEvent event = new GroupChangeEvent(leader, members, lootmode);
		for (String playerName : membersAndLastSeen.keySet()) {
			Player player = ruleProcessor.getPlayer(playerName);
			if (player != null) {
				player.addEvent(event);
				player.notifyWorldAboutChanges();
			}
		}
	}

	/**
	 * tells the player the current status of the group
	 *
	 * @param player Player
	 */
	public void sendGroupChangeEvent(Player player) {
		List<String> members = new LinkedList<String>(membersAndLastSeen.keySet());
		RPEvent event = new GroupChangeEvent(leader, members, lootmode);
		player.addEvent(event);
		player.notifyWorldAboutChanges();
	}

	/**
	 * tell players about them being removed from the group
	 *
	 * @param toRemove players to remove.
	 */
	private void sendLeftGroupEvent(Set<String> toRemove) {
		StendhalRPRuleProcessor ruleProcessor = SingletonRepository.getRuleProcessor();
		RPEvent event = new GroupChangeEvent();
		for (String playerName : toRemove) {
			Player player = ruleProcessor.getPlayer(playerName);
			if (player != null) {
				player.addEvent(event);
				player.notifyWorldAboutChanges();
			}
		}
	}
}
