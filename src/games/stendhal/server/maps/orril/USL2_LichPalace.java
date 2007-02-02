package games.stendhal.server.maps.orril;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.ItemGuardCreature;
import games.stendhal.server.entity.portal.LockedDoor;
import games.stendhal.server.entity.portal.Portal;
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

		/*
		 * Portals configured in xml?
		 */
		if(attributes.get("xml-portals") == null) {
			Portal portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.set(70, 38);
			portal.setNumber(0);
			portal.setDestination("-1_orril_castle_w", 0);
			zone.addPortal(portal);

			Portal door = new LockedDoor("lich_gold_key", "skulldoor", Direction.UP);
			zone.assignRPObjectID(door);
			door.set(54, 52);
			door.setNumber(1);
			door.setDestination("-2_orril_lich_palace", 2);
			zone.addPortal(door);

			portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.set(54, 57);
			portal.setNumber(2);
			portal.setDestination("-2_orril_lich_palace", 1);
			zone.addPortal(portal);

			portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.set(55, 57);
			portal.setNumber(3);
			portal.setDestination("-2_orril_lich_palace", 1);
			zone.addPortal(portal);
		}
	}
}
