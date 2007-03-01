package games.stendhal.server.maps.semos;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.maps.ZoneConfigurator;

import java.util.Map;

import marauroa.common.game.IRPZone;

/**
 * Semos Jail  - Outside
 * 
 * @author hendrik
 */
public class SemosJailWestOutside implements ZoneConfigurator {
	private NPCList npcs = NPCList.get();

	/**
	 * Build the Semos jail areas
	 */
	public void build() {
		StendhalRPWorld world = StendhalRPWorld.get();

		configureZone(
			(StendhalRPZone) world.getRPZone(
				new IRPZone.ID("0_semos_plains_w")),
			java.util.Collections.EMPTY_MAP);
	}


	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		buildPortals(zone);
	}


	/*
	 * Outside
	 */
	private void buildPortals(StendhalRPZone zone) {
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(86);
		portal.setY(26);
		portal.setReference(new Integer(0));
		portal.setDestination("-1_semos_jail", 0);
		zone.addPortal(portal);
	}
}
