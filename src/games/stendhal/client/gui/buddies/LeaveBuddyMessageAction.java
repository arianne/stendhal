package games.stendhal.client.gui.buddies;

import games.stendhal.client.gui.j2DClient;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class LeaveBuddyMessageAction implements ActionListener {
	private final String buddyName;
	protected LeaveBuddyMessageAction(final String buddyName) {
		if (buddyName.indexOf(' ') > -1) {
			this.buddyName = "'" + buddyName + "'";
		} else {
			this.buddyName  = buddyName;
		}
	}

	public void actionPerformed(final ActionEvent e) {
		

		j2DClient.get().setChatLine("/msg postman tell " + buddyName + " ");

	}

}
