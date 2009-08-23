package games.stendhal.server.maps.magic.house1;

import marauroa.common.game.RPObject;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.item.Corpse;

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

}
