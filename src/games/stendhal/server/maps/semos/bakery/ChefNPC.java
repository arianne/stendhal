/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.semos.bakery;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.adder.ProducerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;

/**
 * The bakery chef. Father of the camping girl.
 * He makes sandwiches for players.
 * He buys cheese.
 *
 * @author daniel
 * @see games.stendhal.server.maps.orril.river.CampingGirlNPC
 * @see games.stendhal.server.maps.quests.PizzaDelivery
 */
public class ChefNPC implements ZoneConfigurator  {

	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Leander") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				// to the well
				nodes.add(new Node(15,3));
				// to a barrel
				nodes.add(new Node(15,8));
				// to the baguette on the table
				nodes.add(new Node(13,8));
				// around the table
				nodes.add(new Node(13,10));
				nodes.add(new Node(10,10));
				// to the sink
				nodes.add(new Node(10,12));
				// to the pizza/cake/whatever
				nodes.add(new Node(7,12));
				nodes.add(new Node(7,10));
				// to the pot
				nodes.add(new Node(3,10));
				// towards the oven
				nodes.add(new Node(3,4));
				nodes.add(new Node(5,4));
				// to the oven
				nodes.add(new Node(5,3));
				// one step back
				nodes.add(new Node(5,4));
				// towards the well
				nodes.add(new Node(15,4));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			public void createDialog() {
				addJob("I'm the local baker. I also run a #pizza delivery service. We used to get a lot of orders from Ados before the war broke out and they blocked the road. At least it gives me more time to #make sandwiches for our valuable customers; everybody says they're great!");
				addHelp("If you want to earn some money, you could do me a #favor and help me with the #pizza deliveries. My daughter #Sally used to do it, but she's camping at the moment.");
				addReply("bread", "Oh, Erna handles that side of the business; just go over and talk to her.");
				addReply("cheese",
				"Cheese is pretty hard to find at the minute, we had a big rat infestation recently. I wonder where the little rodents took it all to? If you #'sell cheese' I'd be happy to buy some from you!");
				addReply("ham",
				"Well, you look like a skilled hunter; why not go to the forest and hunt some up fresh? Don't bring me those little pieces of meat, though... I only make sandwiches from high quality ham!");
				addReply("Sally",
				"My daughter Sally might be able to help you get ham. She's a scout, you see; I think she's currently camped out south of Or'ril Castle.");
				addReply("pizza", "I need someone who helps me delivering pizza. Maybe you could do that #task.");
				addReply(Arrays.asList("sandwich", "sandwiches"),
				"My sandwiches are tasty and nutritious. If you want one, just tell me to #'make 1 sandwich'.");
				addOffer("My #pizza needs cheese and we have no supplies. I'll buy cheese if you will #sell.");
				final Map<String, Integer> offers = new TreeMap<String, Integer>();
				offers.put("cheese", 5);
				new BuyerAdder().addBuyer(this, new BuyerBehaviour(offers), false);

				addGoodbye();

				// Leander makes sandwiches if you bring him bread, cheese, and ham.
				final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
				requiredResources.put("bread", 1);
				requiredResources.put("cheese", 2);
				requiredResources.put("ham", 1);

				final ProducerBehaviour behaviour = new ProducerBehaviour(
						"leander_make_sandwiches", "make", "sandwich",
						requiredResources, 3 * 60);

				new ProducerAdder().addProducer(this, behaviour,
				"Hallo! Glad to see you in my kitchen where I make #pizza and #sandwiches.");


			}};
			npc.setPosition(15, 3);
			npc.setEntityClass("chefnpc");
			npc.setDescription("You see Leander. His job gives him a beautiful smell.");
			zone.add(npc);
	}
}
