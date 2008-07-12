package games.stendhal.server.maps.athor.cave;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.ItemGuardCreature;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;

import java.util.Map;

public class MinotaurCreature implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 * 
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */

	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildQuicksandArea(zone, attributes);
	}

	private void buildQuicksandArea(final StendhalRPZone zone, final Map<String, String> attributes) {
		final EntityManager manager = SingletonRepository.getEntityManager();

		final Creature creature = new ItemGuardCreature(manager.getCreature("minotaur"), "minotaur key");

		final CreatureRespawnPoint point = new CreatureRespawnPoint(zone, 121, 121, creature, 1);

		zone.add(point);
	}
}
