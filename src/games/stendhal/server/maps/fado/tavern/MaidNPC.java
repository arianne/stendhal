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
package games.stendhal.server.maps.fado.tavern;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.CollisionAction;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;

/**
 * Builds the tavern maid NPC.
 *
 * @author timothyb89/kymara
 */
public class MaidNPC implements ZoneConfigurator {

	private static final int TIME_OUT = 60;

	//
	// ZoneConfigurator
	//

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	//
	// MaidNPC
	//

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC tavernMaid = new SpeakerNPC("Old Mother Helena") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(28, 15));
				nodes.add(new Node(10, 15));
				nodes.add(new Node(10, 27));
				nodes.add(new Node(19, 27));
				nodes.add(new Node(19, 28));
				nodes.add(new Node(20, 28));
				nodes.add(new Node(21, 28));
				nodes.add(new Node(21, 27));
				nodes.add(new Node(28, 27));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				//addGreeting();
				addJob("I am the bar maid for this fair tavern. We sell imported beers and fine food.");
				addHelp("Why not gather some friends and take a break together, you can put your food down and eat from that long table.");
				addQuest("Oh, I don't have time for anything like that.");

				final Map<String, Integer> offers = new HashMap<String, Integer>();
				offers.put("beer", 10);
				offers.put("wine", 15);
				offers.put("cherry", 20);
				offers.put("chicken", 50);
				offers.put("bread", 50);
				offers.put("sandwich", 150);

				new SellerAdder().addSeller(this, new SellerBehaviour(offers));
				addGoodbye("Goodbye, all you customers do work me hard ...");
			}
		};

		tavernMaid.setPlayerChatTimeout(TIME_OUT);
		tavernMaid.setEntityClass("oldmaidnpc");
		tavernMaid.setPosition(10, 16);
		tavernMaid.setCollisionAction(CollisionAction.STOP);
		tavernMaid.initHP(100);
		tavernMaid.setDescription("You see Old Mother Helena. She is a fantastic cook and her soup is world-famous.");
		zone.add(tavernMaid);
	}
}
