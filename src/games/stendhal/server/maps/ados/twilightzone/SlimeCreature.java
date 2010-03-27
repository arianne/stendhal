package games.stendhal.server.maps.ados.twilightzone;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.ItemGuardCreature;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;

import java.util.Map;

/**
 * Configure twilight zone to include a slime Creature which drops twilight elixir. 
 * 
 */
public class SlimeCreature implements ZoneConfigurator {

	
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildTwilightArea(zone, attributes);
	}

	private void buildTwilightArea(final StendhalRPZone zone, final Map<String, String> attributes) {
		final EntityManager manager = SingletonRepository.getEntityManager();
		final Creature creature = new ItemGuardCreature(manager.getCreature("twilight slime"), "twilight elixir", "mithril_cloak", "twilight_zone");
		final CreatureRespawnPoint point = new CreatureRespawnPoint(zone, 5, 5, creature, 1);
		zone.add(point);
	}
}
