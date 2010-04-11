package games.stendhal.server.core.config.zone;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;

import java.util.Map;

public class NoTeleportOut implements ZoneConfigurator {

	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		if ((attributes != null) && attributes.containsKey("x")) {
			int x = Integer.parseInt(attributes.get("x"));
			int y = Integer.parseInt(attributes.get("y"));
			int width = Integer.parseInt(attributes.get("width"));
			int height = Integer.parseInt(attributes.get("height"));
			
			zone.disallowOut(x, y, width, height);
		} else {
			zone.disallowOut();
		}
	}
}
