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
 * A woman who bakes bread for players.
 * 
 * @author daniel
 */
public class ShopAssistantNPC implements ZoneConfigurator  {

	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Erna") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
                nodes.add(new Node(26,9));
                nodes.add(new Node(26,6));
                nodes.add(new Node(28,6));
                nodes.add(new Node(28,2));
                nodes.add(new Node(28,5));
                nodes.add(new Node(22,5));
                nodes.add(new Node(22,4));
                nodes.add(new Node(22,7));
                nodes.add(new Node(26,7));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			public void createDialog() {
				addJob("I'm the shop assistant at this bakery.");
				addReply("flour",
				"We usually get our #flour from a mill northeast of here, but the wolves ate their delivery boy! If you help us out by bringing some, we can #bake delicious bread for you.");
				addHelp("Bread is very good for you, especially for you adventurers who are always gulping down red meat. And my boss, Leander, happens to make the best sandwiches on the island!");
				addGoodbye();

				// Erna bakes bread if you bring her flour.
				final Map<String, Integer> requiredResources = new TreeMap<String, Integer>();
				requiredResources.put("flour", 2);

				final ProducerBehaviour behaviour = new ProducerBehaviour("erna_bake_bread",
						"bake", "bread", requiredResources, 10 * 60);

				new ProducerAdder().addProducer(this, behaviour,
				"Welcome to the Semos bakery! We'll #bake fine bread for anyone who helps bring our #flour delivery from the mill.");
			}};
			npc.setPosition(26, 9);
			npc.setEntityClass("housewifenpc");
			npc.setDescription("You see Erna. She's worked a long time for Leander and is his loyal assistant.");
			zone.add(npc);		
	}
}

