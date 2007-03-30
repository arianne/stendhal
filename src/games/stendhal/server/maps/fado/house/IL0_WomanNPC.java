package games.stendhal.server.maps.fado.house;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

/**
 * Builds Josephine NPC (Cloak Collector)
 *
 * @author kymara
 */
public class IL0_WomanNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	//
	// ZoneConfigurator
	//

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildNPC(zone, attributes);
	}

	//
	// IL0_womanNPC - Josephine, the Cloaks Collector
	//

	private void buildNPC(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC woman = new SpeakerNPC("Josephine") {

			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(3, 3));
				nodes.add(new Path.Node(16, 3));
				nodes.add(new Path.Node(16, 6));
				nodes.add(new Path.Node(3, 6));
				nodes.add(new Path.Node(3, 5));
				nodes.add(new Path.Node(5, 5));
				nodes.add(new Path.Node(5, 3));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				//addGreeting();
				addJob("If I could, I'd design dresses!");
				addHelp("You can get help from Xhiphin Zohos, he's usually just outside. *giggle* I wonder why!");
				addGoodbye("Bye bye now!");
			}
		};
		woman.setDescription("You see a fashionably dressed young woman. She looks like a bit of a flirt.");
		npcs.add(woman);
		zone.assignRPObjectID(woman);
		woman.put("class", "youngwomannpc");
		woman.set(3, 3);
		woman.initHP(100);
		zone.add(woman);
	}
}
