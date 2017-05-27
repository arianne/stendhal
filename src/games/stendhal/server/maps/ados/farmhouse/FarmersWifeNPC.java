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
package games.stendhal.server.maps.ados.farmhouse;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;

/**
 * NPC to sell milk.
 *
 * @author kymara
 */
public class FarmersWifeNPC implements ZoneConfigurator {

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

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Philomena") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(27, 4));
				nodes.add(new Node(33, 4));
				nodes.add(new Node(33, 10));
				nodes.add(new Node(27, 10));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Goeden dag!");
				addJob("My husband runs this farm, and mostly I look after his younger sister and her boy, they are upstairs. If you could check on them that'd be a help, I heard her crying earlier.");
				addQuest("If you can write Junit tests then my daughter needs you. Just ask Diogenes how to help the project.");
				addHelp("I can sell you a bottle of milk or some butter from our dairy cows if you like.");
				final Map<String, Integer> offerings = new HashMap<String, Integer>();
				offerings.put("milk", 30);
				offerings.put("butter", 40);
				new SellerAdder().addSeller(this, new SellerBehaviour(offerings));

				addGoodbye("Tot ziens.");
			}
		};
		npc.setEntityClass("wifenpc");
		npc.setPosition(27, 4);
		npc.initHP(100);
		npc.setDescription("You see Philomena. She smells a bit of cows, but her milk is unique.");
	    zone.add(npc);
	}
}
