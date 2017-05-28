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

import games.stendhal.common.Direction;
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
public class BlacksheepHarryNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildblacksheepharry(zone);
	}

	private void buildblacksheepharry(final StendhalRPZone zone) {
		final SpeakerNPC blacksheepharry = new SpeakerNPC("Blacksheep Harry") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(5, 2));
				nodes.add(new Node(10, 2));
				setPath(new FixedPath(nodes, true));

			}

		@Override
		protected void createDialog() {
			addJob("I supply canned tuna for the whole world.");
			addHelp("I only #make canned tuna. My brothers here make sausage and cheese sausage.");
			addOffer("Just give me some mackerel, I will #make you some canned tuna.");
			addQuest("I don't really think I should ask for help right now.");
			addGoodbye("Good bye. Tell all your friends about us.");

			// Blacksheep Harry makes you some tuna if you bring him a mackerel and a perch
			// (uses sorted TreeMap instead of HashMap)
			final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
			requiredResources.put("mackerel", Integer.valueOf(1));
			requiredResources.put("perch", Integer.valueOf(1));
			requiredResources.put("marbles", Integer.valueOf(2));

			final ProducerBehaviour behaviour = new ProducerBehaviour("blacksheepharry_make_tuna", "make", "canned tuna",
			        requiredResources, 2 * 60);

			new ProducerAdder().addProducer(this, behaviour,
			        "Welcome to Blacksheep Meat Market. Can I #make you some canned tuna?");
		}
	};

	blacksheepharry.setEntityClass("blacksheepnpc");
	blacksheepharry.setDirection(Direction.DOWN);
	blacksheepharry.setPosition(5, 2);
	blacksheepharry.initHP(100);
	blacksheepharry.setDescription("You see Blacksheep Harry. He is an insider when it's about one kind of fish.");
	zone.add(blacksheepharry);

	}
}
