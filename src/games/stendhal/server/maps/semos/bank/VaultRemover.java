package games.stendhal.server.maps.semos.bank;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;

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
		SingletonRepository.getRPWorld().removeZone(zone);
	}

}
