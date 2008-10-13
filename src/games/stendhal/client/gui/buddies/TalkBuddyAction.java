package games.stendhal.client.gui.buddies;

import games.stendhal.client.gui.StendhalGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TalkBuddyAction implements ActionListener {
	private final String buddyName;
	public TalkBuddyAction(final String buddyName) {
		if (buddyName.indexOf(' ') > -1) {
			this.buddyName = "'" + buddyName + "'";
		} else {
			this.buddyName = buddyName;
		}

		
	}

	public void actionPerformed(final ActionEvent e) {
		StendhalGUI.get().setChatLine("/tell " + buddyName + " ");

	}

}
