package utilities.RPClass;

import games.stendhal.server.entity.trade.Offer;
import marauroa.common.game.RPClass;

public class OfferTestHelper {

	public static void generateRPClasses() {
		EntityTestHelper.generateRPClasses();
		if (!RPClass.hasRPClass("offer")) {
			Offer.generateRPClass();
		}
	}

}
