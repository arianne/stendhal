/* $Id$ */
package games.stendhal.server.script;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.scripting.ScriptImpl;

import java.util.List;

import marauroa.common.game.IRPZone;

/**
 * Creates portals in orril that are missing.
 *
 * @author hendrik
 */
public class OrrilPortalWorkaround extends ScriptImpl {

	@Override
	public void execute(Player admin, List<String> args) {
		Portal portal;
		Portal portalDestination;
		StendhalRPZone zone1 = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID("-2_orril_dwarf_mine"));
		StendhalRPZone zone2 = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID("-3_orril_dwarf_blacksmith"));

		portal = new Portal();
		zone1.assignRPObjectID(portal);
		portal.setX(44);
		portal.setY(112);
		portal.setNumber(99);
		portal.setDestination("-3_orril_dwarf_blacksmith", 99);
		zone1.add(portal);

		portal = new Portal();
		zone1.assignRPObjectID(portal);
		portal.setX(44);
		portal.setY(110);
		portal.setNumber(99);
		portal.setDestination("-3_orril_dwarf_blacksmith", 99);
		zone1.add(portal);

		portal = new Portal();
		zone1.assignRPObjectID(portal);
		portal.setX(43);
		portal.setY(111);
		portal.setNumber(99);
		portal.setDestination("-3_orril_dwarf_blacksmith", 99);
		zone1.add(portal);

		portal = new Portal();
		zone1.assignRPObjectID(portal);
		portal.setX(45);
		portal.setY(111);
		portal.setNumber(99);
		portal.setDestination("-3_orril_dwarf_blacksmith", 99);
		zone1.add(portal);

		portalDestination = new Portal();
		zone2.assignRPObjectID(portalDestination);
		portalDestination.setX(5);
		portalDestination.setY(9);
		portalDestination.setNumber(99);
		portalDestination.setDestination("-2_orril_dwarf_mine", 99);
		zone2.add(portalDestination);

		
	}

}
