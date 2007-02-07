package games.stendhal.server.maps;

import java.util.Map;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.maps.ados.AdosCityOutside;
import games.stendhal.server.maps.ados.AdosMountainsNorthWest;
import games.stendhal.server.maps.ados.AdosOutsideNorthWest;
import games.stendhal.server.maps.ados.AdosRock;
import games.stendhal.server.maps.ados.AdosSwamp;
import games.stendhal.server.maps.ados.AdosWallNorth;
import games.stendhal.server.maps.ados.IL0_Bakery;
import games.stendhal.server.maps.ados.IL0_Bank;
import games.stendhal.server.maps.ados.IL0_HauntedHouse;
import games.stendhal.server.maps.ados.IL0_Library;
import games.stendhal.server.maps.ados.IL0_MagicianHouse;
import games.stendhal.server.maps.ados.IL0_Tavern;
import games.stendhal.server.maps.ados.IL0_Temple;


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

		new IL0_Bakery().build();
		new IL0_Bank().build();
		new IL0_HauntedHouse().build();
		new IL0_Library().build();
		new IL0_Tavern().build();
		new IL0_Temple().build();

		new AdosMountainsNorthWest().build();
		new IL0_MagicianHouse().build();

		new AdosRock().build();
		new AdosOutsideNorthWest().build();
		new AdosWallNorth().build();
		new AdosSwamp().build();
	}
}
