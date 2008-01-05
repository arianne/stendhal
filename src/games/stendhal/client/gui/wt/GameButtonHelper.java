/*
 * @(#) src/games/stendhal/client/gui/wt/GameButtonHelper.java
 *
 * $Id$
 */

package games.stendhal.client.gui.wt;

//
//

import games.stendhal.client.StendhalUI;

/**
 * The GameButtonHelper.
 */
public class GameButtonHelper extends InternalManagedDialog {
	/**
	 * The buyWindow.
	 */
	protected GameButtonHelperContent content;

	/** Creates a new instance of Buddies. */
	public GameButtonHelper(SettingsPanel sp, StendhalUI ui) {
		super("gametools", "Game Tools");

		content = new GameButtonHelperContent(sp, this, ui);
		setContent(content);

	}

}
