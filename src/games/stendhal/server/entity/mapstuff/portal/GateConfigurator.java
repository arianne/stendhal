package games.stendhal.server.entity.mapstuff.portal;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ChatCondition;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.util.Map;

import org.codehaus.groovy.control.CompilationFailedException;

public class GateConfigurator implements ZoneConfigurator {
	public void configureZone(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		final int x = MathHelper.parseInt(attributes.get("x"));
		final int y = MathHelper.parseInt(attributes.get("y"));
		final String orientation = attributes.get("orientation");
		final String image = attributes.get("image");
		
		ChatCondition condition = null;
		final String condString = attributes.get("condition");
		if (condString != null) {
			final GroovyShell interp = new GroovyShell(new Binding());
			String code = "import games.stendhal.server.entity.npc.condition.*;\r\n"
				+ condString;
			try {
				condition = (ChatCondition) interp.evaluate(code);
			} catch (CompilationFailedException e) {
				throw new IllegalArgumentException(e);
			}
		}

		buildGate(zone, x, y, orientation, image, condition);
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
			final String orientation, final String image, ChatCondition condition) {
		final Gate gate = new Gate(orientation, image, condition);
		
		gate.setPosition(x, y);
		zone.add(gate);
	}
}
