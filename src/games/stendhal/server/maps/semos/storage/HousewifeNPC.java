package games.stendhal.server.maps.semos.storage;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HousewifeNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildSemosStorageArea(zone, attributes);
	}

	private void buildSemosStorageArea(final StendhalRPZone zone, final Map<String, String> attributes) {
		final SpeakerNPC npc = new SpeakerNPC("Eonna") {

			/**
			 * Creates a path around the table with the beers and to the furnance.
			 */
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(4, 12)); 
				nodes.add(new Node(15, 13));
				nodes.add(new Node(15, 13));
				nodes.add(new Node(15, 9));
				nodes.add(new Node(10, 9));
				nodes.add(new Node(10, 13));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hi there, young hero.");
				addJob("I'm just a regular housewife.");
				addHelp("I don't think I can help you with anything.");
				addGoodbye();
			}
		};

		npc.setEntityClass("welcomernpc");
		npc.setDescription("You see Eonna. She is a lovely housewife and scared to death of rats!");
		npc.setPosition(4, 13);
		npc.initHP(100);
		zone.add(npc);
	}
}
