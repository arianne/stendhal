package utilities.RPClass;


import games.stendhal.server.entity.creature.Pet;
import marauroa.common.game.RPClass;

public class PetTestHelper {
	public static void generateRPClasses() {
		CreatureTestHelper.generateRPClasses();
		if (!RPClass.hasRPClass("pet")) {
			Pet.generateRPClass();
		}

	}
	
	

}
