package games.stendhal.server.core.config.zone;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;

import java.util.Map;

public class NoTeleportIn implements ZoneConfigurator {

	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		zone.disallowIn();

	}

}
