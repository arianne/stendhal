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

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class BuddyPanelControler implements PropertyChangeListener {
	private static PropertyChangeListener instance;
	
	private final JComponent buddyPanel;
	private final BuddyListModel model;

	public BuddyPanelControler() {
		// The panel is actually just the background
		buddyPanel = new JPanel();
		// the default layout manager is too dumb to understand alignment
		buddyPanel.setLayout(new BoxLayout(buddyPanel, BoxLayout.Y_AXIS));
		// now the actual sorted list
		model = new BuddyListModel();
		JList list = new BuddyPanel(model);
		list.setAlignmentX(Component.LEFT_ALIGNMENT);
		buddyPanel.add(list);
		instance = this;
	}

	public Component getComponent() {
		return buddyPanel;
	}
	
	public void propertyChange(final PropertyChangeEvent evt) {
		if (evt == null) {
			return;
		}
		//remove
		@SuppressWarnings("unchecked")
		Map<String, String> oldBuddies = (Map<String, String>) evt.getOldValue();
		if (oldBuddies != null) {
			for (final Entry<String, String> entry : oldBuddies.entrySet()) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						model.removeBuddy(entry.getKey());		
					}
				});
			}
		}
		//change online status
		@SuppressWarnings("unchecked")
		Map<String, String> newBuddies = (Map<String, String>) evt.getNewValue();
		if (newBuddies != null) {
			for (final Entry<String, String> entry : newBuddies.entrySet()) {
				final boolean online = Boolean.parseBoolean(entry.getValue());
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						model.setOnline(entry.getKey(), online);
					}
				});
			}
		}
	}

	public static PropertyChangeListener get() {
		return instance;
	}

}
