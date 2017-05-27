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

import java.awt.Color;
import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import games.stendhal.client.sprite.DataLoader;

/**
 * Rendering component for buddies in a JList.
 */
class BuddyLabel extends JLabel implements ListCellRenderer<Buddy> {
	/**
	 * The online icon image.
	 */
	private static ImageIcon onlineIcon = new ImageIcon(DataLoader.getResource("data/gui/buddy_online.png"));

	/**
	 * The offline icon image.
	 */
	private static ImageIcon offlineIcon = new ImageIcon(DataLoader.getResource("data/gui/buddy_offline.png"));

	/**
	 * Create new BuddyLabel.
	 */
	public BuddyLabel() {
		super();
		initialize();
		this.setText("bobbele");
	}

	/**
	 * Set the buddy online or away.
	 *
	 * @param online <code>true</code> if the buddy is online,
	 * <code>false</code> if away
	 */
	private void setOnline(final boolean online) {
		this.setEnabled(online);
	}

	/**
	 * This method initializes icons, foreground and size.
	 *
	 */
	private void initialize() {
		this.setOpaque(false);
		this.setIcon(onlineIcon);
		this.setDisabledIcon(offlineIcon);
		this.setForeground(Color.GREEN);
	}

	@Override
	public Component getListCellRendererComponent(JList<? extends Buddy> list, Buddy buddy,
			int index, boolean selected, boolean focused) {
		// We ignore most of the parameters
		setText(buddy.getName());
		setOnline(buddy.isOnline());
		return this;
	}
}
