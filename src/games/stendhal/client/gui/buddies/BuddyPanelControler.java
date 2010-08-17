package games.stendhal.client.gui.buddies;

import games.stendhal.client.gui.styled.WoodStyle;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.SwingUtilities;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

public class BuddyPanelControler implements PropertyChangeListener {
	private static PropertyChangeListener instance;
	private static PropertyChangeListener mapBasedInstance;
	private BuddyPanel buddyPanel;

	public BuddyPanelControler() {
		buddyPanel = new BuddyPanel(WoodStyle.getInstance());
		buddyPanel.setVisible(true);

		buddyPanel.setSize(100, 200);
		instance = this;
		mapBasedInstance = new MapBasedBuddyPropertyChangeListener();
	}

	public Component getComponent() {
		return buddyPanel;
	}

	public void propertyChange(final PropertyChangeEvent evt) {
		if (evt == null) {
			return;
		}
		
		//remove buddy from panel
		RPSlot buddyslot = (RPSlot) evt.getOldValue();
		if (buddyslot != null) {
			for (RPObject object : buddyslot) {
				for (final String buddyname : object) {
					if (!"id".equals(buddyname)) {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								buddyPanel.remove(buddyname.substring(1));		
							}
						});
					}
				}
				return;

			}
		}

		//change online status
		buddyslot = (RPSlot) evt.getNewValue();
		for (RPObject object : buddyslot) {
			for (final String buddyname : object) {
				if (!"id".equals(buddyname)) {
					if (object.get(buddyname).equals("0")) {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								buddyPanel.setOffline(buddyname.substring(1));
							}
						});
					} else {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								buddyPanel.setOnline(buddyname.substring(1));
							}
						});
					}
				}
			}

		}

	}

	public static PropertyChangeListener get() {
		return instance;
	}
	
	public static PropertyChangeListener getMapBasedBuddyPropertyChangeListener() {
		return mapBasedInstance;
	}
	
	class MapBasedBuddyPropertyChangeListener implements PropertyChangeListener {

		@SuppressWarnings("unchecked")
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt == null) {
				return;
			}
			//remove
			Map<String, String> oldBuddies = (Map<String, String>) evt.getOldValue();
			if (oldBuddies != null) {
				for (final Entry<String, String> entry : oldBuddies.entrySet()) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							buddyPanel.remove(entry.getKey());		
						}
					});
				}
			}
			//change online status
			Map<String, String> newBuddies = (Map<String, String>) evt.getNewValue();
			if (newBuddies != null) {
				for (final Entry<String, String> entry : newBuddies.entrySet()) {
					if (!Boolean.parseBoolean(entry.getValue())) {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								buddyPanel.setOffline(entry.getKey());
							}
						});
					} else {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								buddyPanel.setOnline(entry.getKey());
							}
						});
					}
				}
			}
		}
		
	}

}
