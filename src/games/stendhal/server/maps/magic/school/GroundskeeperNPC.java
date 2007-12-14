package games.stendhal.server.maps.magic.school;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Builds the groundskeeper NPC.
 * 
 * @author Teiv
 */
public class GroundskeeperNPC implements ZoneConfigurator {

	//
	// ZoneConfigurator
	//

	/**
	 * Configure a zone.
	 * 
	 * @param zone
	 *            The zone to be configured.
	 * @param attributes
	 *            Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone, attributes);
	}

	private void buildNPC(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC groundskeeperNPC = new SpeakerNPC("Morgrin") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(35, 13));
				nodes.add(new Node(35, 7));
				nodes.add(new Node(34, 7));
				nodes.add(new Node(34, 4));
				nodes.add(new Node(30, 4));
				nodes.add(new Node(30, 14));
				nodes.add(new Node(32, 14));
				nodes.add(new Node(32, 13));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hello my friend. Nice day for walking isn't it?");
				addReply("no",
						"Oh sorry. Hope tomorrow your day is a better one.");
				addReply("yes", "Fine fine, I hope you enjoy your day.");
				addJob("My job is to clean up school, repair broken things! That's enough to do for a whole day!");
				addHelp("I can not help you, I am busy all the day. But you could help me with a 'little' #task!");
				addGoodbye("Bye.");
			}
		};

		groundskeeperNPC.setEntityClass("groundskeepernpc");
		groundskeeperNPC.setPosition(35, 13);
		groundskeeperNPC.initHP(1000);
		zone.add(groundskeeperNPC);
	}
}
