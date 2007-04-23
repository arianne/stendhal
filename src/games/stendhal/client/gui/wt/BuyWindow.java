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
public class BuyWindow extends InternalManagedDialog {
	/**
	 * The buyWindow
	 */
	protected BuyWindowContent content;

	/** Creates a new instance of Buddies */
	public BuyWindow(StendhalUI ui) {
		super("buywindow", "Purchase Items");

		content = new BuyWindowContent(ui, this);
		setContent(content);
	}

	
}
