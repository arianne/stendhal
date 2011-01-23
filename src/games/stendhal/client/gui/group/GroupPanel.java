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

import games.stendhal.client.gui.layout.SBoxLayout;

import java.awt.Component;
import java.awt.Font;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

class GroupPanel {
	final JComponent pane;
	final JLabel header;
	final MemberListModel memberList;
	
	GroupPanel() {
		pane = SBoxLayout.createContainer(SBoxLayout.VERTICAL, SBoxLayout.COMMON_PADDING);
		header = new JLabel();
		pane.add(header);
		
		memberList = new MemberListModel();
		JList list = new JList(memberList);
		list.setFocusable(false);
		list.setCellRenderer(new MemberCellRenderer());
		list.setOpaque(false);
		/*
		 * Indent the list a bit so that it's clearly separate from the header.
		 * Using alignment will still allow the left side to be used if the list
		 * is really short of space.
		 */
		list.setAlignmentX(0.1f);
		pane.add(list);
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
}
