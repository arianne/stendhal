package games.stendhal.server.maps.semos.bakery;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.ProducerBehaviour;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

public class ShopAssistantNPC implements ZoneConfigurator {

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
		SpeakerNPC erna = new SpeakerNPC("Erna") {

			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(26, 8));
				nodes.add(new Path.Node(26, 5));
				nodes.add(new Path.Node(28, 5));
				nodes.add(new Path.Node(28, 1));
				nodes.add(new Path.Node(28, 4));
				nodes.add(new Path.Node(22, 4));
				nodes.add(new Path.Node(22, 3));
				nodes.add(new Path.Node(22, 6));
				nodes.add(new Path.Node(26, 6));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addJob("I'm the shop assistant at this bakery.");
				addReply(
				        "flour",
				        "We usually get our #flour from a mill northeast of here, but the wolves ate their delivery boy! If you help us out by bringing some, we can #bake delicious bread for you.");
				addHelp("Bread is very good for you, especially for you adventurers who are always gulping down red meat. And my boss, Leander, happens to make the best sandwiches on the island!");
				addGoodbye();

				// Erna bakes bread if you bring her flour.
				Map<String, Integer> requiredResources = new HashMap<String, Integer>();
				requiredResources.put("flour", new Integer(2));

				ProducerBehaviour behaviour = new ProducerBehaviour("erna_bake_bread", "bake", "bread",
				        requiredResources, 10 * 60);

				addProducer(behaviour,
				        "Welcome to the Semos bakery! We'll #bake fine bread for anyone who helps bring our #flour delivery from the mill.");
			}
		};
		npcs.add(erna);
		zone.assignRPObjectID(erna);
		erna.put("class", "housewifenpc");
		erna.set(26, 8);
		erna.initHP(100);
		zone.add(erna);

	}
}
