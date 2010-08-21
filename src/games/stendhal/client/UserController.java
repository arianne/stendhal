package games.stendhal.client;

import games.stendhal.client.gui.bag.BagPanelControler;
import games.stendhal.client.gui.buddies.BuddyPanelControler;
import games.stendhal.client.gui.stats.KarmaIndicator;
import games.stendhal.client.gui.stats.StatsPanelController;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map;
import java.util.Map.Entry;

import marauroa.common.game.RPEvent;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

public class UserController implements ObjectChangeListener {

	final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	//TODO: add 2 more for events and slots so you can add listeners distinguished
	// maybe extend this
	
	public UserController() {
		pcs.addPropertyChangeListener("buddies", BuddyPanelControler.get());
		pcs.addPropertyChangeListener("bag", BagPanelControler.get());
		pcs.addPropertyChangeListener("features", KarmaIndicator.get());

		StatsPanelController stats = StatsPanelController.get();
		stats.registerListeners(pcs);
	}

	public void deleted() {
		for (final PropertyChangeListener listener : pcs.getPropertyChangeListeners()) {
			listener.propertyChange(null);
		}
	}

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
