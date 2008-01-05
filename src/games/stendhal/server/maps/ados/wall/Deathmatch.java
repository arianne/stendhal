package games.stendhal.server.maps.ados.wall;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.maps.quests.AdosDeathmatch;
import games.stendhal.server.util.Area;

import java.awt.geom.Rectangle2D;
import java.util.Map;

/**
 * Ados Wall North population - Deathmatch.
 *
 * @author hendrik
 */
public class Deathmatch implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		Rectangle2D shape = new Rectangle2D.Double();
		shape.setRect(88, 77, 112 - 88 + 1, 93 - 77 + 1);
		Area arena = new Area(zone, shape);
		AdosDeathmatch deathmatch = new AdosDeathmatch(zone, arena);
		deathmatch.createHelmet(102, 75);
		deathmatch.createNPC("Thanatos", 98, 77);
	}
}
