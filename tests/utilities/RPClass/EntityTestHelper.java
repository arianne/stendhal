package utilities.RPClass;

import games.stendhal.server.entity.Entity;
import marauroa.common.game.RPClass;

public class EntityTestHelper {
	public static void generateRPClasses() {

		if (!RPClass.hasRPClass("entity")) {
			Entity.generateRPClass();
		}

	}
}
