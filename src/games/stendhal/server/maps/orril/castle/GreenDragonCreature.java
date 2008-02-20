package games.stendhal.server.maps.orril.castle;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.ItemGuardCreature;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;

import java.util.Map;

/**
 * Configure Orril Castle West (Underground/Level -1).
 */
public class GreenDragonCreature implements ZoneConfigurator {


	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildCastleDungeonArea(zone, attributes);
	}

	private void buildCastleDungeonArea(StendhalRPZone zone, Map<String, String> attributes) {
		EntityManager manager = (EntityManager) SingletonRepository.getEntityManager();
		Creature creature = new ItemGuardCreature(manager.getCreature("green dragon"), "dungeon silver key");
		CreatureRespawnPoint point = new CreatureRespawnPoint(zone, 69, 43, creature, 1);
		zone.add(point);
	}
}
