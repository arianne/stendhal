/*
 * @(#) src/games/stendhal/client/StendhalUI.java
 *
 * $Id$
 */

package games.stendhal.client;

import games.stendhal.client.gui.ManagedWindow;
import games.stendhal.common.NotificationType;

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
	private static StendhalUI sharedUI;

	/**
	 * The stendhal client.
	 */
	protected StendhalClient client;

	/**
	 * Create a stendhal UI.
	 * 
	 * @param client
	 *            The client.
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
	public abstract void addEventLine(String text, NotificationType type);

	/**
	 * Add an event line.
	 * 
	 */
	public abstract void addEventLine(String header, String text,
			NotificationType type);

        /**
         * Adds a Swing component to the view
         */
        public abstract void addWindow(ManagedWindow imd);
        
	/**
	 * Initiate outfit selection by the user.
	 */
	public abstract void chooseOutfit();

	/**
	 * Like chooseOutfit(), but for Guilds
	 */
	public abstract void ManageGuilds();

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
	 * @return The client.
	 */
	public StendhalClient getClient() {
		return client;
	}

	/**
	 * Get the current game screen height.
	 * 
	 * @return The height.
	 */
	public abstract int getHeight();

	/**
	 * Get the game screen.
	 * 
	 * @return The game screen.
	 */
	public abstract IGameScreen getScreen();

	/**
	 * Get the current game screen width.
	 * 
	 * @return The width.
	 */
	public abstract int getWidth();

	/**
	 * Request quit confirmation from the user.
	 */
	public abstract void requestQuit();

	/**
	 * Set the shared [singleton] value.
	 * 
	 * @param sharedUI
	 *            The stendhal UI.
	 */
	public static void setDefault(StendhalUI sharedUI) {
		StendhalUI.sharedUI = sharedUI;
	}

	/**
	 * Set the input chat line text.
	 * 
	 * @param text
	 *            The text.
	 */
	public abstract void setChatLine(String text);

	/**
	 * Set the offline indication state.
	 * 
	 * @param offline
	 *            <code>true</code> if offline.
	 */
	public abstract void setOffline(boolean offline);

	/**
	 * Set the user's positiion.
	 *
	 * @param	x		The user's X coordinate.
	 * @param	y		The user's Y coordinate.
	 */
	public abstract void setPosition(double x, double y);


	//
	// <StendhalGUI>
	//
	// These really shouldn't be here, as they are UI implementation
	// specific. But for now this will allow more code refactoring,
	// until this can be pushed into a sub-class.
	//

	/**
	 * @return Returns the altDown.
	 */
	public abstract boolean isAltDown();

	/**
	 * @return Returns the ctrlDown.
	 */
	public abstract boolean isCtrlDown();

	/**
	 * @return Returns the shiftDown.
	 */
	public abstract boolean isShiftDown();
}
