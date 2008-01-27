package utilities.RPClass;

import marauroa.common.game.RPClass;
import games.stendhal.server.entity.mapstuff.spawner.SheepFood;



public class SheepFoodTestHelper {
	public static void generateRPClasses() {
		
		if (!RPClass.hasRPClass("food")) {
			SheepFood.generateRPClass();
		}

	}
}
