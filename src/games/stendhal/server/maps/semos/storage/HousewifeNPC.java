package games.stendhal.server.maps.semos.storage;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HousewifeNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildSemosStorageArea(zone, attributes);
	}

	private void buildSemosStorageArea(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC npc = new SpeakerNPC("Eonna") {

			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(4, 12)); // its around the table with
				// the beers and to the
				// furnance
				nodes.add(new Path.Node(15, 12));
				nodes.add(new Path.Node(15, 12));
				nodes.add(new Path.Node(15, 8));
				nodes.add(new Path.Node(10, 8));
				nodes.add(new Path.Node(10, 12));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting("Hi there, young hero.");
				addJob("I'm just a regular housewife.");
				addHelp("I don't think I can help you with anything.");
				addGoodbye();
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "welcomernpc");
		npc.set(4, 12);
		npc.initHP(100);
		zone.add(npc);
	
	}
}
