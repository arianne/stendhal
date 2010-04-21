package games.stendhal.server.entity.mapstuff.portal;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;

import java.util.Map;

public class GateConfigurator implements ZoneConfigurator {
	public void configureZone(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		final int x = MathHelper.parseInt(attributes.get("x"));
		final int y = MathHelper.parseInt(attributes.get("y"));
		final String orientation = attributes.get("orientation");
		final String image = attributes.get("image");

		buildGate(zone, x, y, orientation, image);
	}
	
	/**
	 * Create the gate
	 * 
	 * @param zone the zone to add the gate to
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param orientation gate orientation
	 */
	private void buildGate(final StendhalRPZone zone, final int x, final int y, 
			final String orientation, final String image) {
		final Gate gate = new Gate(orientation, image);
		
		gate.setPosition(x, y);
		zone.add(gate);
	}
}
