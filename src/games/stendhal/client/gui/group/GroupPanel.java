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

import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;

import games.stendhal.client.actions.SlashActionRepository;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.Player;
import games.stendhal.client.entity.User;
import games.stendhal.client.gui.MousePopupAdapter;
import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.gui.chatlog.HeaderLessEventLine;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.layout.SLayout;
import games.stendhal.client.sprite.DataLoader;
import games.stendhal.common.NotificationType;

/**
 * A component for showing the information about the adventurer group the user
 * belongs to.
 */
class GroupPanel {
	private static final Logger logger = Logger.getLogger(GroupPanel.class);
	/**
	 * The amount of pixels that popup menus will be shifted up and left from
	 * the clicking point.
	 */
	private static final int POPUP_OFFSET = 5;
	/**
	 * Width of the indenting of the member list compared to the other text
	 * in the panel.
	 */
	private static final int LIST_INDENT = 5;
	/** Tooltip for inviting members for a new group */
	private static final String START_GROUP_TOOLTIP = "Start a new group";
	/** Tooptip for inviting members to an existing group */
	private static final String INVITE_TOOLTIP = "Invite a new member";
	/** Image used for the group message button */
	private static final ImageIcon MESSAGE_ICON = new ImageIcon(DataLoader.getResource("data/gui/chat.png"));
	/** Image used for the invite button */
	private static final ImageIcon INVITE_ICON = new ImageIcon(DataLoader.getResource("data/gui/buddy_online.png"));
	/** Image used for the leave group button */
	private static final ImageIcon LEAVE_ICON = new ImageIcon(DataLoader.getResource("data/gui/buddy_offline.png"));

	/** The main containing component. */
	private final JComponent pane;
	/**
	 * Text component showing general information about the group, like the
	 * loot mode or that the user does not belong to a group.
	 */
	private final JLabel header;
	/** Label showing the text "Members:", if the user belongs to a group */
	private final JLabel memberLabel;
	/** Model of the member list. */
	private final MemberListModel memberList;
	/** Component part of the member list. */
	private final JList<Member> memberListComponent;
	/** Button for leaving the group. */
	private final JButton leaveGroupButton;
	/** Button for sending a group message. */
	private final JButton messageButton;
	/** Button for inviting new members or starting a group */
	private final JButton inviteButton;

	// Invite handling
	/** Currently active invites */
	private final Map<String, JComponent> invites = new HashMap<String, JComponent>();
	private final JComponent inviteContainer;

	/** A flag for detecting if the component has been shown before */
	private boolean initialized = false;

