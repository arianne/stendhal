/*
 * @(#) src/games/stendhal/client/events/PositionChangeListener.java
 *
 * $Id$
 */

package games.stendhal.client.listener;


/**
 * A listener of position events.
 */
public interface PositionChangeListener {
	/**
	 * The user position changed.
	 * 
	 * @param x
	 *            The X coordinate (in world units).
	 * @param y
	 *            The Y coordinate (in world units).
	 */
	void positionChanged(double x, double y);
}
