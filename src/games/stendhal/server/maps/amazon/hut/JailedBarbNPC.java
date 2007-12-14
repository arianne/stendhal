package games.stendhal.server.maps.amazon.hut;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Builds the jailed Barbarian in Prison Hut on amazon island
 * 
 * @author Teiv
 */
public class JailedBarbNPC implements ZoneConfigurator {
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
		SpeakerNPC JailedBarbNPC = new SpeakerNPC("Lorenz") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(11, 12));
				nodes.add(new Node(11, 10));
				nodes.add(new Node(9, 10));
				nodes.add(new Node(9, 6));
				nodes.add(new Node(11, 6));
				nodes.add(new Node(11, 4));
				nodes.add(new Node(4, 4));
				nodes.add(new Node(4, 6));
				nodes.add(new Node(6, 6));
				nodes.add(new Node(6, 10));
				nodes.add(new Node(4, 10));
				nodes.add(new Node(4, 12));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Flowers, flowers. All over here these ugly flowers!");
				addJob("I belong to the #Guard of the hidden King! Oops to much information for you!");
				addReply("guard",
						"Uhm as i said, i didn't said anything to you!");
				addHelp("Kill as much of these ugly Amazoness as you can, they tried to make me going insane with these ugly flowers all over here.");
				addOffer("Nothing to offer you!");
				addGoodbye("Bye bye, and cut down some of these ugly flowers!");
			}
		};

		JailedBarbNPC.setEntityClass("jailedbarbariannpc");
		JailedBarbNPC.setPosition(11, 12);
		JailedBarbNPC.initHP(100);
		zone.add(JailedBarbNPC);
	}
}
