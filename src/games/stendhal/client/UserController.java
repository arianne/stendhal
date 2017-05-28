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
package games.stendhal.client;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map;
import java.util.Map.Entry;

import games.stendhal.client.gui.buddies.BuddyPanelController;
import games.stendhal.client.gui.stats.KarmaIndicator;
import games.stendhal.client.gui.stats.ManaIndicator;
import games.stendhal.client.gui.stats.StatsPanelController;
import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

class UserController implements ObjectChangeListener {

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	//TODO: add 2 more for events and slots so you can add listeners distinguished
	// maybe extend this

	public UserController() {
		pcs.addPropertyChangeListener("buddies", BuddyPanelController.get());
		pcs.addPropertyChangeListener("features", KarmaIndicator.get());
		pcs.addPropertyChangeListener("features", ManaIndicator.get());

		StatsPanelController stats = StatsPanelController.get();
		stats.registerListeners(pcs);
	}

	@Override
	public void deleted() {
		for (final PropertyChangeListener listener : pcs.getPropertyChangeListeners()) {
			listener.propertyChange(null);
		}
	}

	@Override
	public void modifiedAdded(final RPObject changes) {
		for (final String attrib : changes) {
			pcs.firePropertyChange(attrib, null, changes.get(attrib));
		}
		for (final RPEvent event : changes.events()) {
			pcs.firePropertyChange(event.getName(), null, event);
		}
		for (final RPSlot slot : changes.slots()) {
			pcs.firePropertyChange(slot.getName(), null, slot);
		}
		for (Entry<String, Map<String, String>> entry : changes.maps().entrySet()) {
			pcs.firePropertyChange(entry.getKey(), null, entry.getValue());
		}
	}

	@Override
	public void modifiedDeleted(final RPObject changes) {
		for (final String attrib : changes) {
			pcs.firePropertyChange(attrib, changes.get(attrib), null);
		}
		for (final RPEvent event : changes.events()) {
			pcs.firePropertyChange(event.getName(), event, null);
		}
		for (final RPSlot slot : changes.slots()) {
			pcs.firePropertyChange(slot.getName(), slot, null);
		}
		for (Entry<String, Map<String, String>> entry : changes.maps().entrySet()) {
			pcs.firePropertyChange(entry.getKey(), entry.getValue(), null);
		}
	}

}
