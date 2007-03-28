/*
 * @(#) src/games/stendhal/client/StendhalUI.java
 *
 * $Id$
 */

package games.stendhal.client;

//
//

import java.awt.Color;

/**
 * A base class for the stendhal client UI (not GUI).
 *
 * This should have minimal UI-implementation dependent code. That's what
 * sub-classes are for!
 */
public abstract class StendhalUI {
	/**
	 * A shared [singleton] copy.
	 */
	private static StendhalUI	sharedUI;

	/**
	 * The stendhal client.
	 */
	protected StendhalClient	client;


	/**
	 * Create a stendhal UI.
	 *
	 * @param	client		The client.
	 */
	public StendhalUI(StendhalClient client) {
		this.client = client;
	}


	//
	// StendhalUI
	//

	/**
	 * Add an event line.
	 *
	 */
	public abstract void addEventLine(String text);

	/**
	 * Add an event line.
	 *
	 */
	public abstract void addEventLine(String header, String text);

	/**
	 * Add an event line.
	 *
	 */
	public abstract void addEventLine(String text, Color color);

	/**
	 * Add an event line.
	 *
	 */
	public abstract void addEventLine(String header, String text, Color color);

	/**
	 * Get the default UI.
	 *
	 *
	 */
	public static StendhalUI get() {
		return sharedUI;
	}


	/**
	 * Get the client.
	 *
	 * @return	The client.
	 */
	public StendhalClient getClient() {
		return client;
	}


	/**
	 * Get the current game screen height.
	 *
	 * @return	The height.
	 */
	public abstract int getHeight();


	/**
	 * Get the game screen.
	 *
	 * @return	The game screen.
	 */
	public abstract GameScreen getScreen();


	/**
	 * Get the current game screen width.
	 *
	 * @return	The width.
	 */
	public abstract int getWidth();


	/**
	 * Set the shared [singleton] value.
	 *
	 * @param	The stendhal UI.
	 */
	public static void setDefault(StendhalUI sharedUI) {
		StendhalUI.sharedUI = sharedUI;
	}


	/**
	 * Set the input chat line text.
	 *
	 * @param	text		The text.
	 */
	public abstract void setChatLine(String text);
}
