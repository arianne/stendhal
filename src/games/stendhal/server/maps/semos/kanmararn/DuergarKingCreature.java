package games.stendhal.server.maps.semos.kanmararn;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.ItemGuardCreature;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;
import games.stendhal.server.rule.defaultruleset.DefaultEntityManager;

import java.util.Map;

/**
 * Configure Kanmararn Prison to include a Duergar King Creature who carries a key. 
 * Then it should give a key that is bound to the player.
 */
public class DuergarKingCreature implements ZoneConfigurator {

	DefaultEntityManager manager = (DefaultEntityManager) StendhalRPWorld.get().getRuleManager().getEntityManager();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildPrisonArea(zone, attributes);
	}

	private void buildPrisonArea(StendhalRPZone zone, Map<String, String> attributes) {
		Creature creature = new ItemGuardCreature(manager.getCreature("duergar_king"), "kanmararn_prison_key");
		CreatureRespawnPoint point = new CreatureRespawnPoint(zone, 50, 15, creature, 1);
		zone.add(point);
	}
}
