package utilities.RPClass;

import games.stendhal.server.entity.Blood;
import marauroa.common.game.RPClass;

public class BloodTestHelper {
	public static void generateRPClasses() {
		EntityTestHelper.generateRPClasses();
		if (!RPClass.hasRPClass("blood")) {
			Blood.generateRPClass();
		}

	}
}
