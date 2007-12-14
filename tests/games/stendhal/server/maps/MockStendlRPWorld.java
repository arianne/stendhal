/**
 *
 */
package games.stendhal.server.maps;

import games.stendhal.server.core.engine.StendhalRPWorld;
import marauroa.common.game.RPObject;

public class MockStendlRPWorld extends StendhalRPWorld {

	@Override
	public void modify(RPObject object) {
	}

	public static StendhalRPWorld get() {
		if (!(instance instanceof MockStendlRPWorld)) {
			instance = new MockStendlRPWorld();
		}
		return instance;
	}

	@Override
	protected void initialize() {

	}

}
