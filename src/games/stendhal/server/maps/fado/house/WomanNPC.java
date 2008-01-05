package games.stendhal.server.maps.fado.house;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Builds Josephine NPC (Cloak Collector).
 *
 * @author kymara
 */
public class WomanNPC implements ZoneConfigurator {
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
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(3, 4));
				nodes.add(new Node(16, 4));
				nodes.add(new Node(16, 7));
				nodes.add(new Node(3, 7));
				nodes.add(new Node(3, 6));
				nodes.add(new Node(5, 6));
				nodes.add(new Node(5, 4));
				setPath(new FixedPath(nodes, true));
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
		woman.setEntityClass("youngwomannpc");
		woman.setPosition(3, 4);
		woman.initHP(100);
		zone.add(woman);
	}
}
