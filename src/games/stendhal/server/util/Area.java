package games.stendhal.server.util;

import games.stendhal.server.entity.Entity;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import marauroa.common.game.IRPZone;

/**
 * An area is a specified place on a specified zone like (88, 78) to (109, 98)
 * in 0_ados_wall_n.
 * 
 * @author hendrik
 */
public class Area {

	private IRPZone zone;

	private Shape shape;

	/**
	 * Creates a new Area.
	 * 
	 * @param zone
	 *            name of the map
	 * @param shape
	 *            shape on that map
	 */
	public Area(IRPZone zone, Rectangle2D shape) {
		this.zone = zone;
		this.shape = shape;
	}

	/**
	 * Checks wether an entity is in this area (e. g. on this zone and inside of
	 * the shape)
	 * 
	 * @param entity
	 *            An entity to check
	 * @return true, if and only if the entity is in this area.
	 */
	public boolean contains(Entity entity) {
		IRPZone entityZone = entity.getZone();

		// We have ask the zone whether it knows about the entity because
		// player-objects stay alive some time after logout.
		return zone.equals(entityZone) && zone.has(entity.getID())
				&& shape.contains(entity.getX(), entity.getY());
	}

	/**
	 * Gets the shape.
	 * 
	 * @return shape
	 */
	public Shape getShape() {
		return shape;
	}
}
