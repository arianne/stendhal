/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.engine;


/**
 * a location within a zone
 *
 * @author hendrik
 */
public class Spot {
	private final StendhalRPZone zone;
	private final int x;
	private final int y;

	/**
	 * is a defined place in a zone.
	 *
	 * Spots are read only
	 *
	 * @param zone must not be null
	 * @param x x
	 * @param y y
	 */
	public Spot(final StendhalRPZone zone, final int x, final int y) {
		this.zone = zone;
		this.x = x;
		this.y = y;
	}

	/**
	 * gets x
	 *
	 * @return x
	 */
	public int getX() {
		return x;
	}

	/**
	 * gets y
	 *
	 * @return y
	 */
	public int getY() {
		return y;
	}

	/**
	 * gets the zone
	 *
	 * @return zone
	 */
	public StendhalRPZone getZone() {
		return zone;
	}

}
