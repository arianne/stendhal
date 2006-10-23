package games.stendhal.server.entity.portal;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Player;

/**
 * A door to a zone which only one player may enter.
 *
 * @author hendrik
 */
public class OnePlayerRoomDoor extends Door {

	/**
	 * Creates a new OnePlayerRoomDoor
	 *
	 * @param clazz clazz
	 * @param dir   direction
	 */
	public OnePlayerRoomDoor(String clazz, Direction dir) {
		super(clazz, dir);
	}

	@Override
	protected boolean mayBeOpend(Player player) {
		StendhalRPWorld world = StendhalRPWorld.get();
		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(super.getDestinationZone());
		return (zone.getPlayerAndFirends().size() == 0);
	}
}
