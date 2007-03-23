/*
 * @(#) src/games/stendhal/client/gui/ManagedWindow.java
 *
 * $Id$
 */

package games.stendhal.client.gui;

/**
 * A managed window.
 */
public interface ManagedWindow {

	/**
	 * Get the managed window name.
	 *
	 *
	 */
	public String getName();

	/**
	 * Get X coordinate of the window.
	 *
	 * @return	A value sutable for passing to <code>moveTo()</code>.
	 */
	public int getX();

	/**
	 * Get Y coordinate of the window.
	 *
	 * @return	A value sutable for passing to <code>moveTo()</code>.
	 */
	public int getY();

	/**
	 * Determine if the window is minimized.
	 *
	 * @return	<code>true</code> if the window is minimized.
	 */
	public boolean isMinimized();

	/**
	 * Determine if the window is visible.
	 *
	 * @return	<code>true</code> if the window is visible.
	 */
	public boolean isVisible();

	/**
	 * Move to a location. This may be subject to internal representation,
	 * and should only use what was passed from <code>getX()</code> and
	 * <code>getY()</code>.
	 *
	 * @param	x		The X coordinate;
	 * @param	y		The Y coordinate;
	 *
	 * @return	<code>true</code> if the move was allowed.
	 */
	public boolean moveTo(int x, int y);

	/**
	 * Set the window as minimized.
	 *
	 * @param	minimized	Whether the window should be minimized.
	 */
	public void setMinimized(boolean minimized);

	/**
	 * Set the window as visible (or hidden).
	 *
	 * @param	visible		Whether the window should be visible.
	 */
	public void setVisible(boolean visible);
}
