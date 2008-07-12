package games.stendhal.server.maps.semos.kanmararn;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.ItemGuardCreature;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;

import java.util.Map;

/**
 * Configure Kanmararn Prison to include a Duergar King Creature who carries a key. 
 * Then it should give a key that is bound to the player.
 */
public class DuergarKingCreature implements ZoneConfigurator {

	private final EntityManager manager = SingletonRepository.getEntityManager();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildPrisonArea(zone, attributes);
	}

	private void buildPrisonArea(final StendhalRPZone zone, final Map<String, String> attributes) {
		final Creature creature = new ItemGuardCreature(manager.getCreature("duergar king"), "kanmararn prison key");
		final CreatureRespawnPoint point = new CreatureRespawnPoint(zone, 50, 15, creature, 1);
		zone.add(point);
	}
}
