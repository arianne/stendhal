package games.stendhal.client.gui.buddies;

import games.stendhal.client.gui.j2DClient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class TalkBuddyAction implements ActionListener {
	private final String buddyName;
	protected TalkBuddyAction(final String buddyName) {
		if (buddyName.indexOf(' ') > -1) {
			this.buddyName = "'" + buddyName + "'";
		} else {
			this.buddyName = buddyName;
		}

		
	}

	public void actionPerformed(final ActionEvent e) {
		j2DClient.get().setChatLine("/tell " + buddyName + " ");

	}

}
