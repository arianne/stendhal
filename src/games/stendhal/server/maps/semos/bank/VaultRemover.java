package games.stendhal.server.maps.semos.bank;

import marauroa.common.game.RPObject;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.item.Corpse;

/**
 * removes the vault
 *
 * @author hendrik
 */
public class VaultRemover implements TurnListener {
	private StendhalRPZone zone;

	/**
	 * creates a new VaultRemover
	 *
	 * @param zone StendhalRPZone 
	 */
	public VaultRemover(StendhalRPZone zone) {
		this.zone = zone;
	}

	public void onTurnReached(int currentTurn) {
		// Tell all corpses they are to be removed
		// (stops timers)
		for (RPObject object : zone) {
			if (object instanceof Corpse) {
				((Corpse) object).onRemoved(zone);
			}
		}
		SingletonRepository.getRPWorld().removeZone(zone);
	}

}
