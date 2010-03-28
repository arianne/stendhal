package games.stendhal.server.maps.fado.forest;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Fado forest NPC - beekeeper.
 *
 * @author kymara
 */
public class BeeKeeperNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone, attributes);
	}

	private void buildNPC(final StendhalRPZone zone, final Map<String, String> attributes) {
		final SpeakerNPC npc = new SpeakerNPC("Aldrin") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(44, 76));
				nodes.add(new Node(53, 76));
				nodes.add(new Node(53, 77));
				nodes.add(new Node(56, 77));
				nodes.add(new Node(56, 78));
				nodes.add(new Node(57, 78));
				nodes.add(new Node(57, 79));
                nodes.add(new Node(58, 79));
                nodes.add(new Node(58, 86));
                nodes.add(new Node(43, 86));
                nodes.add(new Node(43, 87));
                nodes.add(new Node(59, 87));
                nodes.add(new Node(59, 80));
                nodes.add(new Node(58, 80));
                nodes.add(new Node(58, 79));
				nodes.add(new Node(57, 79));
				nodes.add(new Node(57, 78));
				nodes.add(new Node(56, 78));
				nodes.add(new Node(56, 77));
				nodes.add(new Node(44, 77));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hello.");
				addJob("I keep bees. I expect you've seen my hives around here.");
				addQuest("I don't think I have any job for you to do. You have to work with bees alone, really.");
				addHelp("Bees make honey and wax. I can sell you some if you like. Honey and wax that is, not bees!");
				final Map<String, Integer> offerings = new HashMap<String, Integer>();
				offerings.put("honey", 50);
				offerings.put("beeswax", 80);
				new SellerAdder().addSeller(this, new SellerBehaviour(offerings), false);
				addOffer("I sell sweet honey and beeswax which I harvest myself.");
				addGoodbye("Goodbye and be careful around the hives!");
			}
		};

		npc.setEntityClass("beekeepernpc");
		npc.setPosition(44, 76);
		npc.initHP(100);
		zone.add(npc);
	}
}
