package games.stendhal.server.maps.ados.fishermans_hut;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.ProducerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Inside Ados fishermans hut south.
 */
public class BlacksheepJoeNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildblacksheepjoe(zone);
	}

	private void buildblacksheepjoe(StendhalRPZone zone) {
		SpeakerNPC blacksheepjoe = new SpeakerNPC("Blacksheep Joe") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(13, 13));
				nodes.add(new Node(13, 9));
				setPath(new FixedPath(nodes, true));

			}

		@Override
		protected void createDialog() {
			addJob("I supply cheese sausages for the whole world.");
			addHelp("I only #make cheese sausages. My brothers here make sausage and canned tuna.");
			addOffer("Just check the blackboard in the back, I will #make you some cheese sausages.");
			addQuest("I don't need any help right now. Thanks.");
			addGoodbye("Good bye. Tell all your friends about us.");

			// Blacksheep Joe creates you some cheese sausages
			// (uses sorted TreeMap instead of HashMap)
			Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
			requiredResources.put("vampirette entrails", Integer.valueOf(1));
			requiredResources.put("bat entrails", Integer.valueOf(1));
			requiredResources.put("chicken", Integer.valueOf(1));
			requiredResources.put("black pearl", Integer.valueOf(1));
			requiredResources.put("cheese", Integer.valueOf(1));

			ProducerBehaviour behaviour = new ProducerBehaviour("blacksheepjoe_make_cheese_sausage", "make", "cheese sausage",
			        requiredResources, 2 * 60);

			new ProducerAdder().addProducer(this, behaviour,
			        "Hi there. Welcome to Blacksheep Meat Market. Can I #make you some cheese sausages?");
		}
	};

	blacksheepjoe.setEntityClass("blacksheepnpc");
	blacksheepjoe.setPosition(13, 13);
	blacksheepjoe.initHP(100);
	zone.add(blacksheepjoe);
		
	}
}