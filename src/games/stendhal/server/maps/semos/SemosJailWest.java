package games.stendhal.server.maps.semos;

import games.stendhal.common.Direction;
import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.actions.AdministrationAction;
import games.stendhal.server.entity.Chest;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.Sign;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.portal.OneWayPortalDestination;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.pathfinder.Path;

import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.IRPZone;

public class SemosJailWest {
	public void build() {

		buildPortals();
	}

	private void buildPortals() {
		StendhalRPWorld world = StendhalRPWorld.get();
		StendhalRPZone zoneOutside = (StendhalRPZone) world.getRPZone(new IRPZone.ID("0_semos_plains_w"));
		StendhalRPZone sub1semosJail = (StendhalRPZone) world.getRPZone(new IRPZone.ID("-1_semos_jail"));
		Portal portal = new Portal();
		zoneOutside.assignRPObjectID(portal);
		portal.setX(86);
		portal.setY(25);
		portal.setNumber(0);
		portal.setDestination("-1_semos_jail", 0);
		zoneOutside.addPortal(portal);
		
		portal = new Portal();
		sub1semosJail.assignRPObjectID(portal);
		portal.setX(28);
		portal.setY(17);
		portal.setNumber(0);
		portal.setDestination("0_semos_plains_w", 0);
		sub1semosJail.addPortal(portal);
	}
}
