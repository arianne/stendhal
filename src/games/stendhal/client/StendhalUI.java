/*
 * @(#) src/games/stendhal/client/StendhalUI.java
 *
 * $Id$
 */

package games.stendhal.client;

//
//

import games.stendhal.client.entity.Inspector;
import games.stendhal.client.gui.ManagedWindow;

import java.awt.Color;

import javax.swing.JPopupMenu;

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
	 * TODO: Change all calls to this to use NotificationType form
	 */
	public abstract void addEventLine(String text, Color color);

	/**
	 * Add an event line.
	 *
	 */
	public abstract void addEventLine(String text, NotificationType type);

	/**
	 * Add an event line.
	 *
	 */
	public abstract void addEventLine(String header, String text, NotificationType type);


	/**
	 * Initiate outfit selection by the user.
	 */
	public abstract void chooseOutfit();

	/**
	 *Like chooseOutfit(), but for Guilds
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
	 * Get the entity inspector.
	 *
	 * @return	The inspector.
	 */
	public abstract Inspector getInspector();


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
	 * Request quit confirmation from the user.
	 */
	public abstract void requestQuit();


	/**
	 * Set the shared [singleton] value.
	 *
	 * @param sharedUI The stendhal UI.
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


	/**
	 * Set the offline indication state.
	 *
	 * @param	offline		<code>true</code> if offline.
	 */
	public abstract void setOffline(boolean offline);


	//
	// <StendhalGUI>
	//
	// These really shouldn't be here, as they are UI implementation
	// specific. But for now this will allow more code refactoring,
	// until this can be pushed into a sub-class.
	//

	/**
	 * Add a new window.
	 *
	 * @param	mw		A managed window.
	 */
	public abstract void addWindow(ManagedWindow mw);

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

	/**
	 * Sets the context menu. It is closed automatically one the user
	 * clicks outside of it.
	 */
	 public abstract void setContextMenu(JPopupMenu contextMenu);
}
