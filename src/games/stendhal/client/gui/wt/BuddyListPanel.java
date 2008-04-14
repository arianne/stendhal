/**
 * @(#) src/games/stendhal/client/gui/wt/BuddyListPanel.java
 *
 * $Id$
 */

package games.stendhal.client.gui.wt;

import games.stendhal.client.StendhalUI;
import games.stendhal.client.events.BuddyChangeListener;
import games.stendhal.client.gui.ClientPanel;
import games.stendhal.client.gui.wt.buddies.ActionSelectedCB;
import games.stendhal.client.gui.wt.buddies.BuddyLabel;

import java.awt.Component;
import java.awt.FlowLayout;

/**
 * A panel representing a buddy list.
 */
@SuppressWarnings("serial")
public final class BuddyListPanel extends ClientPanel implements BuddyChangeListener {

	

	/**
	 * Create a buddy list panel.
	 */
	public BuddyListPanel(StendhalUI ui) {
		super("Buddies", 100, 100);
		
		setLayout(new FlowLayout());
		ActionSelectedCB.setUI(ui);
	
	}

	public void buddyAdded(String buddyName) {
		this.add(new BuddyLabel(buddyName));
		validate();
	
	}

	public void buddyOffline(String buddyName) {
		Component c = findComponentByName(buddyName);
		if (c != null) {
			c.setEnabled(false);
		}

	}

	public void buddyOnline(String buddyName) {
		Component c = findComponentByName(buddyName);
		if (c != null) {
			c.setEnabled(true);
		}

	}

	public void buddyRemoved(String buddyName) {
		Component c = findComponentByName(buddyName);
		if (c != null) {
			this.remove(c);
		}
	

	}

	private Component findComponentByName(String name) {

		for (Component c : this.getContentPane().getComponents()) {
			if (name.equalsIgnoreCase(c.getName())) {
				return c;
			}
		}
		return null;

	}
 
}
