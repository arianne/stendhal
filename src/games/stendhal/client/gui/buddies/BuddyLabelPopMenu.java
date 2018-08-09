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
package games.stendhal.client.gui.buddies;


import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import games.stendhal.client.entity.User;

class BuddyLabelPopMenu extends JPopupMenu {
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = -8258597079393606179L;

	protected BuddyLabelPopMenu(final String buddyName, final boolean online) {
		super(buddyName);
		if (online) {
			createOnlineMenu(buddyName);
		} else {
			createOfflineMenu(buddyName);
		}

		JMenuItem removeBuddyMenuItem = new JMenuItem("删除");
		this.add(removeBuddyMenuItem);
		removeBuddyMenuItem.addActionListener(new RemovebuddyAction(buddyName));
	}

	// this one will fill into the chatline : /tell postman tell buddyName
	// and then you type the message
	private void createOfflineMenu(final String buddyName) {
		JMenuItem leaveMessageBuddyMenuItem = new JMenuItem("离开消息");
		this.add(leaveMessageBuddyMenuItem);
		leaveMessageBuddyMenuItem.addActionListener(new LeaveBuddyMessageAction(buddyName));
	}

	private void createOnlineMenu(final String buddyName) {

		// this one will fill into the chatline : /tell buddyName
		// and then you type the message
		JMenuItem talkBuddyMenuItem = new JMenuItem("讲话");
		this.add(talkBuddyMenuItem);
		talkBuddyMenuItem.addActionListener(new TalkBuddyAction(buddyName));


		JMenuItem whereBuddyMenuItem = new JMenuItem("地点");
		this.add(whereBuddyMenuItem);
		whereBuddyMenuItem.addActionListener(new WhereBuddyAction(buddyName));

		JMenuItem inviteBuddyMenuItem = new JMenuItem("邀请");
		this.add(inviteBuddyMenuItem);
		inviteBuddyMenuItem.addActionListener(new InviteBuddyAction(buddyName));

		if (User.isAdmin()) {
			JMenuItem teleportToBuddyMenuItem = new JMenuItem("(*)传送至");
			this.add(teleportToBuddyMenuItem);
			teleportToBuddyMenuItem.addActionListener(new TeleportToBuddyAction(buddyName));
		}
	}
}
