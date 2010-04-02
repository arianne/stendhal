package games.stendhal.server.maps.orril.dungeon;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.RatKidsNPCBase;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Builds a Rat Child NPC.
 *
 * @author Norien
 */
public class RatChild1NPC implements ZoneConfigurator {
	

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone, attributes);
	}

	private void buildNPC(final StendhalRPZone zone, final Map<String, String> attributes) {
		final SpeakerNPC rat = new RatKidsNPCBase("Opal") {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
                                nodes.add(new Node(5, 75));
				nodes.add(new Node(19, 75));
				nodes.add(new Node(19, 79));
				nodes.add(new Node(5, 79));
				setPath(new FixedPath(nodes, true));
			}
		};

		rat.setDescription("You see a rat child.");
		rat.setEntityClass("ratchild1npc");
		rat.setPosition(5, 75);
		rat.initHP(100);
		zone.add(rat);
	}
}