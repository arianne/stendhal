/**
 * 
 */
package games.stendhal.client.gui.buddies;

import games.stendhal.client.actions.SlashActionRepository;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

final class RemovebuddyAction implements ActionListener {
	private final String buddyName;

	RemovebuddyAction(final String buddyName) {
		this.buddyName = buddyName;
	}

	public void actionPerformed(final ActionEvent e) {
		final String [] args = new String [1];
		args[0] = buddyName;
		
		SlashActionRepository.get("remove").execute(args, null);
		
	}
}
