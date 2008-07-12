/*
 * @(#) src/games/stendhal/client/gui/wt/BuddyListDialog.java
 *
 * $Id$
 */

package games.stendhal.client.gui.wt;

//
//

import games.stendhal.client.StendhalUI;

/**
 * The player's buddy list dialog.
 */
public class BuddyListDialog extends InternalManagedDialog {
	/**
	 * The buddy list.
	 */
	protected BuddyListPanel content;

	/** Creates a new instance of Buddies. 
	 * @param ui */
	public BuddyListDialog(final StendhalUI ui) {
		super("buddies", "Buddies");

		content = new BuddyListPanel(ui);
		setContent(content);
	}

	//
	// BuddyListDialog
	//

	public void update() {
		content.updateList();
	}
}
