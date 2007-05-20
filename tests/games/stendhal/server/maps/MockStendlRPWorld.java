/**
 * 
 */
package games.stendhal.server.maps;

import games.stendhal.server.StendhalRPWorld;
import marauroa.common.game.RPObject;
import marauroa.server.game.NoRPZoneException;

public class MockStendlRPWorld extends StendhalRPWorld{

	@Override
	public void modify(RPObject object) throws NoRPZoneException {
		
	}
	
	public static StendhalRPWorld get() {
		if (instance == null) {
			instance = new MockStendlRPWorld();
		}
		return instance;
	}
}