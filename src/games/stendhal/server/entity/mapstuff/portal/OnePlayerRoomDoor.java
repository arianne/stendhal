package games.stendhal.server.entity.mapstuff.portal;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.RPEntity;

/**
 * A door to a zone which only one player may enter.
 * 
 * @author hendrik
 */
public class OnePlayerRoomDoor extends Door {

	/**
	 * Tries periodically to open the door. (Just in case the player left zone
	 * event did not get fired).
	 */
	class PeriodicOpener implements TurnListener {

		public void onTurnReached(int currentTurn) {
			if (!isOpen()) {
				if (isAllowed(null)) {
					open();
				}
			}
			SingletonRepository.getTurnNotifier().notifyInTurns(60, this);
		}

	}

	/**
	 * Creates a new OnePlayerRoomDoor.
	 * 
	 * @param clazz
	 *            clazz
	 */
	public OnePlayerRoomDoor(String clazz) {
		super(clazz);
		SingletonRepository.getTurnNotifier().notifyInTurns(60, new PeriodicOpener());
	}

	@Override
	protected boolean isAllowed(RPEntity user) {
		StendhalRPWorld world = SingletonRepository.getRPWorld();
		StendhalRPZone zone = world.getZone(super.getDestinationZone());
		return (zone.getPlayerAndFriends().size() == 0);
	}
}
