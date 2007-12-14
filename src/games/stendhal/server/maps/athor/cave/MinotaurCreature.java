package games.stendhal.server.maps.athor.cave;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.ItemGuardCreature;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;
import games.stendhal.server.rule.defaultruleset.DefaultEntityManager;

import java.util.Map;

public class MinotaurCreature implements ZoneConfigurator {

	DefaultEntityManager manager = (DefaultEntityManager) StendhalRPWorld.get().getRuleManager().getEntityManager();

	/**
	 * Configure a zone.
	 * 
	 * @param zone
	 *            The zone to be configured.
	 * @param attributes
	 *            Configuration attributes.
	 */

	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildQuicksandArea(zone, attributes);
	}

	private void buildQuicksandArea(StendhalRPZone zone,
			Map<String, String> attributes) {
		Creature creature = new ItemGuardCreature(
				manager.getCreature("minotaur"), "minotaur_key");

		CreatureRespawnPoint point = new CreatureRespawnPoint(zone, 121, 121,
				creature, 1);

		zone.add(point);

	}
}
