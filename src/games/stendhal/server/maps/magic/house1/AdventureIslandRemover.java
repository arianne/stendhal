package games.stendhal.server.maps.magic.house1;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;

/**
 * removes the island
 *
 */
public class AdventureIslandRemover implements TurnListener {
	private StendhalRPZone zone;

	/**
	 * creates a new AdventureIslandRemover
	 *
	 * @param zone StendhalRPZone 
	 */
	public AdventureIslandRemover(StendhalRPZone zone) {
		this.zone = zone;
	}

	public void onTurnReached(int currentTurn) {
		if (zone.getPlayers().size()==0) {
			SingletonRepository.getRPWorld().removeZone(zone);
		} 
	}

}
