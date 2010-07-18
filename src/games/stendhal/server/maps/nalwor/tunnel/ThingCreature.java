package games.stendhal.server.maps.nalwor.tunnel;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.ItemGuardCreature;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;

import java.util.Map;

/**
 * Configure Drow Tunnel -1 to include a Thing Creature who carries an amulet. 
 * Then it should give an amulet that is bound to the player,
 * provided that the quest KillDarkElves is not completed.
 */
public class ThingCreature implements ZoneConfigurator {

	
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildDrowTunnelArea(zone, attributes);
	}

	private void buildDrowTunnelArea(final StendhalRPZone zone, final Map<String, String> attributes) {
		final EntityManager manager = SingletonRepository.getEntityManager();
		final Creature creature = new ItemGuardCreature(manager.getCreature("thing"), "amulet", "kill_dark_elves", null);
		final CreatureRespawnPoint point = new CreatureRespawnPoint(zone, 32, 5, creature, 1);
		zone.add(point);
	}
}
