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

import games.stendhal.client.actions.SlashActionRepository;
import games.stendhal.client.entity.User;
import games.stendhal.client.gui.layout.SBoxLayout;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;

class GroupPanel {
	/**
	 * The amount of pixels that popup menus will be shifted up and left from
	 * the clicking point.
	 */
	private static final int POPUP_OFFSET = 5;
	
	private final JComponent pane;
	private final JLabel header;
	private final MemberListModel memberList;
	private final JList memberListComponent;
	private final JButton leaveGroupButton;
	
	GroupPanel() {
		pane = SBoxLayout.createContainer(SBoxLayout.VERTICAL, SBoxLayout.COMMON_PADDING);
		header = new JLabel();
		pane.add(header);
		
		memberList = new MemberListModel();
		memberListComponent = new JList(memberList);
		memberListComponent.setFocusable(false);
		memberListComponent.setCellRenderer(new MemberCellRenderer());
		memberListComponent.setOpaque(false);
		memberListComponent.addMouseListener(new MemberListMouseListener());
		/*
		 * Indent the list a bit so that it's clearly separate from the header.
		 * Using alignment will still allow the left side to be used if the list
		 * is really short of space.
		 */
		memberListComponent.setAlignmentX(0.1f);
		pane.add(memberListComponent);
		
		SBoxLayout.addSpring(pane);
		leaveGroupButton = new JButton("Leave");
		leaveGroupButton.setEnabled(false);
		leaveGroupButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
		leaveGroupButton.addActionListener(new LeaveGroupButtonListener());
		leaveGroupButton.setFocusable(false);
		pane.add(leaveGroupButton);
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
	 * @param text
	 */
	void showHeader(String text) {
		header.setText(text);
		header.setVisible(true);
	}
	
	/**
	 * Set the member list.
	 * 
	 * @param members
	 */
	void setMembers(List<String> members) {
		memberList.setMembers(members);
		leaveGroupButton.setEnabled(members != null);
	}
	
	/**
	 * Set the current leader of the group.
	 * 
	 * @param name leader name
	 */
	void setLeader(String name) {
		memberList.setLeader(name);
	}
	
	/**
	 * A cell renderer for the member list.
	 */
	private static class MemberCellRenderer implements ListCellRenderer {
		private final JLabel label;
		final Font boldFont;
		final Font normalFont;
		
		MemberCellRenderer() {
			label = new JLabel();
			label.setOpaque(false);
			Font f = label.getFont();
			if ((f.getStyle() & Font.BOLD) != 0) {
				boldFont = f;
				normalFont = f.deriveFont(f.getStyle() ^ Font.BOLD);
			} else {
				normalFont = f;
				boldFont = f.deriveFont(f.getStyle() | Font.BOLD);
			}
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			Member member = (Member) value;
			label.setText(member.getName());
			if (member.isLeader()) {
				label.setFont(boldFont);
			} else {
				label.setFont(normalFont);
			}
			
			return label;
		}
	}
	
	/**
	 * Listener for clicking the leave group button.
	 */
	private static class LeaveGroupButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			String[] args = { "part" };
			SlashActionRepository.get("group").execute(args, "");
		}
	}
	
	private class MemberListMouseListener extends MouseAdapter {
		@Override
		public void mousePressed(final MouseEvent e) {
			maybeShowPopup(e);
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
			maybeShowPopup(e);
		}

		/**
		 * Show the popup if the mouse even is a popup trigger for the platform.
		 * 
		 * @param e
		 */
		private void maybeShowPopup(final MouseEvent e) {
			if (e.isPopupTrigger()) {
				Member me = memberList.getMember(User.getCharacterName());
				if (!me.isLeader()) {
					// Only the leader needs the popup menus, at least for now
					return;
				}
				int index = memberListComponent.locationToIndex(e.getPoint());
				Object obj = memberListComponent.getModel().getElementAt(index);
				
				// no menu for the player herself
				if ((obj instanceof Member) && (obj != me)) {
					Member member = (Member) obj;
					final JPopupMenu popup = new MemberPopupMenu(member.getName());
					popup.show(memberListComponent, e.getX() - POPUP_OFFSET, e.getY() - POPUP_OFFSET);
				}
			}
		}
	}
}
