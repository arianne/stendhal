/*
 * @(#) src/games/stendhal/client/gui/wt/GameButtonHelper.java
 *
 * $Id$
 */

package games.stendhal.client.gui.wt;

//
//

import games.stendhal.client.gui.j2DClient;

/**
 * The GameButtonHelper.
 */
class GameButtonHelper extends InternalManagedDialog {
	/**
	 * The buyWindow.
	 */
	private GameButtonHelperContent content;

	/** Creates a new instance of Buddies. 
	 * @param sp 
	 * @param ui */
	public GameButtonHelper(final SettingsPanel sp, final j2DClient ui) {
		super("gametools", "Game Tools");

		content = new GameButtonHelperContent(sp, this, ui);
		setContent(content);

	}

}
