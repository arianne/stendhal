/*
 * @(#) src/games/stendhal/client/gui/j2d/GameScreen2D.java
 *
 * $Id$
 *
 */

package games.stendhal.client.gui.j2d;

//
//

import games.stendhal.client.GameScreen;
import games.stendhal.client.StendhalClient;

import java.awt.Canvas;

/**
 * A 2D representation of a game screen.
 * 
 * TODO: Move 2D specific aspects of game screen here.
 */
public class GameScreen2D extends GameScreen {
	/**
	 * Create a 2D game screen.
	 * 
	 * @param client
	 *            The client.
	 * @param canvas
	 *            The canvas to render in.
	 */
	public GameScreen2D(final StendhalClient client, final Canvas canvas) {
		super(client, canvas);
	}
}
