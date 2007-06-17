package games.stendhal.server.maps.fado.bakery;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.ProducerBehaviour;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Path;

/**
 * Builds the bakery baker NPC.
 *
 * @author timothyb89/kymara
 */
public class BakerNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

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

	//
	// IL0_BakerNPC
	//

	private void buildNPC(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC baker = new SpeakerNPC("Linzo") {

			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				// to the well
				nodes.add(new Path.Node(15, 2));
				// to a barrel
				nodes.add(new Path.Node(15, 7));
				// to the baguette on the table
				nodes.add(new Path.Node(13, 7));
				// around the table
				nodes.add(new Path.Node(13, 9));
				nodes.add(new Path.Node(10, 9));
				// to the sink
				nodes.add(new Path.Node(10, 11));
				// to the pizza/cake/whatever
				nodes.add(new Path.Node(7, 11));
				nodes.add(new Path.Node(7, 9));
				// to the pot
				nodes.add(new Path.Node(3, 9));
				// towards the oven
				nodes.add(new Path.Node(3, 3));
				nodes.add(new Path.Node(5, 3));
				// to the oven
				nodes.add(new Path.Node(5, 2));
				// one step back
				nodes.add(new Path.Node(5, 3));
				// towards the well
				nodes.add(new Path.Node(15, 3));

				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addJob("I'm the local baker. My speciality is fish and leek pies. I pride myself in making them promptly.");
				addReply(Arrays.asList("cod", "mackerel"),   
				        "You can catch cod in Ados. Mackerel may be caught at sea. Perhaps creatures which eat fish might drop them too.");
				addReply("flour","We get our supplies of flour from Semos");
				addReply("leek","We're lucky enough to have leeks growing right here in the Fado allotments.");
				addHelp("Ask me to make you a fish and leek pie. They're not stodgy like meat pies so you can eat them a little quicker.");
				addGoodbye();

				// Linzo makes fish pies if you bring him flour, leek, cod and mackerel
				Map<String, Integer> requiredResources = new HashMap<String, Integer>();
				requiredResources.put("flour", 1);
				requiredResources.put("cod", 2);
				requiredResources.put("mackerel", 1);
				requiredResources.put("leek", 1);

				ProducerBehaviour behaviour = new ProducerBehaviour("linzo_make_fish_pie", "make", "fish_pie",
				        requiredResources, 5 * 60);

				addProducer(behaviour,
				        "Hi there. Have you come to try my fish pies? I can #make one for you.");
			}
		};

		npcs.add(baker);
		zone.assignRPObjectID(baker);
		baker.put("class", "bakernpc");
		baker.setDirection(Direction.DOWN);
		baker.set(15, 2);
		baker.initHP(1000);
		zone.add(baker);
	}
}
