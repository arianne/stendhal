/***************************************************************************
 *                   (C) Copyright 2003-2019 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.ados.market;

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
 * Provides Ambrogita, a grocery seller in Ados Market
 *
 * @author omero
 *
 */
public class GrocerySellerNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		final String[] yells = {
			"HEYOH! Grocery stuff here at the market... Come closer, have a look!",
			"HOYEH! I have all the stuff to prepare a decent meal and more!",
			"HAYAH! Is this a market or a cemetery?!... It seems so quiet around here..."
		};
		new MonologueBehaviour(buildNPC(zone), yells, 3);
	}

	private SpeakerNPC buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Ambrogita") {

			@Override
			public void createDialog() {
				addGreeting(
					"Hello visitor! You do not look familiar... " +
					"If you came looking for grocery stuff, I #offer grocery stuff... " +
					"Oh, I should really set up one of those blackboards where offers are listed!");

				addOffer(
					"Oh, I mostly sell grocery stuff... " +
					"Eggs, potatos, onions, garlic, good pinto beans, " +
					"real hot habanero pepper, " +
					"some olive oil or vinegar... " +
					"herbs like sclaria or kekik..." +
					"I have sugar of course... Got plenty of that! " +
					"And honey too if you really need it..." +
					"If you want to #buy some stuff, tell me what you need... " +
					"Oh, I should really set up one of those blackboards where offers are listed!");
					//Offered items:
					final Map<String, Integer> offerings = new HashMap<String, Integer>();
					offerings.put("egg", 50);
					offerings.put("onion", 50);
					offerings.put("garlic", 50);
					offerings.put("potato", 50);
					offerings.put("pinto beans", 50);
					offerings.put("habanero pepper", 135);
					offerings.put("olive oil", 135);
					offerings.put("vinegar", 135);
					offerings.put("kekik", 135);
					offerings.put("sclaria", 135);
					offerings.put("sugar", 250);
					offerings.put("honey", 350);
					new SellerAdder().addSeller(this, new SellerBehaviour(offerings), false);

				addJob(
					"I am here to #offer grocery stuff to travelers like you... " +
					"If you want to #buy, tell me... " +
					"Oh, I should really set up one of those blackboards where offers are listed!");

				addHelp(
					"If you need some grocery stuff, I do #offer some grocery stuff... " +
					"When you want to #buy something, tell me... " +
					"Oh, I should really set up one of those blackboards where offers are listed");

				addGoodbye(
					"So long... " +
					"Oh, I should really set up one of those blackboards where offers are listed");
			}

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(20, 30));
				nodes.add(new Node(20, 35));
				nodes.add(new Node(14, 35));
				nodes.add(new Node(13, 31));
				setPath(new FixedPath(nodes, true));
			}
		};

		// Finalize Ambrogita, Ados Market Grocery Seller NPC
		npc.setEntityClass("adosmarketgrocerysellernpc");
		npc.setPosition(16, 33);
		npc.setCollisionAction(CollisionAction.REVERSE);
		npc.setDescription("You see Ambrogita, a busy marketplace seller...");
		zone.add(npc);
		return npc;

	}
}
