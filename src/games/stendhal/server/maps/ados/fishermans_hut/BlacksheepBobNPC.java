package games.stendhal.server.maps.ados.fishermans_hut;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.adder.ProducerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Inside Ados fishermans hut south.
 */
public class BlacksheepBobNPC implements ZoneConfigurator {
    private ShopList shops = SingletonRepository.getShopList();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildblacksheepbob(zone);
	}

	private void buildblacksheepbob(StendhalRPZone zone) {
		SpeakerNPC blacksheepbob = new SpeakerNPC("Blacksheep Bob") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(2, 13));
				nodes.add(new Node(2, 9));
				setPath(new FixedPath(nodes, true));

			}

		@Override
		protected void createDialog() {
			addJob("I'm proud to say i #make some absolutely delicious sausage.");
			addHelp("I only #make sausage. My brothers here make canned tuna and cheese sausage.");
			addOffer("Check the blackboard to see what I need to #make you some sausage.");
			addQuest("I don't need any help.");
			addGoodbye("Good bye. Be sure to tell your friends about us.");

			// Blacksheep Bob makes you sausages if you supply his ingredients
			// (uses sorted TreeMap instead of HashMap)
			Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
			requiredResources.put("vampirette entrails", Integer.valueOf(1));
			requiredResources.put("bat entrails", Integer.valueOf(1));
			requiredResources.put("meat", Integer.valueOf(1));
			requiredResources.put("wine", Integer.valueOf(2));
			
			ProducerBehaviour behaviour = new ProducerBehaviour("blacksheepbob_make_sausage", "make", "sausage",
			        requiredResources, 2 * 60);

			new ProducerAdder().addProducer(this, behaviour,
			        "Hey there. Welcome to Blacksheep Meat Market. Can i #make you some sausage?");
		}
	};

	blacksheepbob.setEntityClass("blacksheepnpc");
	blacksheepbob.setPosition(2, 13);
	blacksheepbob.initHP(100);
	zone.add(blacksheepbob);
		
	}
}