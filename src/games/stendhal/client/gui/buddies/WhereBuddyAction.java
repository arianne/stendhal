package games.stendhal.client.gui.buddies;

import games.stendhal.client.actions.SlashActionRepository;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class WhereBuddyAction implements ActionListener {

	private final String buddyName;

	protected WhereBuddyAction(final String buddyName) {
		this.buddyName = buddyName;
	}

	public void actionPerformed(final ActionEvent e) {
		final String [] args = new String [1];
		args[0] = buddyName;
		
		SlashActionRepository.get("where").execute(args, null);
	

	}

}
