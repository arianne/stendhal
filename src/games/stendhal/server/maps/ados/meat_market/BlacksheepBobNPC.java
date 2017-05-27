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
package games.stendhal.server.maps.ados.meat_market;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.ProducerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.ProducerBehaviour;

/**
 * Inside Ados meat market.
 */
public class BlacksheepBobNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildblacksheepbob(zone);
	}

	private void buildblacksheepbob(final StendhalRPZone zone) {
		final SpeakerNPC blacksheepbob = new SpeakerNPC("Blacksheep Bob") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(2, 13));
				nodes.add(new Node(2, 9));
				setPath(new FixedPath(nodes, true));

			}

		@Override
		protected void createDialog() {
			addJob("I'm proud to say I #make some absolutely delicious sausage.");
			addHelp("I only #make sausage. My brothers here make canned tuna and cheese sausage.");
			addOffer("Check the blackboard to see what I need to #make you some sausage.");
			addQuest("I don't need any help.");
			addGoodbye("Good bye. Be sure to tell your friends about us.");

			// Blacksheep Bob makes you sausages if you supply his ingredients
			// (uses sorted TreeMap instead of HashMap)
			final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
			requiredResources.put("vampirette entrails", Integer.valueOf(1));
			requiredResources.put("bat entrails", Integer.valueOf(1));
			requiredResources.put("meat", Integer.valueOf(1));
			requiredResources.put("wine", Integer.valueOf(2));

			final ProducerBehaviour behaviour = new ProducerBehaviour("blacksheepbob_make_sausage", "make", "sausage",
			        requiredResources, 2 * 60);

			new ProducerAdder().addProducer(this, behaviour,
			        "Hey there. Welcome to Blacksheep Meat Market. Can I #make you some sausage?");
		}
	};

	blacksheepbob.setEntityClass("blacksheepnpc");
	blacksheepbob.setPosition(2, 13);
	blacksheepbob.initHP(100);
	blacksheepbob.setDescription("You see Blacksheep Bob. He is popular for his sausage.");
	zone.add(blacksheepbob);

	}
}
