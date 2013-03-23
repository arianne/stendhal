package games.stendhal.server.entity.mapstuff.block;

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;

public class BlockTargetZoneConfigurator implements ZoneConfigurator {

	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		int x = Integer.parseInt(attributes.get("x"));
		int y = Integer.parseInt(attributes.get("y"));
		BlockTarget blockTarget = new BlockTarget(x, y);
		zone.add(blockTarget);
		zone.addMovementListener(blockTarget);
	}

}
