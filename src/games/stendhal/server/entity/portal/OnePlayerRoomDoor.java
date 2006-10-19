package games.stendhal.server.entity.portal;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;

/**
 * A door to a zone which only one player may enter.
 *
 * @author hendrik
 */
public class OnePlayerRoomDoor extends Door {

	public OnePlayerRoomDoor(String clazz, Direction dir) {
		super(clazz, dir);
	}

	private boolean mayBeOpend() {
		StendhalRPWorld world = StendhalRPWorld.get();
		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(super.getDestinationZone());
		return (zone.getPlayerAndFirends().size() == 0);
	}

	@Override
	public void onUsed(RPEntity user) {
		if (mayBeOpend()) {
			open();
		} else {
			close();
		}
		super.onUsed(user);
	}

	// TODO: implement auto opening and closing of this door
}