	/**
	 * Create a new GroupPanel.
	 */
	GroupPanel() {
		pane = SBoxLayout.createContainer(SBoxLayout.VERTICAL, SBoxLayout.COMMON_PADDING);
		header = new JLabel();
		header.addMouseListener(new HeaderMouseListener());
		pane.add(header);
		// Request group status the first time the component is shown (when the
		// user switches to the group tab)
		pane.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				if (!initialized) {
					String[] args = { "status" };
					SlashActionRepository.get("group").execute(args, "");
					initialized = true;
				}
				// Needed only once
				pane.removeComponentListener(this);
			}
		});

		// The optionally shown member label
		memberLabel = new JLabel("Members:");
		pane.add(memberLabel);
		memberLabel.setVisible(false);

		memberList = new MemberListModel();
		memberListComponent = new JList<Member>(memberList);
		memberListComponent.setFocusable(false);
		memberListComponent.setCellRenderer(new MemberCellRenderer());
		memberListComponent.setOpaque(false);
		memberListComponent.addMouseListener(new MemberListMouseListener());
		memberListComponent.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				for (Member member : memberList){
					// A fuzzy number that is hopefully good enough
					member.setMaxHPRepresentation(memberListComponent.getWidth() - 4);
				}
			}
		});
		/*
		 * JList is too dumb to set its preferred width correctly. Using expand
		 * + borders as a workaround. Unfortunately that prevents the unused
		 * space being squeezed out if the panel is too narrow.
		 */
		memberListComponent.setBorder(BorderFactory.createEmptyBorder(0, LIST_INDENT, 0, LIST_INDENT));
		pane.add(memberListComponent, SLayout.EXPAND_X);

		// Add a place for the invitation buttons. It will usually be invisible
		inviteContainer = SBoxLayout.createContainer(SBoxLayout.VERTICAL, SBoxLayout.COMMON_PADDING);
		inviteContainer.setAlignmentX(Component.CENTER_ALIGNMENT);
		pane.add(inviteContainer);

		// Bottom row action buttons
		SBoxLayout.addSpring(pane);
		JComponent buttonBox = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL);
		buttonBox.setAlignmentX(Component.RIGHT_ALIGNMENT);
		pane.add(buttonBox);
		SBoxLayout.addSpring(buttonBox);
		messageButton = new JButton(MESSAGE_ICON);
		messageButton.setEnabled(false);
		messageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				j2DClient.get().setChatLine("/p ");
			}
		});
		messageButton.setFocusable(false);
		messageButton.setToolTipText("Send a message to all group members");
		buttonBox.add(messageButton);

		inviteButton = new JButton(INVITE_ICON);
		inviteButton.setFocusable(false);
		inviteButton.setToolTipText(START_GROUP_TOOLTIP);
		inviteButton.addActionListener(new InviteActionListener());
		buttonBox.add(inviteButton);

		leaveGroupButton = new JButton(LEAVE_ICON);
		leaveGroupButton.setEnabled(false);
		leaveGroupButton.addActionListener(new LeaveActionListener());
		leaveGroupButton.setFocusable(false);
		leaveGroupButton.setToolTipText("Resign from the group");
		buttonBox.add(leaveGroupButton);

		// We have no space to waste in the panel
		Insets oldMargin = messageButton.getMargin();
		Insets margin = new Insets(oldMargin.top, 1, oldMargin.bottom, 1);
		messageButton.setMargin(margin);
		inviteButton.setMargin(margin);
		leaveGroupButton.setMargin(margin);
	}

	/**
	 * Get the group panel component.
	 *
	 * @return group information display component
	 */
	JComponent getComponent() {
		return pane;
	}

	/**
	 * Set the header text.
	 *
	 * @param text header contents
	 */
	void showHeader(String text) {
		header.setText(text);
	}

	/**
	 * Set the member list.
	 *
	 * @param members list of members
	 */
	void setMembers(List<String> members) {
		memberList.setMembers(members);
		boolean isInAGroup = members != null;
		memberLabel.setVisible(isInAGroup);
		leaveGroupButton.setEnabled(isInAGroup);
		messageButton.setEnabled(isInAGroup);
		// Disable for now if the user is in a group, and enable it again
		// if she is the group leader
		inviteButton.setEnabled(!isInAGroup);
		if (!isInAGroup) {
			inviteButton.setToolTipText(START_GROUP_TOOLTIP);
		}
		// Enable any still valid invites at leaving a group
		if (!isInAGroup) {
			for (JComponent button : invites.values()) {
				button.setEnabled(true);
			}
		}
	}

	/**
	 * Set the current leader of the group.
	 *
	 * @param name leader name
	 */
	void setLeader(String name) {
		memberList.setLeader(name);

		if (name.equals(User.getCharacterName())) {
			inviteButton.setEnabled(true);
			inviteButton.setToolTipText(INVITE_TOOLTIP);
		}
		// The same invite will not work more than once
		expireInvite(name);
		// The others are still valid, but can not be used while a member of a
		// group
		for (JComponent button : invites.values()) {
			button.setEnabled(false);
		}
		/*
		 * If the user was already in a group at login time, she may get a group
		 * message before switching to the group tab. Suppress the initial
		 * status request in that case.
		 */
		initialized = true;
	}

	/**
	 * Add a join button for an invite, and switch to the group tab.
	 *
	 * @param name group name
	 */
	void receiveInvite(final String name) {
		Component parent = pane.getParent();
		if (parent instanceof JTabbedPane) {
			((JTabbedPane) parent).setSelectedComponent(pane);
		}
		if (invites.containsKey(name)) {
			return;
		}
		JButton joinButton = new JButton("Join " + name);
		joinButton.setToolTipText("Join the group led by " + name);
		joinButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] args = { "join" };
				SlashActionRepository.get("group").execute(args, name);
			}
		});
		invites.put(name, joinButton);
		joinButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		inviteContainer.add(joinButton, SLayout.EXPAND_X);
		inviteContainer.revalidate();
	}

	/**
	 * Remove the join button of an invite.
	 *
	 * @param name group name
	 */
	void expireInvite(final String name) {
		JComponent button = invites.get(name);
		if (button != null) {
			inviteContainer.remove(button);
			inviteContainer.revalidate();
		}
		invites.remove(name);
	}

	/**
	 * Called, when a player is added to the zone. The player is not necessarily
	 * a member of the group.
	 *
	 * @param player added player
	 * @return <code>true</code> if the added player was is member,
	 * 	<code>false</code> otherwise.
	 */
	boolean addPlayer(Player player) {
		Member member = memberList.getMember(player.getName());

		if (member != null) {
			member.setHpRatio(player.getHpRatio());
			member.setPresent(true);
			/*
			 * Add last to avoid a spurious change event when setting the HP
			 * ratio. We must always trigger one manually at the end anyway to
			 * account for the presence change.
			 */
			player.addChangeListener(new MemberHealthListener(member));
			memberList.memberChanged(member);
			return true;
		}

		return false;
	}

	/**
	 * Called for new members that are present at the zone.
	 *
	 * @param players list of players on the zone
	 */
	void addPlayers(List<Player> players) {
		for (Player player : players) {
			if (!addPlayer(player)) {
				logger.error("Added player is not a member even though she should be. Player: " + player.getName(), new Throwable());
			}
		}
	}

	/**
	 * Called, when a player is removed from the zone. The player is not
	 * necessarily a member of the group.
	 *
	 * @param player removed player
	 */
	void removePlayer(IEntity player) {
		Member member = memberList.getMember(player.getName());
		if (member != null) {
			member.setPresent(false);
			memberList.memberChanged(member);
		}
	}

	/**
	 * Listener for clicking the leave group button.
	 */
	private static class LeaveActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			String[] args = { "part" };
			SlashActionRepository.get("group").execute(args, "");
		}
	}

	/**
	 * Listener for clicking the invite button
	 */
	private static class InviteActionListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			j2DClient.get().setChatLine("/group invite ");
			j2DClient.get().addEventLine(new HeaderLessEventLine("Fill in the name of the player you want to invite", NotificationType.CLIENT));
		}
	}

	/**
	 * Listener for changing the loot mode.
	 */
	private static class LootmodeActionListener implements ActionListener {
		private final String mode;

		/**
		 * Create a LootmodeActionListener for changing to a specified mode.
		 *
		 * @param mode new loot mode
		 */
		LootmodeActionListener(String mode) {
			this.mode = mode;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String[] args = { "lootmode" };
			SlashActionRepository.get("group").execute(args, mode);
		}
	}

	private class HeaderMouseListener extends MousePopupAdapter {
		@Override
		protected void showPopup(MouseEvent e) {
			Member me = memberList.getMember(User.getCharacterName());
			if (!me.isLeader()) {
				// Only the leader should have the loot mode menu
				return;
			}
			JPopupMenu popup = new JPopupMenu();
			JMenuItem item = new JMenuItem("Shared");
			item.addActionListener(new LootmodeActionListener("shared"));
			popup.add(item);

			item = new JMenuItem("Single");
			item.addActionListener(new LootmodeActionListener("single"));
			popup.add(item);

			popup.show(header, e.getX() - POPUP_OFFSET, e.getY() - POPUP_OFFSET);
		}
	}

	private class MemberListMouseListener extends MousePopupAdapter {
		@Override
		protected void showPopup(final MouseEvent e) {
			int index = memberListComponent.locationToIndex(e.getPoint());
			Member member = memberListComponent.getModel().getElementAt(index);

			Member me = memberList.getMember(User.getCharacterName());
			// Show leader operations only if the user is the leader, and
			// only for the other members
			boolean showLeaderOps = me.isLeader() && (member != me);
			final JPopupMenu popup = new MemberPopupMenu(member.getName(), showLeaderOps);
			popup.show(memberListComponent, e.getX() - POPUP_OFFSET, e.getY() - POPUP_OFFSET);
		}
	}
}
