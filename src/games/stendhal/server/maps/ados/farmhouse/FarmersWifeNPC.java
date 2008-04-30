package games.stendhal.server.maps.ados.farmhouse;

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
 * NPC to sell milk.
 *
 * @author kymara
 */
public class FarmersWifeNPC implements ZoneConfigurator {

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
		SpeakerNPC npc = new SpeakerNPC("Philomena") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(27, 4));
				nodes.add(new Node(33, 4));
				nodes.add(new Node(33, 10));
				nodes.add(new Node(27, 10));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Goeden dag!");
				addJob("My husband runs this farm. I don't know where he is now though ...");
				addQuest("If you can write Junit tests then my daughter needs you. Just ask Diogenes how to help the project.");
				addHelp("I can sell you a bottle of milk from our dairy cows if you like.");
				Map<String, Integer> offerings = new HashMap<String, Integer>();
				offerings.put("milk", 30);
				new SellerAdder().addSeller(this, new SellerBehaviour(offerings));

				addGoodbye("Tot ziens.");
			}
		};
		npc.setEntityClass("wifenpc");
		npc.setPosition(27, 4);
		npc.initHP(100);
	    zone.add(npc);
	}
}
