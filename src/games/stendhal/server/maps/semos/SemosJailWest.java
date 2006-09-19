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
		zoneSub1SemosJail();
		zoneSub2SemosJail();
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

	private void zoneSub1SemosJail() {
		NPCList npcs = NPCList.get();
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID(
		"-1_semos_jail"));
		SpeakerNPC npc = new SpeakerNPC("Marcus") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(9, 6));
				nodes.add(new Path.Node(21, 6));
				nodes.add(new Path.Node(21, 7));
				nodes.add(new Path.Node(9, 7));
				setPath(nodes, true);
			}
	
			@Override
			protected void createDialog() {
				addGreeting("Greetings! How may I #help you?");
				addJob("I am the jail keeper. You have been confined here because of your bad behaviour.");
				addHelp("Wait for an admin to come here and decide about you. There is meanwhile no exit from here.");
				addGoodbye();
			}
		};
		npcs.add(npc);
	
		zone.assignRPObjectID(npc);
		npc.put("class", "youngsoldiernpc");
		npc.set(9, 6);
		npc.initHP(100);
		zone.addNPC(npc);
	
	}
	
	private void zoneSub2SemosJail() {
		NPCList npcs = NPCList.get();
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID(
		"-2_semos_jail"));
		SpeakerNPC npc = new SpeakerNPC("Sten Tanquilos") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(4, 14));
				nodes.add(new Path.Node(27, 14));
				nodes.add(new Path.Node(27, 17));
				nodes.add(new Path.Node(4, 17));
				setPath(nodes, true);
			}
	
			@Override
			protected void createDialog() {
				addGreeting("Greetings! How may I #help you?");
				addJob("I am the jail keeper. You have been confined here because of your bad behaviour.");
				addHelp("Wait for an admin to come here and decide about you. There is meanwhile no exit from here.");
				addGoodbye();
			}
		};
		npcs.add(npc);
	
		zone.assignRPObjectID(npc);
		npc.put("class", "youngsoldiernpc");
		npc.set(4, 14);
		npc.initHP(100);
		zone.addNPC(npc);
	}
}
