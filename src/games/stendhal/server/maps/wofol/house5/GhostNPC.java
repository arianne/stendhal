package games.stendhal.server.maps.wofol.house5;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.GhostNPCBase;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Builds a Ghost NPC.
 *
 * @author kymara
 */
public class GhostNPC implements ZoneConfigurator {
	//
	// ZoneConfigurator
	//

	/**
	 * Configures a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildNPC(zone, attributes);
	}

	private void buildNPC(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC ghost = new GhostNPCBase("Zak") {
			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(3, 4));
				nodes.add(new Node(10, 4));
				nodes.add(new Node(10, 9));
				nodes.add(new Node(8, 9));
				nodes.add(new Node(8, 7));
				nodes.add(new Node(6, 7));
				nodes.add(new Node(6, 5));
				nodes.add(new Node(3, 5));
				setPath(new FixedPath(nodes, true));
			}
		};

		ghost.setDescription("You see a ghostly figure of a man. You have no idea how he died.");
		ghost.setResistance(0);
		ghost.setEntityClass("man_000_npc");
		// he is a ghost so he is see through
		ghost.setVisibility(50);
		ghost.setPosition(3, 4);
		// he has low HP
		ghost.initHP(30);
		zone.add(ghost);
	}
}
