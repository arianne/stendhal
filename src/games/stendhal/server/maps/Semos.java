package games.stendhal.server.maps;

import games.stendhal.server.maps.semos.SemosCityInside;
import games.stendhal.server.maps.semos.SemosCityInsideTavern;
import games.stendhal.server.maps.semos.SemosCityOutside;
import games.stendhal.server.maps.semos.SemosJailWest;
import games.stendhal.server.maps.semos.SemosPlainsNorth;
import games.stendhal.server.maps.semos.SemosPlainsNorthEast;
import games.stendhal.server.maps.semos.SemosPlainsSouth;
import games.stendhal.server.maps.semos.SemosVillageWest;

public class Semos implements IContent {

	public Semos() {
		// sorted form north west to south east
		new SemosPlainsNorth().build();
		new SemosPlainsNorthEast().build();

		new SemosJailWest().build();
		new SemosVillageWest().build();

		new SemosCityInside().build();
		new SemosCityInsideTavern().build();
		new SemosCityOutside().build();


		new SemosPlainsSouth().build();
	}

}