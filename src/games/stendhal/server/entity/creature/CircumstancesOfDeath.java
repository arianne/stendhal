package games.stendhal.server.entity.creature;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;

/**
 * Contains statistics for notifications
 */
public class CircumstancesOfDeath {
	public RPEntity killer;
// 	public List<RPEntity> killers;
	public RPEntity victim;
	public StendhalRPZone zone;
}
