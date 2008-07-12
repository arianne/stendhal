package games.stendhal.server.maps.ados.fishermans_hut;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Ados Fisherman (Inside / Level 0).
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
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildFisherman(zone, attributes);
	}

	private void buildFisherman(final StendhalRPZone zone, final Map<String, String> attributes) {
		final SpeakerNPC fisherman = new SpeakerNPC("Pequod") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
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
