/***************************************************************************
 *                   (C) Copyright 2003-2018 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.util;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.IRPZone;

/**
 * An area is a specified place on a specified zone like (88, 78) to (109, 98)
 * in 0_ados_wall_n.
 *
 * @author hendrik
 */
public class Area {

	private final StendhalRPZone zone;
	private final Shape shape;

	/**
	 * Creates a new Area.
	 *
	 * @param zone
	 *            name of the map
	 * @param shape
	 *            shape on that map
	 */
	public Area(final StendhalRPZone zone, final Rectangle2D shape) {
		this.zone = zone;
		this.shape = shape;
	}

	/**
	 * Creates a new Area.
	 *
	 * @param zone name of the map
	 * @param x x
	 * @param y y
	 * @param width width
	 * @param height height
	 */
	public Area(final StendhalRPZone zone, int x, int y, int width, int height) {
		this.zone = zone;
		final Rectangle2D myshape = new Rectangle2D.Double();
		myshape.setRect(x, y, width, height);
		this.shape = myshape;
	}
	/**
	 * Checks whether an entity is in this area (e. g. on this zone and inside of
	 * the shape)
	 *
	 * @param entity
	 *            An entity to check
	 * @return true, if and only if the entity is in this area.
	 */
	public boolean contains(final Entity entity) {
		if (entity == null) {
			return false;
		}
		final IRPZone entityZone = entity.getZone();

		// We have ask the zone whether it knows about the entity because
		// player-objects stay alive some time after logout.
		return zone.equals(entityZone) && zone.has(entity.getID())
				&& shape.contains(entity.getX(), entity.getY());
	}

	/**
	 * Checks whether a respawn point is within this area.
	 *
	 * @param point the point to examine
	 * @return <code>true</code> if the point is within this area, <code>
	 * false otherwise</code>
	 */
	public boolean contains(final CreatureRespawnPoint point) {
		if (point == null) {
			return false;
		}
		final IRPZone entityZone = point.getZone();

		// We have ask the zone whether it knows about the entity because
		// player-objects stay alive some time after logout.
		return zone.equals(entityZone) && shape.contains(point.getX(), point.getY());
	}

	/**
	 * Gets the shape.
	 *
	 * @return shape
	 */
	public Shape getShape() {
		return shape;
	}

	/**
	 * Gets a list of players in the area.
	 *
	 * @return  A list of all players in the area.
	 */
	public List<Player> getPlayers() {
		final List<Player> playersInZone = zone.getPlayers();
		// for each of the players in the zone, check contains(player)
		final List<Player> result = new LinkedList<Player>();
		for (Player player : playersInZone) {
			if (this.contains(player)) {
				result.add(player);
			}
		}
		return result;
	}

	/**
	 gets the zone
	 *
	 * @return StendhalRPZone
	 */
	public StendhalRPZone getZone() {
		return zone;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((shape == null) ? 0 : shape.hashCode());
		result = prime * result + ((zone == null) ? 0 : zone.getName().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		Area other = (Area) obj;
		if (shape == null) {
			if (other.shape != null) {
				return false;
			}
		} else if (!shape.equals(other.shape)) {
			return false;
		}

		if (zone == null) {
			if (other.zone != null) {
				return false;
			}
		} else if (!zone.getName().equals(other.zone.getName())) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Area [zone=" + zone.getName() + ", shape=" + shape + "]";
	}

}
