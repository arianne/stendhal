package games.stendhal.server.maps;

import games.stendhal.server.maps.ados.AdosMountainsNorthWest;
import games.stendhal.server.maps.ados.AdosOutsideNorthWest;
import games.stendhal.server.maps.ados.AdosRock;

public class Ados implements IContent {

	public Ados() {
		new AdosMountainsNorthWest().build();
		new AdosRock().build();
		new AdosOutsideNorthWest().build();

	}



}