/*
 * @(#) src/games/stendhal/client/gui/wt/BuddyListDialog.java
 *
 * $Id$
 */

package games.stendhal.client.gui.wt;

//
//

import javax.swing.JComponent;
import games.stendhal.client.StendhalUI;

/**
 * The player's buddy list dialog.
 */
public class BuddyListDialog extends InternalManagedDialog {
	/**
	 * The buddy list.
	 */
	protected BuddyListPanel content;

	/** Creates a new instance of Buddies */
	public BuddyListDialog(StendhalUI ui) {
		super("buddies", "Buddies");

		setContent(new BuddyListPanel(ui));
	}

	//
	// BuddyListDialog
	//

	public void update() {
		content.updateList();
	}
}
