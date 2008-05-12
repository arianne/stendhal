package games.stendhal.server.maps.nalwor.tunnel;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.ItemGuardCreature;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;

import java.util.Map;

public class DiscipleAssassinCreature implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 * 
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */

	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildAssassinIdGuy(zone, attributes);
	}

	private void buildAssassinIdGuy(StendhalRPZone zone, Map<String, String> attributes) {
		EntityManager manager = (EntityManager) SingletonRepository.getEntityManager();

		Creature creature = new ItemGuardCreature(manager.getCreature("disciple assassin"), "assassins id");

		CreatureRespawnPoint point = new CreatureRespawnPoint(zone, 242, 41, creature, 1);

		zone.add(point);
	}
}
