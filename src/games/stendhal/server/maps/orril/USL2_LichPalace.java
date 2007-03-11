package games.stendhal.server.maps.orril;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.ItemGuardCreature;
import games.stendhal.server.entity.portal.LockedDoor;
import games.stendhal.server.entity.spawner.CreatureRespawnPoint;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.rule.defaultruleset.DefaultEntityManager;

import java.util.Map;

/**
 * Configure Orril Lich Palace (Underground/Level -2).
 */
public class USL2_LichPalace implements ZoneConfigurator {
	DefaultEntityManager manager = (DefaultEntityManager)
		StendhalRPWorld.get().getRuleManager().getEntityManager();


	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		buildLichPalace(zone, attributes);
	}


	private void buildLichPalace(StendhalRPZone zone,
	 Map<String, String> attributes) {
		Creature creature = new ItemGuardCreature(
			manager.getCreature("royal_mummy"), "lich_gold_key");

		CreatureRespawnPoint point =
			new CreatureRespawnPoint(zone, 54, 48, creature, 1);

		zone.addRespawnPoint(point);
	}
}
