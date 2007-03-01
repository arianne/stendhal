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
 * Configure Orril Castle West (Underground/Level -1).
 */
public class USL1_CastleWest implements ZoneConfigurator {
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
		buildCastleDungeonArea(zone, attributes);
	}


	private void buildCastleDungeonArea(StendhalRPZone zone,
	 Map<String, String> attributes) {
		Creature creature = new ItemGuardCreature(manager
				.getCreature("green_dragon"), "dungeon_silver_key");
		CreatureRespawnPoint point = new CreatureRespawnPoint(zone, 69, 43, creature, 1);
		zone.addRespawnPoint(point);


		/*
		 * Portals configured in xml?
		 */
		if(attributes.get("xml-portals") == null) {
			Portal door = new LockedDoor("dungeon_silver_key", "skulldoor", Direction.DOWN);
			zone.assignRPObjectID(door);
			door.set(69, 37);
			door.setReference(new Integer(0));
			door.setDestination("-2_orril_lich_palace", 0);
			zone.addPortal(door);
		}
	}
}
