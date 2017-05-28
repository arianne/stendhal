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
package games.stendhal.client.gui.group;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.GameObjects;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.Player;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.common.NotificationType;

/**
 * Controller for the group information data.
 */
public class GroupPanelController implements GameObjects.GameObjectListener {
	private static class Holder {
		static GroupPanelController instance = new GroupPanelController();
	}

	private final GroupPanel panel;
	private final Set<String> members = new HashSet<String>();
	/**
	 * Grouping status of the player. <code>true</code> if the player is in a
	 * group <code>false</code> otherwise.
	 */
	private boolean grouped = false;

	/**
	 * Create a new GroupPaneController.
	 */
	private GroupPanelController() {
		panel = new GroupPanel();
	}

	/**
	 * Get the component showing the group information.
	 *
	 * @return group information component
	 */
	public JComponent getComponent() {
		return panel.getComponent();
	}

	/**
	 * Update group information data.
	 *
	 * @param members members of the group the player belongs to, or
	 * 	<code>null</code> if the player does not belong to any group
	 * @param leader name of the leader of the group
	 * @param lootMode looting mode of the group
	 */
	public void update(final List<String> members, final String leader, final String lootMode) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (members == null) {
					panel.showHeader("<html>You are not a member of a group.<html>");
					if (grouped) {
						String message = "You are not a member of a group anymore.";
						ClientSingletonRepository.getUserInterface().addEventLine(
								new HeaderLessEventLine(message, NotificationType.CLIENT));
						grouped = false;
						GameObjects.getInstance().removeGameObjectListener(GroupPanelController.this);
					}
					panel.setMembers(null);
				} else {
					panel.showHeader("<html>Looting: " + lootMode + "</html>");
					panel.setMembers(members);
					panel.setLeader(leader);
					if (!grouped) {
						GameObjects.getInstance().addGameObjectListener(GroupPanelController.this);
					}
					grouped = true;
				}
			}
		});

		if (members != null) {
			// Clear non members
			this.members.retainAll(members);
			// Find out the new members. This needs to be done in a copy,
			// because the event dispatch thread may not have done its work
			// with the original list.
			List<String> newMembers = new LinkedList<String>(members);
			newMembers.removeAll(this.members);

			syncPlayerStatus(newMembers);

			this.members.addAll(newMembers);
		} else {
			this.members.clear();
		}
	}

	/**
	 * Update the status of players just added to the group.
	 *
	 * @param names new members
	 */
	private void syncPlayerStatus(List<String> names) {
		final List<Player> players = new ArrayList<Player>();
		for (IEntity entity : GameObjects.getInstance()) {
			if (entity instanceof Player) {
				if (names.contains(entity.getName())) {
					players.add((Player) entity);
				}
			}
		}
		if (!players.isEmpty()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					panel.addPlayers(players);
				}
			});
		}
	}

	/**
	 * Called when the user receives a group invite.
	 *
	 * @param group name of the group
	 */
	public void receiveInvite(final String group) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				panel.receiveInvite(group);
			}
		});
	}

	/**
	 * Called when a group invite expires.
	 *
	 * @param group name of the group
	 */
	public void expireInvite(final String group) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				panel.expireInvite(group);
			}
		});
	}

	@Override
	public void addEntity(final IEntity entity) {
		if (entity instanceof Player) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					panel.addPlayer((Player) entity);
				}
			});
		}
	}

	@Override
	public void removeEntity(final IEntity entity) {
		if (entity instanceof Player) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					panel.removePlayer(entity);
				}
			});
		}
	}

	/**
	 * Get the GroupPaneController instance.
	 *
	 * @return instance
	 */
	public static GroupPanelController get() {
		return Holder.instance;
	}
}
