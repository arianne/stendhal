package games.stendhal.server.maps.orril.dungeon;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.npc.GhostNPCBase;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Builds a Ghost NPC
 *
 * @author kymara
 */
public class GhostNPC implements ZoneConfigurator {
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

	private void buildNPC(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC ghost = new GhostNPCBase("Goran") {
			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(216, 127));
				nodes.add(new Node(200, 127));
				nodes.add(new Node(200, 120));
				nodes.add(new Node(216, 120));
				nodes.add(new Node(216, 122));
				nodes.add(new Node(200, 122));
				nodes.add(new Node(200, 124));
				nodes.add(new Node(216, 124));
				setPath(new FixedPath(nodes, true));
			}
		};

		ghost.setDescription("You see a ghostly figure of a man. He appears to have died in battle.");
		ghost.setResistance(0);
		ghost.setEntityClass("deadmannpc");
		// he is a ghost so he is see through
		ghost.setVisibility(70);
		ghost.setPosition(216, 127);
		// he has low HP
		ghost.initHP(30);
		zone.add(ghost);
	}
}
