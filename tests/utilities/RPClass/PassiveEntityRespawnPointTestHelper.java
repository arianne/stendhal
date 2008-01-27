package utilities.RPClass;

import games.stendhal.server.entity.mapstuff.spawner.PassiveEntityRespawnPoint;
import marauroa.common.game.RPClass;

public class PassiveEntityRespawnPointTestHelper {

	public static void generateRPClasses() {

		EntityTestHelper.generateRPClasses();
		if (!RPClass.hasRPClass("plant_grower")) {
			PassiveEntityRespawnPoint.generateRPClass();
		}
	}

}
