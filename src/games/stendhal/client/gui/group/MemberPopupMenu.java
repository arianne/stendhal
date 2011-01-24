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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Popup menu for the member entries in the group listing.
 */
class MemberPopupMenu extends JPopupMenu {
	MemberPopupMenu(String member) {
		JMenuItem kick = new JMenuItem("Kick");
		this.add(kick);
		kick.addActionListener(new KickAction(member));
	}
	
	/**
	 * Listener for activating the kick menu item.
	 */
	private static class KickAction implements ActionListener {
		private final String member;
		
		KickAction(String member) {
			this.member = member;
		}
		
		public void actionPerformed(ActionEvent arg0) {
			String[] args = { "kick" };
			SlashActionRepository.get("group").execute(args, member);
		}
	}
}
