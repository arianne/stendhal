package utilities.RPClass;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import games.stendhal.server.entity.mapstuff.spawner.GrowingPassiveEntityRespawnPoint;
import marauroa.common.game.RPClass;

public class GrowingPassiveEntityRespawnPointTestHelper {

	public static void generateRPClasses() {

		if (!RPClass.hasRPClass("growing_entity_spawner")) {
			PassiveEntityRespawnPointTestHelper.generateRPClasses();
			GrowingPassiveEntityRespawnPoint.generateRPClass();
		}
	}
	
	@Test
	public void testname() throws Exception {
		generateRPClasses();
		assertTrue(RPClass.hasRPClass("growing_entity_spawner"));

	}
}
