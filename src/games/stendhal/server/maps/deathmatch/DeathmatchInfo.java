package games.stendhal.server.maps.deathmatch;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.util.Area;

public class DeathmatchInfo {
	private final Area arena;
	private final String zoneName;
	private final StendhalRPZone zone;

	public DeathmatchInfo(final Area arena, final String zoneName, final StendhalRPZone zone) {
		super();
		this.arena = arena;
		this.zoneName = zoneName;
		this.zone = zone;
	}

	public Area getArena() {
		return arena;
	}

	public StendhalRPZone getZone() {
		return zone;
	}

	public String getZoneName() {
		return zoneName;
	}
	
}
