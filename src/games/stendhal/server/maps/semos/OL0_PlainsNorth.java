package games.stendhal.server.maps.semos;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

public class OL0_PlainsNorth implements ZoneConfigurator {
	private NPCList npcs = NPCList.get();


	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		buildSemosNorthPlainsArea(zone);
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
		zone.add(npc);
	}
}
