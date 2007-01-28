package games.stendhal.server.maps;

import games.stendhal.server.maps.semos.IL0_Bakery;
import games.stendhal.server.maps.semos.IL0_Bank;
import games.stendhal.server.maps.semos.IL0_Blacksmith;
import games.stendhal.server.maps.semos.IL0_Library;
import games.stendhal.server.maps.semos.IL0_Storage;
import games.stendhal.server.maps.semos.IL0_Temple;
import games.stendhal.server.maps.semos.IL0_Townhall;
import games.stendhal.server.maps.semos.ISL1_Storage;
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

		new IL0_Bakery().build();
		new IL0_Bank().build();
		new IL0_Blacksmith().build();
		new IL0_Library().build();
		new IL0_Storage().build();
		new IL0_Temple().build();
		new IL0_Townhall().build();
		new ISL1_Storage().build();

		new SemosCityTavern0().build();
		new SemosCityTavern1().build();

		new SemosCityOutside().build();


		new SemosPlainsSouth().build();
	}

}
