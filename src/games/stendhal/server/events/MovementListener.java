/*
 * @(#) src/games/stendhal/server/events/MovementListener.java
 *
 * $Id$
 */

package games.stendhal.server.events;

//
//

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.ActiveEntity;

import java.awt.geom.Rectangle2D;

/**
 * Objects that monitor being moved over an area in a zone.
 * 
 * NOTE: This does not currently handle enter/exit situations where an entity
 * does not "walk" (e.g. teleported, signon/off).
 */
public interface MovementListener {
	/**
	 * Get the area that this object occupies.
	 * 
	 * @return A rectangular area.
	 */
	Rectangle2D getArea();

	/**
	 * Invoked when an entity enters the object area.
	 * 
	 * @param entity
	 *            The entity that moved.
	 * @param zone
	 *            The new zone.
	 * @param newX
	 *            The new X coordinate.
	 * @param newY
	 *            The new Y coordinate.
	 */
	void onEntered(ActiveEntity entity, StendhalRPZone zone, int newX, int newY);

	/**
	 * Invoked when an entity leaves the object area.
	 * 
	 * @param entity
	 *            The entity that entered.
	 * @param zone
	 *            The old zone.
	 * @param oldX
	 *            The old X coordinate.
	 * @param oldY
	 *            The old Y coordinate.
	 * 
	 */
	void onExited(ActiveEntity entity, StendhalRPZone zone, int oldX, int oldY);

	/**
	 * Invoked when an entity moves while over the object area.
	 * 
	 * @param entity
	 *            The entity that left.
	 * @param zone
	 *            The zone.
	 * @param oldX
	 *            The old X coordinate.
	 * @param oldY
	 *            The old Y coordinate.
	 * @param newX
	 *            The new X coordinate.
	 * @param newY
	 *            The new Y coordinate.
	 */
	void onMoved(ActiveEntity entity, StendhalRPZone zone, int oldX, int oldY,
			int newX, int newY);
}
