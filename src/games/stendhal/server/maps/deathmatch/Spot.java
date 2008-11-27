package games.stendhal.server.maps.deathmatch;

import games.stendhal.server.core.engine.StendhalRPZone;

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
	 * @param x
	 * @param y
	 */
	public Spot(final StendhalRPZone zone, final int x, final int y) {
		super();
		this.zone = zone;
		this.x = x;
		this.y = y;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public StendhalRPZone getZone() {
		return zone;
	}
	
	
}
