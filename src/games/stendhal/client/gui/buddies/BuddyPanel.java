package games.stendhal.client.gui.buddies;

import games.stendhal.client.gui.styled.Style;
import games.stendhal.client.gui.styled.swing.StyledJPanel;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.BoxLayout;

class BuddyPanel extends StyledJPanel{
	Map<String, BuddyLabel> labelMap = new ConcurrentHashMap<String, BuddyLabel>();
	public BuddyPanel(Style style) {
		super(style);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setSize(new Dimension(168, 88));
		setVisible(true);
	}


	public void addBuddy(String buddyName, boolean isOnline) {
		BuddyLabel bl = new BuddyLabel(buddyName, isOnline);
		labelMap.put(buddyName, bl);
		this.add(bl,Component.LEFT_ALIGNMENT);
		revalidate();

	}

	void setOffline(String buddyName) {
		labelMap.get(buddyName).setOnline(false);
		revalidate();
	}


	void setOnline(String buddyName) {
		labelMap.get(buddyName).setOnline(true);
		revalidate();
	}


	public void remove(String buddyName) {
		this.remove(labelMap.get(buddyName));
		labelMap.remove(buddyName);
		revalidate();
		repaint();
	}

}
