package games.stendhal.server.maps.orril.castle;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.ItemGuardCreature;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;
import games.stendhal.server.rule.defaultruleset.DefaultEntityManager;

import java.util.Map;

/**
 * Configure Orril Castle West (Underground/Level -1).
 */
public class GreenDragonCreature implements ZoneConfigurator {

	DefaultEntityManager manager = (DefaultEntityManager) StendhalRPWorld.get().getRuleManager().getEntityManager();

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
		Creature creature = new ItemGuardCreature(manager.getCreature("green_dragon"), "dungeon_silver_key");
		CreatureRespawnPoint point = new CreatureRespawnPoint(zone, 69, 43, creature, 1);
		zone.add(point);
	}
}
