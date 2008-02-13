package utilities.RPClass;

import games.stendhal.server.entity.creature.Cat;
import marauroa.common.game.RPClass;

public class CatTestHelper {
	public static void generateRPClasses() {
		PetTestHelper.generateRPClasses();

		if (!RPClass.hasRPClass("cat")) {
			Cat.generateRPClass();
		}
	}
}
