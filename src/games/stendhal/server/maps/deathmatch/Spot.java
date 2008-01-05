package games.stendhal.server.maps.deathmatch;

import games.stendhal.server.core.engine.StendhalRPZone;

public class Spot {
	private StendhalRPZone zone;
	private int x;
	private int y;

	/**
	 * is a defined place in a zone.
	 *
	 * Spots are read only
	 *
	 * @param zone must not be null
	 * @param x
	 * @param y
	 */
	public Spot(StendhalRPZone zone, int x, int y) {
		super();
		assert (zone != null); //todo: remove this
		this.zone = zone;
		this.x = x;
		this.y = y;
	}
	int getX() {
		return x;
	}
	int getY() {
		return y;
	}
	StendhalRPZone getZone() {
		return zone;
	}
	
	
}
