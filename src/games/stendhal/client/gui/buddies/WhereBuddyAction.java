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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import games.stendhal.client.actions.SlashActionRepository;

class WhereBuddyAction implements ActionListener {
	private final String buddyName;

	protected WhereBuddyAction(final String buddyName) {
		this.buddyName = buddyName;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		SlashActionRepository.get("where").execute(null, buddyName);
	}
}
