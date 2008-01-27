package utilities.RPClass;


import games.stendhal.server.entity.mapstuff.office.ArrestWarrant;
import marauroa.common.game.RPClass;

public class ArrestWarrentTestHelper {
	public static void generateRPClasses() {
		EntityTestHelper.generateRPClasses();
		if (!RPClass.hasRPClass(ArrestWarrant.RPCLASS_NAME)) {
			ArrestWarrant.generateRPClass();
		}

	}

}
