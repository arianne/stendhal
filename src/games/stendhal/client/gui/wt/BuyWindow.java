/*
 * @(#) src/games/stendhal/client/gui/wt/BuyWindow.java
 *
 * $Id$
 */

package games.stendhal.client.gui.wt;

//
//

import games.stendhal.client.StendhalUI;

/**
 * The player buy window.
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
