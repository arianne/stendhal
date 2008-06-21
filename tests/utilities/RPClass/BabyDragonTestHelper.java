package utilities.RPClass;

import games.stendhal.server.entity.creature.BabyDragon;
import marauroa.common.game.RPClass;

public class BabyDragonTestHelper {
	
	
public static void generateRPClasses() {
		
		PetTestHelper.generateRPClasses();
		
		if (!RPClass.hasRPClass("baby_dragon")) {
			BabyDragon.generateRPClass();
		}

	}

}
