package games.stendhal.client.gui.buddies;

import games.stendhal.client.gui.styled.Style;
import games.stendhal.client.gui.styled.swing.StyledJPanel;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.BoxLayout;

@SuppressWarnings("serial")
class BuddyPanel extends StyledJPanel {
	Map<String, BuddyLabel> labelMap = new ConcurrentHashMap<String, BuddyLabel>();
	public BuddyPanel(final Style style) {
		super(style);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setSize(new Dimension(168, 88));
		setVisible(true);
	}


	public void addBuddy(final String buddyName, final boolean isOnline) {
		final BuddyLabel label = new BuddyLabel(buddyName, isOnline);
		labelMap.put(buddyName, label);
		this.add(label, Component.LEFT_ALIGNMENT);
		revalidate();

	}

	void setOffline(final String buddyName) {
		labelMap.get(buddyName).setOnline(false);
		revalidate();
	}


	void setOnline(final String buddyName) {
		labelMap.get(buddyName).setOnline(true);
		revalidate();
	}


	public void remove(final String buddyName) {
		this.remove(labelMap.get(buddyName));
		labelMap.remove(buddyName);
		revalidate();
		repaint();
	}

}
