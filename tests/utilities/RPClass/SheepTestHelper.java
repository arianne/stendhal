package utilities.RPClass;

import games.stendhal.server.entity.creature.Sheep;
import marauroa.common.game.RPClass;

public class SheepTestHelper {
	public static void generateRPClasses() {
	
		CreatureTestHelper.generateRPClasses();
		if (!RPClass.hasRPClass("sheep")) {
			Sheep.generateRPClass();
		}

	}

}
