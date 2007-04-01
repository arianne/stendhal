package games.stendhal.server.maps.semos.bakery;

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
import games.stendhal.server.pathfinder.Path;

public class ChefNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildSemosBakeryArea(zone, attributes);
	}

	private void buildSemosBakeryArea(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC leander = new SpeakerNPC("Leander") {

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

				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addJob("I'm the local baker. I also run a #pizza delivery service. We used to get a lot of orders from Ados before the war broke out and they blocked the road. At least it gives me more time to #make sandwiches for out valuable customers; everybody says they're great!");
				addHelp("If you want to earn some money, you could do me a #favor and help me with the #pizza deliveries. My daughter #Sally used to do it, but she's camping at the moment.");
				addReply("bread", "Oh, Erna handles that side of the business; just go over and talk to her.");
				addReply(
				        "cheese",
				        "Cheese is pretty hard to find at the minute, we had a big rat infestation recently. I wonder where the little rodents took it all to?");
				addReply(
				        "ham",
				        "Well, you look like a skilled hunter; why not go to the forest and hunt some up fresh? Don't bring me those little pieces of meat, though... I only make sandwiches from high quality ham!");
				addReply(
				        "Sally",
				        "My daughter Sally might be able to help you get ham. She's a scout, you see; I think she's currently camped out south of Or'ril Castle.");
				addReply("pizza", "I need someone who helps me delivering pizza. Maybe you could do that #task.");
				addReply(Arrays.asList("sandwich", "sandwiches"),
				        "My sandwiches are tasty and nutritious. If you want one, just tell me to #make #1 #sandwich.");
				addGoodbye();

				// Leander makes sandwiches if you bring him bread, cheese, and ham.
				Map<String, Integer> requiredResources = new HashMap<String, Integer>();
				requiredResources.put("bread", new Integer(1));
				requiredResources.put("cheese", new Integer(2));
				requiredResources.put("ham", new Integer(1));

				ProducerBehaviour behaviour = new ProducerBehaviour("leander_make_sandwiches", "make", "sandwich",
				        requiredResources, 3 * 60);

				addProducer(behaviour, "Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.");
			}
		};
		npcs.add(leander);
		zone.assignRPObjectID(leander);
		leander.put("class", "chefnpc");
		leander.setDirection(Direction.DOWN);
		leander.set(15, 2);
		leander.initHP(100);
		zone.add(leander);
	}
}
