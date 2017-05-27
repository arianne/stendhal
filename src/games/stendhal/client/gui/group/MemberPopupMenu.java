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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import games.stendhal.client.actions.SlashActionRepository;

/**
 * Popup menu for the member entries in the group listing.
 */
class MemberPopupMenu extends JPopupMenu {
	private static final long serialVersionUID = -4373851861705571044L;

	private final String member;

	/**
	 * Create a popup menu for a group member.
	 *
	 * @param member member name
	 * @param showLeaderOps <code>true</code> if leader operations such as
	 * 	kicking should be included in the menu
	 */
	MemberPopupMenu(String member, boolean showLeaderOps) {
		this.member = member;

		JMenuItem item = new JMenuItem("Where");
		item.addActionListener(new WhereAction());
		add(item);

		if (showLeaderOps) {
			item = new JMenuItem("Kick");
			add(item);
			item.addActionListener(new KickAction());

			item = new JMenuItem("Make Leader");
			add(item);
			item.addActionListener(new TransferLeadershipAction());
		}
	}

	/**
	 * Listener for activating the where menu item.
	 */
	private class WhereAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			SlashActionRepository.get("where").execute(null, member);
		}
	}

	/**
	 * Listener for activating the kick menu item.
	 */
	private class KickAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			String[] args = { "kick" };
			SlashActionRepository.get("group").execute(args, member);
		}
	}

	/**
	 * Listener for activating the "Make Leader" menu item.
	 */
	private class TransferLeadershipAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			String[] args = { "leader" };
			SlashActionRepository.get("group").execute(args, member);
		}
	}
}
