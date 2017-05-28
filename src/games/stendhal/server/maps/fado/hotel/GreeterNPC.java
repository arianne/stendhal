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
package games.stendhal.server.maps.fado.hotel;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Builds the hotel greeter NPC.
 *
 * @author timothyb89
 */
public class GreeterNPC implements ZoneConfigurator {
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
	// IL0_GreeterNPC
	//

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC greeterNPC = new SpeakerNPC("Linda") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(16, 50));
				nodes.add(new Node(27, 50));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hello! Welcome to the Fado City Hotel! Can I #help you?");

				addJob("I am the hotel receptionist.");
				addHelp("When the building work on the hotel rooms is complete you will be able to #reserve one.");
				//addHelp("You can #reserve a room if you'd like, or #explore the hotel.");
				addReply("reserve",
				        "Sorry, but the hotel is still under construction and you can not reserve a room yet. You can #explore the rest.");
				addReply("explore", "I'm afraid there is not very much to see, yet. The hotel is still being finished.");
				//addSeller(new SellerBehaviour(shops.get("food&drinks")));
				addGoodbye("Bye.");
			}
		};

		greeterNPC.setEntityClass("hotelreceptionistnpc");
		greeterNPC.setPosition(16, 50);
		greeterNPC.initHP(1000);
		greeterNPC.setDescription("You see Linda. She belongs to the friendly hotel staff and is responsible for the room reservations.");
		zone.add(greeterNPC);
	}
}
