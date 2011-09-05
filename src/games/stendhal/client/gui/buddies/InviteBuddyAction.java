package games.stendhal.client.gui.buddies;

import games.stendhal.client.actions.SlashActionRepository;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InviteBuddyAction implements ActionListener {
	
	private final String buddyName;
	
	public InviteBuddyAction(String buddyName) {
		if (buddyName.indexOf(' ') > -1) {
			this.buddyName = "'" + buddyName + "'";
		} else {
			this.buddyName = buddyName;
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		SlashActionRepository.get("group").execute(new String[]{"invite"}, this.buddyName);
	}

}
