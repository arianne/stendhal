package games.stendhal.client.gui.buddies;

import games.stendhal.client.gui.styled.WoodStyle;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.SwingUtilities;

public class BuddyPanelControler implements PropertyChangeListener {
	
	private static PropertyChangeListener instance;
	BuddyPanel buddyPanel;

	public BuddyPanelControler() {
		buddyPanel = new BuddyPanel(WoodStyle.getInstance());
		buddyPanel.setVisible(true);
		buddyPanel.setSize(100, 200);
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
						buddyPanel.remove(entry.getKey());		
					}
				});
			}
		}
		//change online status
		@SuppressWarnings("unchecked")
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

	public static PropertyChangeListener get() {
		return instance;
	}

}
