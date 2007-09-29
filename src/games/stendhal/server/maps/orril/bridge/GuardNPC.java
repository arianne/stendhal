package games.stendhal.server.maps.orril.bridge;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Builds the bridge guard (to fado) NPC.
 *
 * @author kymara
 */
public class GuardNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 * 
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Stefan") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(62, 105));
				nodes.add(new Node(63, 105));
				nodes.add(new Node(64, 105));
				nodes.add(new Node(65, 105));
				nodes.add(new Node(64, 105));
				nodes.add(new Node(63, 105));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hi, can I #help you?");
				addJob("I guard this bridge and check the road block stays in place.");
				addHelp("The road to Fado is closed until the city is safe.");
				addQuest("I'd like something more interesting to do, too.");
				addGoodbye("Goodbye and come back soon, I get bored here.");
			}
		};

		npc.setDescription("You see a bored looking guard.");
		npc.setEntityClass("recruiter1npc");
		npc.setPosition(62, 105);
		npc.initHP(100);
		zone.add(npc);
	}
}
