package games.stendhal.server.maps.semos;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Chest;
import games.stendhal.server.entity.NPCOwnedChest;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.ProducerBehaviour;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;
import games.stendhal.server.scripting.ScriptingNPC;
import marauroa.common.game.IRPZone;

public class IL0_Bakery implements ZoneConfigurator {
	private NPCList npcs = NPCList.get();
	private ShopList shops = ShopList.get();

	public void build() {
		StendhalRPWorld world = StendhalRPWorld.get();

		configureZone(
			(StendhalRPZone) world.getRPZone(
				new IRPZone.ID("int_semos_bakery")),
			java.util.Collections.EMPTY_MAP);
	}


	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		buildSemosBakeryArea(zone, attributes);
	}


	private void buildSemosBakeryArea(StendhalRPZone zone,
	 Map<String, String> attributes) {
		/*
		 * Portals configured in xml?
		 */
		if(attributes.get("xml-portals") == null) {
			Portal portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setX(26);
			portal.setY(14);
			portal.setNumber(0);
			portal.setDestination("0_semos_city", 10);
			zone.addPortal(portal);
		}

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
				addReply("flour", "We usually get our #flour from a mill northeast of here, but the wolves ate their delivery boy! If you help us out by bringing some, we can #bake delicious bread for you.");
				addHelp("Bread is very good for you, especially for you adventurers who are always gulping down red meat. And my boss, Leander, happens to make the best sandwiches on the island!");
				addGoodbye();

				// Erna bakes bread if you bring her flour.
				Map<String, Integer> requiredResources = new HashMap<String, Integer>();
				requiredResources.put("flour", new Integer(2));

				ProducerBehaviour behaviour = new ProducerBehaviour(
						"erna_bake_bread", "bake", "bread", requiredResources, 10 * 60);

				addProducer(behaviour, "Welcome to the Semos bakery! We'll #bake fine bread for anyone who helps bring our #flour delivery from the mill.");
			}
		};
		npcs.add(erna);
		zone.assignRPObjectID(erna);
		erna.put("class", "housewifenpc");
		erna.set(26, 8);
		erna.initHP(100);
		zone.addNPC(erna);

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
				addJob("I'm the local baker. We used to get a lot of orders from Ados before the war broke out and they blocked the road. At least it gives me more time to #make sandwiches for out valuable customers; everybody says they're great!");
				addReply("bread", "Oh, Erna handles that side of the business; just go over and talk to her.");
				addReply("cheese", "Cheese is pretty hard to find at the minute, we had a big rat infestation recently. I wonder where the little rodents took it all to?");
				addReply("ham", "Well, you look like a skilled hunter; why not go to the forest and hunt some up fresh? Don't bring me those little pieces of meat, though... I only make sandwiches from high quality ham!");
				addHelp("My daughter Sally might be able to help you get ham. She's a scout, you see; I think she's currently camped out south of Or'ril Castle.");				addGoodbye();

				// Leander makes sandwiches if you bring him bread, cheese, and ham.
				Map<String, Integer> requiredResources = new HashMap<String, Integer>();
				requiredResources.put("bread", new Integer(1));
				requiredResources.put("cheese", new Integer(2));
				requiredResources.put("ham", new Integer(1));

				ProducerBehaviour behaviour = new ProducerBehaviour(
						"leander_make_sandwiches", "make", "sandwich", requiredResources, 3 * 60);

				addProducer(behaviour,
						"Hi! I bet you've heard about my famous sandwiches and want me to #make you one, am I right?");
			}
		};
		npcs.add(leander);
		zone.assignRPObjectID(leander);
		leander.put("class", "chefnpc");
		leander.setDirection(Direction.DOWN);
		leander.set(15, 2);
		leander.initHP(100);
		zone.addNPC(leander);
		
		Chest chest = new NPCOwnedChest(erna);
		zone.assignRPObjectID(chest);
		chest.set(29, 6);
		zone.add(chest);
	}
}
