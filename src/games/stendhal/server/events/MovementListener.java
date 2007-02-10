/*
 * @(#) src/games/stendhal/server/events/MovementListener.java
 *
 * $Id$
 */

package games.stendhal.server.events;

//
//

import java.awt.geom.Rectangle2D;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;

/**
 * Objects that monitor being moved over an area in a zone.
 *
 * NOTE: This does not currently handle enter/exit situations where an
 * entity does not "walk" (e.g. teleported, signon/off).
 */
public interface MovementListener {
	/**
	 * Get the area that this object occupies.
	 *
	 * @return	A rectangular area.
	 */
	public Rectangle2D getArea();


	/**
	 * Invoked when an entity enters the object area.
	 *
	 * @param	entity		The RPEntity who moved.
	 * @param	zone		The new zone.
	 * @param	newX		The new X coordinate.
	 * @param	newY		The new Y coordinate.
	 */
	public void onEntered(RPEntity entity, StendhalRPZone zone,
	 int newX, int newY);


	/**
	 * Invoked when an entity leaves the object area.
	 *
	 * @param	entity		The RPEntity who entered.
	 * @param	zone		The old zone.
	 * @param	oldX		The old X coordinate.
	 * @param	oldY		The old Y coordinate.
	 *
	 */
	public void onExited(RPEntity entity, StendhalRPZone zone,
	 int oldX, int oldY);


	/**
	 * Invoked when an entity moves while over the object area.
	 *
	 * @param	entity		The RPEntity who left.
	 * @param	zone		The zone.
	 * @param	oldX		The old X coordinate.
	 * @param	oldY		The old Y coordinate.
	 * @param	newX		The new X coordinate.
	 * @param	newY		The new Y coordinate.
	 */
	public void onMoved(RPEntity entity, StendhalRPZone zone,
	 int oldX, int oldY, int newX, int newY);
}
