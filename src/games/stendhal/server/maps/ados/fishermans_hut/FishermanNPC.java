package games.stendhal.server.maps.ados.fishermans_hut;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Ados Fisherman (Inside / Level 0)
 *
 * @author dine
 */
public class FishermanNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildGoldsmith(zone, attributes);
	}

	private void buildGoldsmith(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC fisherman = new SpeakerNPC("Pequod") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				// from left
				nodes.add(new Node(3, 3));
				// to right
				nodes.add(new Node(12, 3));
				// to left
				nodes.add(new Node(3, 3));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addJob("I'm a fisherman.");
				addHelp("Nowadays you can read signposts, books and other things here in Faiumoni.");
				addGoodbye("Goodbye.");
			}
		};

		fisherman.setEntityClass("fishermannpc");
		fisherman.setDirection(Direction.DOWN);
		fisherman.setPosition(3, 3);
		fisherman.initHP(100);
		zone.add(fisherman);
	}
}
