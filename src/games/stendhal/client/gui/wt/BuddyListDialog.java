/*
 * @(#) src/games/stendhal/client/gui/wt/BuddyListDialog.java
 *
 * $Id$
 */

package games.stendhal.client.gui.wt;

//
//

import javax.swing.JComponent;

/**
 * The player's buddy list dialog.
 */
public class BuddyListDialog extends InternalManagedDialog {

	protected BuddyListPanel content;

	/** Creates a new instance of Buddies */
	public BuddyListDialog() {
		super("buddies", "Buddies");
	}

	//
	// BuddyListDialog
	//

	public void update() {
		content.updateList();
	}

	//
	// ManagedDialog
	//

	@Override
	protected JComponent createContent() {
		content = new BuddyListPanel();

		return content;
	}
}
