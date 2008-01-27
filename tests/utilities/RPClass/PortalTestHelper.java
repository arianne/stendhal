package utilities.RPClass;

import games.stendhal.server.entity.mapstuff.portal.Portal;
import marauroa.common.game.RPClass;

public class PortalTestHelper {
	public static void generateRPClasses() {
		EntityTestHelper.generateRPClasses();
		if (!RPClass.hasRPClass("portal")) {
			Portal.generateRPClass();
		}

	}
}
