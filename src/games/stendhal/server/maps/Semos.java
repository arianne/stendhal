package games.stendhal.server.maps;

import games.stendhal.server.maps.semos.SemosCityInside;
import games.stendhal.server.maps.semos.SemosCityTavern0;
import games.stendhal.server.maps.semos.SemosCityTavern1;
import games.stendhal.server.maps.semos.SemosCityOutside;
import games.stendhal.server.maps.semos.SemosJailWestOutside;
import games.stendhal.server.maps.semos.SemosJailWestSL1;
import games.stendhal.server.maps.semos.SemosJailWestSL2;
import games.stendhal.server.maps.semos.SemosPlainsNorth;
import games.stendhal.server.maps.semos.SemosPlainsNorthEast;
import games.stendhal.server.maps.semos.SemosPlainsSouth;
import games.stendhal.server.maps.semos.SemosVillageWest;

public class Semos implements IContent {

	public Semos() {
		// sorted form north west to south east
		new SemosPlainsNorth().build();
		new SemosPlainsNorthEast().build();

		new SemosJailWestOutside().build();
		new SemosJailWestSL1().build();
		new SemosJailWestSL2().build();

		new SemosVillageWest().build();

		new SemosCityInside().build();

		new SemosCityTavern0().build();
		new SemosCityTavern1().build();

		new SemosCityOutside().build();


		new SemosPlainsSouth().build();
	}

}
