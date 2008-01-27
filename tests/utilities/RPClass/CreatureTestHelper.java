package utilities.RPClass;

import utilities.PlayerTestHelper;
import marauroa.common.game.RPClass;
import games.stendhal.server.entity.creature.Creature;

public class CreatureTestHelper {

	public static void generateRPClasses() {
		
		PlayerTestHelper.generateNPCRPClasses();
		
		if (!RPClass.hasRPClass("creature")) {
			Creature.generateRPClass();
		}

	}

}
