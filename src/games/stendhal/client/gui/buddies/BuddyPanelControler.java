package games.stendhal.client.gui.buddies;

import games.stendhal.client.gui.styled.WoodStyle;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.SwingUtilities;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

public class BuddyPanelControler implements PropertyChangeListener {
	private static PropertyChangeListener instance;
	private BuddyPanel buddyPanel;

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

}
