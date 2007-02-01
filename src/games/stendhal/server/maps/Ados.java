package games.stendhal.server.maps;

import java.util.Map;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.maps.ados.AdosCityInside;
import games.stendhal.server.maps.ados.AdosCityOutside;
import games.stendhal.server.maps.ados.AdosMountainsNorthWest;
import games.stendhal.server.maps.ados.AdosOutsideNorthWest;
import games.stendhal.server.maps.ados.AdosRock;
import games.stendhal.server.maps.ados.AdosSwamp;
import games.stendhal.server.maps.ados.AdosWallNorth;

public class Ados implements ZoneConfigurator, IContent {

	public Ados() {
	}


	public void build() {
		configureZone(null, null);
	}


	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		new AdosCityOutside().build();
		new AdosCityInside().build();
		new AdosMountainsNorthWest().build();
		new AdosRock().build();
		new AdosOutsideNorthWest().build();
		new AdosWallNorth().build();
		new AdosSwamp().build();
	}
}
