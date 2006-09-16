package games.stendhal.server.maps;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.maps.semos.SemosCityInside;
import games.stendhal.server.maps.semos.SemosCityInsideTavern;
import games.stendhal.server.maps.semos.SemosCityOutside;
import games.stendhal.server.maps.semos.SemosPlainsNorth;
import games.stendhal.server.maps.semos.SemosPlainsNorthEast;
import games.stendhal.server.maps.semos.SemosPlainsSouth;
import games.stendhal.server.maps.semos.SemosVillageWest;

public class Semos implements IContent {

	public Semos() {
		new SemosCityOutside().build();
		new SemosCityInsideTavern().build();
		
		new SemosVillageWest().build();
		new SemosPlainsNorth().build();
		new SemosPlainsNorthEast().build();
		new SemosPlainsSouth().build();
		new SemosCityInside().build();
	}

}