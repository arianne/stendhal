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
public class GameButtonHelper extends InternalManagedDialog {
	/**
	 * The buyWindow
	 */
	protected GameButtonHelperContent content;

	/** Creates a new instance of Buddies */
	public GameButtonHelper(SettingsPanel sp, StendhalUI ui) {
		super("buywindow", "Purchase Items");

		content = new GameButtonHelperContent(sp, this, ui);
		setContent(content);
		
	}

	
}
