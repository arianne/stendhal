/*
 * @(#) src/games/stendhal/client/gui/wt/BuddyListDialog.java
 *
 * $Id$
 */

package games.stendhal.client.gui.wt;

//
//

import java.awt.Frame;
import javax.swing.JComponent;

/**
 * The player's buddy list dialog.
 */
public class BuddyListDialog extends ManagedDialog {

	protected BuddyListPanel content;

	/** Creates a new instance of Buddies */
	public BuddyListDialog(Frame frame) {
		super(frame, "buddies", "Buddies");

		dialog.setResizable(false);
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
