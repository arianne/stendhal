package games.stendhal.server.maps.semos.plains;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * A little boy who lives at a farm.
 * 
 * @see games.stendhal.server.maps.quests.PlinksToy
 */
public class LittleBoyNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildSemosNorthPlainsArea(zone);
	}

	private void buildSemosNorthPlainsArea(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Plink") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(36, 108));
				nodes.add(new Node(37, 108));
				nodes.add(new Node(37, 105));
				nodes.add(new Node(42, 105));
				nodes.add(new Node(42, 111));
				nodes.add(new Node(48, 111));
				nodes.add(new Node(47, 103));
				nodes.add(new Node(47, 100));
				nodes.add(new Node(53, 100));
				nodes.add(new Node(53, 90));
				nodes.add(new Node(49, 90));
				nodes.add(new Node(49, 98));
				nodes.add(new Node(46, 98));
				nodes.add(new Node(46, 99));
				nodes.add(new Node(36, 99));

				setPath(new FixedPath(nodes, true));
			}


			@Override
			public void createDialog() {
				// NOTE: These texts are only available after finishing the quest.
				addGreeting();
				addJob("I play all day.");
				addHelp("Be careful out east, there are wolves about!");
				addGoodbye();
			}
	
		};
		npc.setEntityClass("plinknpc");
		npc.setPosition(36, 108);
		npc.initHP(100);
		zone.add(npc);
	}

}
