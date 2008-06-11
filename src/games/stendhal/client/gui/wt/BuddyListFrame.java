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

/**
 * A panel representing a buddy list.
 */
@SuppressWarnings("serial")
public final class BuddyListFrame extends ClientPanel implements BuddyChangeListener {

	BuddyPanel innerPanel = new BuddyPanel();

	/**
	 * Create a buddy list panel.
	 */
	public BuddyListFrame(StendhalUI ui) {
		super("Buddies", 100, 100);
		this.setResizable(true);
		this.add(innerPanel);
		ActionSelectedCB.setUI(ui);
	
	}


	public void buddyAdded(String buddyName) {
		innerPanel.buddyAdded(buddyName);
	}

	public void buddyOffline(String buddyName) {
		innerPanel.buddyOffline(buddyName);
	}

	public void buddyOnline(String buddyName) {
		innerPanel.buddyOnline(buddyName);
	}

	public void buddyRemoved(String buddyName) {
		innerPanel.buddyRemoved(buddyName);
	}
 
}
