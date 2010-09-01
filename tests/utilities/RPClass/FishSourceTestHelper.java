package utilities.RPClass;

import games.stendhal.server.entity.mapstuff.useable.FishSource;
import marauroa.common.game.RPClass;

public class FishSourceTestHelper {
	public static void generateRPClasses() {
		EntityTestHelper.generateRPClasses();
		if (!RPClass.hasRPClass("fish_source")) {
			FishSource.generateRPClass();
		}

	}
}
