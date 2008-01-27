/**
 *
 */
package games.stendhal.server.maps;

import utilities.PlayerTestHelper;
import utilities.RPClass.ItemTestHelper;
import utilities.RPClass.PassiveEntityRespawnPointTestHelper;
import games.stendhal.server.core.engine.StendhalRPWorld;
import marauroa.common.game.RPObject;

public class MockStendlRPWorld extends StendhalRPWorld {

	@Override
	public void modify(RPObject object) {
	}

	
	@Override
	protected void createRPClasses() {
		PassiveEntityRespawnPointTestHelper.generateRPClasses();
		ItemTestHelper.generateRPClasses();
		PlayerTestHelper.generateNPCRPClasses();
		
	}
	
	public static StendhalRPWorld get() {
		if (!(instance instanceof MockStendlRPWorld)) {
			instance = new MockStendlRPWorld();
			((MockStendlRPWorld) instance).createRPClasses();
		}
		return instance;
	}

	
	
	@Override
	protected void initialize() {

	}
	
	public static void  reset() {
		instance = null;
	}

}
