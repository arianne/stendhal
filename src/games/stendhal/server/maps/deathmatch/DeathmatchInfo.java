package games.stendhal.server.maps.deathmatch;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.util.Area;

/**
 * Stores information about the place of the deathmatch.
 *
 * @author hendrik
 */
public class DeathmatchInfo {
	private final Area arena;
	private final String zoneName;
	private final StendhalRPZone zone;

	/**
	 * Creates a new DeathmatchInfo
	 *
	 * @param arena    combat area
	 * @param zoneName name of zone
	 * @param zone     zone
	 */
	public DeathmatchInfo(final Area arena, final String zoneName, final StendhalRPZone zone) {
		super();
		this.arena = arena;
		this.zoneName = zoneName;
		this.zone = zone;
	}

	/**
	 * gets the arena
	 *
	 * @return combat area
	 */
	public Area getArena() {
		return arena;
	}

	/**
	 * gets the zone
	 *
	 * @return zone
	 */
	public StendhalRPZone getZone() {
		return zone;
	}

	/**
	 * get the zone name
	 *
	 * @return name of zone
	 */
	public String getZoneName() {
		return zoneName;
	}
	
}
