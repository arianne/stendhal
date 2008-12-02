package games.stendhal.server.core.events;

import marauroa.common.game.RPObject;
import games.stendhal.server.core.engine.StendhalRPZone;

public interface ZoneEnterExitListener {
	/**
	 * Invoked when an entity enters the object area.
	 * 
	 * @param object
	 *            The object that entered.
	 * @param zone
	 *            The new zone.
	 */
	void onEntered(RPObject object, StendhalRPZone zone);

	/**
	 * Invoked when an entity leaves the object area.
	 * 
	 * @param object
	 *            The object that exited.
	 * @param zone
	 *            The zone that was exited.
	 * 
	 */
	void onExited(RPObject object, StendhalRPZone zone);

}
