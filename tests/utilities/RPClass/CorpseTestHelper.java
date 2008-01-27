package utilities.RPClass;

import games.stendhal.server.entity.item.Corpse;
import marauroa.common.game.RPClass;

public class CorpseTestHelper {
	public static void generateRPClasses() {
		ItemTestHelper.generateRPClasses();
		if (!RPClass.hasRPClass("corpse")) {
			Corpse.generateRPClass();
		}

	}
}
