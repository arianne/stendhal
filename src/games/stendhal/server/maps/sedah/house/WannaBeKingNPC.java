package games.stendhal.server.maps.sedah.house;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Builds the NPC who wants to be the king of Kalavan.
 *
 * @author johnnnny
 */
public class WannaBeKingNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone, attributes);
	}

	/**
	 * initialize the NPC.
	 * 
	 * @param zone
	 * @param attributes
	 */
	private void buildNPC(final StendhalRPZone zone, final Map<String, String> attributes) {
		final SpeakerNPC npc = new SpeakerNPC("Ivan Abe") {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(3, 7));
				nodes.add(new Node(12, 7));
				nodes.add(new Node(12, 3));
				nodes.add(new Node(6, 3));
				nodes.add(new Node(6, 7));
				nodes.add(new Node(3, 7));
				setPath(new FixedPath(nodes, true));
			}
		};

		npc.setEntityClass("wannabekingnpc");
		npc.setPosition(3, 7);
		npc.initHP(100);
		npc.setDescription("You see Ivan Abe. He wants to be the king of Kalavan.");
		zone.add(npc);
	}
}
