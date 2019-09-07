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
package games.stendhal.server.maps.deniran.cityoutside;

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
import games.stendhal.server.entity.npc.behaviour.impl.MonologueBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;

/**
 * Provides a grocery seller in Deniran Marketplace 
 *
 * @author omero
 *
 */
public class DeniranMarketSellerNPC1Grocery implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		final String[] yells = {
			"HEYOH! Grocery stuff here at the market... Come closer, have a look!",
			"HOYEH! I have all the stuff to prepare a decent meal and more!",
			"AHAWW! Is this a market or a cemetery?!... It seems so quiet around here..."
		};
		new MonologueBehaviour(buildNPC(zone), yells, 1);
	}
	
	private SpeakerNPC buildNPC(final StendhalRPZone zone) {
		//Ambrogio is a temporary name
		final SpeakerNPC npc = new SpeakerNPC("Ambrogio") {

			@Override
			public void createDialog() {
				addGreeting(
						"Hello visitor! " +
						"If you came looking for grocery stuff, I #offer grocery stuff... " + 
						"Oh, I should really set up one of those blackboards where offers are listed!");
				addOffer(
						"I sell grocery stuff... " +
								"eggs, " +
								"potatoes, " +
								"pinto beans, " +
								"habanero peppers, " +
								"olive oil, " +
								"vinegar, " +
								"sugar... " +
						"If you want to #buy some stuff, tell me... " +
						"Oh, I should really set up one of those blackboards where offers are listed!");
				//Offered items:
				final Map<String, Integer> offerings = new HashMap<String, Integer>();
                offerings.put("egg", 35);
                offerings.put("potato", 40);
                offerings.put("pinto beans", 45);
                offerings.put("habanero pepper", 50);
                offerings.put("olive oil", 130);
                offerings.put("vinegar", 135);
                offerings.put("sugar", 140);                
                new SellerAdder().addSeller(this, new SellerBehaviour(offerings), false);
                
				addJob("I am here to #offer grocery stuff to travelers like you... " +
					   "If you want to #buy, tell me... " +
					   "Oh, I should really set up one of those blackboards where offers are listed!");
				
				addHelp(
						"If you need some grocery stuff, I do #offer some grocery stuff... " +
						"When you want to #buy something, tell me... " +
						"Oh, I should really set up one of those blackboards where offers are listed");
				
				addGoodbye("So long...");
				
			}

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(23, 116));
				nodes.add(new Node(29, 116));
				nodes.add(new Node(29, 120));
				nodes.add(new Node(20, 120));
				setPath(new FixedPath(nodes, true));
			}
		};
		
		// Finalize Deniran Market Seller NPC (Grocery)
		//npc.setEntityClass("fatsellernpc");
		npc.setEntityClass("deniran_marketsellernpc1grocery");
		npc.setPosition(26, 122);
		npc.setCollisionAction(CollisionAction.REROUTE);
		npc.setDescription("You see a busy marketplace seller...");
		zone.add(npc);
		
		return npc;
		
	}
}
