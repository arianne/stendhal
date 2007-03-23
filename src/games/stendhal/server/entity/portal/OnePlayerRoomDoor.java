package games.stendhal.server.entity.portal;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;

/**
 * A door to a zone which only one player may enter.
 *
 * @author hendrik
 */
public class OnePlayerRoomDoor extends Door {

	/**
	 * Tries periodicly to open the door. (Just in case the player left
	 * zone event did not get fired).
	 */
	private class PeriodicOpener implements TurnListener {

		public void onTurnReached(int currentTurn, String message) {
			if (!isOpen()) {
				if (mayBeOpened(null)) {
					open();
				}
			}
			TurnNotifier.get().notifyInTurns(60, this, null);
		}

	}

	/**
	 * Creates a new OnePlayerRoomDoor
	 *
	 * @param clazz clazz
	 * @param dir   direction
	 */
	public OnePlayerRoomDoor(String clazz, Direction dir) {
		super(clazz, dir);
		TurnNotifier.get().notifyInTurns(60, new PeriodicOpener(), null);
	}

	@Override
	protected boolean mayBeOpened(Player player) {
		StendhalRPWorld world = StendhalRPWorld.get();
		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(super.getDestinationZone());
		return (zone.getPlayerAndFirends().size() == 0);
	}
}
