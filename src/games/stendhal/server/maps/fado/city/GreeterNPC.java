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
package games.stendhal.server.maps.fado.city;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;

/**
 * Builds the city greeter NPC.
 *
 * @author timothyb89
 */
public class GreeterNPC implements ZoneConfigurator {
	private final ShopList shops = SingletonRepository.getShopList();

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
	// OL0_GreeterNPC
	//

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC greeterNPC = new SpeakerNPC("Xhiphin Zohos") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(39, 29));
				nodes.add(new Node(23, 29));
				nodes.add(new Node(23, 21));
				nodes.add(new Node(40, 21));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hello! Welcome to Fado City! You can #learn about Fado from me.");
				addReply("learn",
				        "Fado guards the bridge over Or'ril river which is vital for the commercial route between #Deniran and Ados. There's an active social life here, being the preferred city for celebrating marriages and tasting elaborate meals.");
				addReply("Deniran",
				        "Deniran is the jewel of the crown. Deniran is the center of Faiumoni and supports the army that tries to defeat enemies that wants to conquer Faiumoni.");
				addJob("I greet all of the new-comers to Fado. I can #offer you a scroll if you'd like to come back here again.");
				addHelp("You can head into the tavern to buy food and drinks. You can also visit the people in the houses, or visit the blacksmith or the city hotel.");
				new SellerAdder().addSeller(this, new SellerBehaviour(shops.get("fadoscrolls")));
				addGoodbye("Bye.");
			}
		};

		greeterNPC.setOutfit(1, 6, 1, null, 0, null, 5, null, 0);
		greeterNPC.setPosition(39, 29);
		greeterNPC.initHP(1000);
		greeterNPC.setDescription("You see Xhiphin Zohos. He is a helpful citizen of Fado.");
		zone.add(greeterNPC);
	}
}
