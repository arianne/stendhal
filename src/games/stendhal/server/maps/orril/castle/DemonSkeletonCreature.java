package games.stendhal.server.maps.orril.castle;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rule.defaultruleset.DefaultEntityManager;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.ItemGuardCreature;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;

import java.util.Map;

/**
 * Configure Orril Lich Palace (Underground/Level -2).
 */
public class DemonSkeletonCreature implements ZoneConfigurator {

	DefaultEntityManager manager = (DefaultEntityManager) StendhalRPWorld.get().getRuleManager().getEntityManager();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildLichPalace(zone, attributes);
	}

	private void buildLichPalace(StendhalRPZone zone, Map<String, String> attributes) {
		Creature creature = new ItemGuardCreature(manager.getCreature("demon_skeleton"), "lich_gold_key");

		CreatureRespawnPoint point = new CreatureRespawnPoint(zone, 54, 48, creature, 1);

		zone.add(point);
	}
}
