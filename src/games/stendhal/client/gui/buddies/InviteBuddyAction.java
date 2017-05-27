/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import games.stendhal.client.actions.SlashActionRepository;

class InviteBuddyAction implements ActionListener {
	private final String buddyName;

	InviteBuddyAction(String buddyName) {
		if (buddyName.indexOf(' ') > -1) {
			this.buddyName = "'" + buddyName + "'";
		} else {
			this.buddyName = buddyName;
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		SlashActionRepository.get("group").execute(new String[]{"invite"}, this.buddyName);
	}
}
