package games.stendhal.server.util;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Entity;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import marauroa.common.game.IRPZone;

/**
 * An area is a specified place on a specified zone like
 * (88, 78) to (109, 98) in 0_ados_wall_n.
 *
 * @author hendrik
 */
public class Area {
	private IRPZone zone = null;
	private Shape shape = null; 

	/**
	 * Create a new Area
	 *
	 * @param mapName name of the map
	 * @param shape   shape on that map
	 */
	public Area(IRPZone zone, Rectangle2D shape) {
		this.zone = zone;
		this.shape = shape;
	}

	/**
	 * Checks wether an entity is in this area (e. g. on this zone and inside of the shape)
	 *
	 * @param entity An entity to check
	 * @return true, if and only if the entity is in this area.
	 */
	public boolean contains(Entity entity) {
		IRPZone diceZone = StendhalRPWorld.get().getRPZone(entity.getID());
		return zone.equals(diceZone) && shape.contains(entity.getX(), entity.getY());
	}
}
