package games.stendhal.client.gui.buddies;

import games.stendhal.client.events.BuddyChangeListener;
import games.stendhal.client.gui.styled.WoodStyle;

import java.awt.Component;

public class BuddyPanelControler implements BuddyChangeListener {
	BuddyPanel buddyPanel;

	public BuddyPanelControler() {
		buddyPanel = new BuddyPanel(WoodStyle.getInstance());
		buddyPanel.setVisible(true);
		buddyPanel.setSize(100, 200);
	}

	public void buddyAdded(String buddyName) {

		buddyPanel.addBuddy(buddyName, false);

	}

	public void buddyOffline(String buddyName) {
		buddyPanel.setOffline(buddyName);

	}

	public void buddyOnline(String buddyName) {
		buddyPanel.setOnline(buddyName);
	}

	public void buddyRemoved(String buddyName) {
		buddyPanel.remove(buddyName);

	}

	public Component getComponent() {
		return buddyPanel;
	}

}
