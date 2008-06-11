package games.stendhal.client.gui.wt;

import games.stendhal.client.gui.wt.buddies.BuddyLabel;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JPanel;

public class BuddyPanel extends JPanel {
	public BuddyPanel() {
		setLayout(new FlowLayout());
	}

	public void buddyAdded(String buddyName) {
		add(new BuddyLabel(buddyName));
		validate();
	
	}

	private Component findComponentByName(String name) {
	
		for (Component c : getComponents()) {
			if (name.equalsIgnoreCase(c.getName())) {
				return c;
			}
		}
		return null;
	
	}

	public void buddyOffline(String buddyName) {
		Component c = findComponentByName(buddyName);
		if (c != null) {
			c.setEnabled(false);
		}
	
	}

	public void buddyOnline(String buddyName) {
		Component c = findComponentByName(buddyName);
		if (c != null) {
			c.setEnabled(true);
		}
	
	}

	public void buddyRemoved(String buddyName) {
		Component c = findComponentByName(buddyName);
		if (c != null) {
			remove(c);
		}
		validate();
	}
}
