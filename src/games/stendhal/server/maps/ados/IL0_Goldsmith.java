package games.stendhal.server.maps.ados;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.ProducerBehaviour;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Ados Goldsmith (Inside / Level 0)
 *
 * @author dine
 */
public class IL0_Goldsmith implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildGoldsmith(zone, attributes);
	}

	private void buildGoldsmith(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC goldsmith = new SpeakerNPC("Joshua") {

			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				// to the oven
				nodes.add(new Path.Node(5, 2));
				// to a water
				nodes.add(new Path.Node(5, 8));
				nodes.add(new Path.Node(4, 8));
				// to the table
				nodes.add(new Path.Node(4, 11));
				nodes.add(new Path.Node(3, 11));
				nodes.add(new Path.Node(3, 12));
				// to the bar
				nodes.add(new Path.Node(8, 12));
				nodes.add(new Path.Node(8, 9));
				nodes.add(new Path.Node(14, 9));
				// towards the shields
				nodes.add(new Path.Node(14, 4));
				nodes.add(new Path.Node(18, 4));
				// to the starting point
				nodes.add(new Path.Node(18, 2));

				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting("Hi!");
				addJob("I'm the goldsmith of this city.");
				addHelp("My brother Xoderos is a blacksmith in Semos. Currently he is selling tools. Perhaps he can make a #gold_pan for you.");
				addGoodbye("Bye");

				// Joshua makes gold if you bring him gold_nugget and wood
				Map<String, Integer> requiredResources = new HashMap<String, Integer>();
				requiredResources.put("wood", 2);
				requiredResources.put("gold_nugget", 1);

				ProducerBehaviour behaviour = new ProducerBehaviour("joshua_cast_gold",
						"cast", "gold_bar", requiredResources, 15 * 60);

				addProducer(behaviour,
				        "Hi! I'm the local goldsmith. If you require me to #cast you a #barrel #of #gold just tell me!");
				addReply("wood",
		        		"I need some wood to keep my furnace lit. You can find any amount of it just lying around in the forest.");
				addReply(Arrays.asList("ore", "gold_ore", "gold_nugget"),
				        "I think there are places in the water where you can find gold ore. But you need a special tool to prospect for gold.");
				addReply(Arrays.asList("gold_bar", "gold", "bar"),
				        "After I've casted the gold for you keep it save. I've heard rumours that Fado city will be safe to travel to again soon. There you can sell or trade gold.");
				addReply("gold_pan",
				        "If you had a gold pan, you would be able to prospect for gold at certain places.");

			}
		};
		npcs.add(goldsmith);
		zone.assignRPObjectID(goldsmith);
		goldsmith.put("class", "goldsmithnpc");
		goldsmith.setDirection(Direction.DOWN);
		goldsmith.set(18, 2);
		goldsmith.initHP(100);
		zone.add(goldsmith);
	}
}
