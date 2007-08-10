package games.stendhal.server.maps.ados.wall;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.maps.quests.AdosDeathmatch;
import games.stendhal.server.util.Area;

import java.awt.geom.Rectangle2D;
import java.util.Map;

/**
 * Ados Wall North population
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
		buildDeathmatch(zone);
	}

	/**
	 * Creatures a soldier telling people a story, why Ados is so empty.
	 *
	 * @param zone StendhalRPZone
	 */

	/**
	 * Creatures the Deathmatch referee.
	 *
	 * @param zone StendhalRPZone
	 */
	private void buildDeathmatch(StendhalRPZone zone) {
		String zoneName = zone.getID().getID();
		Rectangle2D shape = new Rectangle2D.Double();
		shape.setRect(88, 77, 112 - 88 + 1, 93 - 77 + 1);
		Area arena = new Area(zone, shape);
		AdosDeathmatch deathmatch = new AdosDeathmatch(zoneName, zone, arena);
		deathmatch.createHelmet(102, 75);
		deathmatch.createNPC("Thanatos", 98, 76);
	}
}
