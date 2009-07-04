/**
 *
 */
package games.stendhal.server.maps;

import games.stendhal.server.core.engine.RPClassGenerator;
import games.stendhal.server.core.engine.StendhalRPWorld;
import marauroa.common.game.RPObject;

public class MockStendlRPWorld extends StendhalRPWorld {

	@Override
	public void modify(final RPObject object) {
	}

	
	
	protected void createRPClasses() {
		
		new RPClassGenerator().createRPClasses();
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
