package games.stendhal.server.maps;

import games.stendhal.server.maps.ados.AdosCityInside;
import games.stendhal.server.maps.ados.AdosCityOutside;
import games.stendhal.server.maps.ados.AdosMountainsNorthWest;
import games.stendhal.server.maps.ados.AdosOutsideNorthWest;
import games.stendhal.server.maps.ados.AdosRock;
import games.stendhal.server.maps.ados.AdosSwamp;
import games.stendhal.server.maps.ados.Deathmatch;

public class Ados implements IContent {

	public Ados() {
		new AdosCityOutside().build();
		new AdosCityInside().build();
		new AdosMountainsNorthWest().build();
		new AdosRock().build();
		new AdosOutsideNorthWest().build();
		new AdosSwamp().build();
		
		new Deathmatch().build();
	}



}