package games.stendhal.client.gui.buddies;

import games.stendhal.client.actions.SlashActionRepository;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class TeleportToBuddyAction implements ActionListener {

    private final String buddyName;

    protected TeleportToBuddyAction(final String buddyName) {
        this.buddyName = buddyName;
    }

    public void actionPerformed(final ActionEvent e) {
        String remainder = buddyName;
        
        SlashActionRepository.get("teleportto").execute(null, remainder);
    
    }

}
