package games.stendhal.server.maps.semos;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;
import marauroa.common.game.IRPZone;

public class SemosPlainsNorth implements ZoneConfigurator {
	private NPCList npcs = NPCList.get();

	public void build() {
		StendhalRPWorld world = StendhalRPWorld.get();

		buildSemosNorthPlainsArea((StendhalRPZone) world
					  .getRPZone(new IRPZone.ID("0_semos_plains_n"))); 
		buildSemosCatacombs1Area((StendhalRPZone) world
					 .getRPZone(new IRPZone.ID("-1_semos_catacombs")));
		buildSemosCatacombs2Area((StendhalRPZone) world
					 .getRPZone(new IRPZone.ID("-2_semos_catacombs")));
		buildSemosCatacombs3Area((StendhalRPZone) world
					 .getRPZone(new IRPZone.ID("-3_semos_catacombs")));
	}


	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		/*
		 * For now - Split to one class per zone
		 */
		build();
	}


	private void buildSemosNorthPlainsArea(StendhalRPZone zone) {

		SpeakerNPC npc = new SpeakerNPC("Plink") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(36, 108));
				nodes.add(new Path.Node(37, 108));
				nodes.add(new Path.Node(37, 105));
				nodes.add(new Path.Node(42, 105));
				nodes.add(new Path.Node(42, 111));
				nodes.add(new Path.Node(48, 111));
				nodes.add(new Path.Node(47, 103));
				nodes.add(new Path.Node(47, 100));
				nodes.add(new Path.Node(53, 100));
				nodes.add(new Path.Node(53, 90));
				nodes.add(new Path.Node(49, 90));
				nodes.add(new Path.Node(49, 98));
				nodes.add(new Path.Node(46, 98));
				nodes.add(new Path.Node(46, 99));
				nodes.add(new Path.Node(36, 99));
				
				setPath(nodes, true);
			}
		
			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I play all day.");
				addHelp("Be careful out east, there are wolves about!");
				addGoodbye();
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "plinknpc");
		npc.set(36, 108);
		npc.initHP(100);
		zone.addNPC(npc);
	}
	private void buildSemosCatacombs1Area(StendhalRPZone zone) {
	}
	private void buildSemosCatacombs2Area(StendhalRPZone zone) {
	}

	private void buildSemosCatacombs3Area(StendhalRPZone zone) {
	}

}
