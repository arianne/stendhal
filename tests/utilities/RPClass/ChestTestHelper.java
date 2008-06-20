package utilities.RPClass;

import games.stendhal.server.entity.mapstuff.chest.Chest;
import marauroa.common.game.RPClass;

public class ChestTestHelper {
	public static void generateRPClasses() {
		ItemTestHelper.generateRPClasses();
		if (!RPClass.hasRPClass("chest")) {
			Chest.generateRPClass();
		}

	}
}
