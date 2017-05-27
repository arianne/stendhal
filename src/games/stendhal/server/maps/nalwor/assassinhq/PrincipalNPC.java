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
package games.stendhal.server.maps.nalwor.assassinhq;

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
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;

/**
 * Inside Nalwor Assassin Headquarters - Level 0 .
 */
public class PrincipalNPC implements ZoneConfigurator {
    private final ShopList shops = SingletonRepository.getShopList();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildprincipal(zone);
	}

	private void buildprincipal(final StendhalRPZone zone) {
		final SpeakerNPC principal = new SpeakerNPC("Femme Fatale") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(2, 18));
				nodes.add(new Node(2, 14));
				nodes.add(new Node(3, 14));
				nodes.add(new Node(3, 18));
				setPath(new FixedPath(nodes, true));

			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("It is my job to keep all my little guys armed and equipped. Please help me.");
				addHelp("I can buy your surplus items.  Please see blackboard on wall for what I need.");
				addOffer("Look at blackboard on wall to see my offer.");
				addQuest("Other than selling me what I need, I don't require anything from you.");
				addGoodbye();
 				new BuyerAdder().addBuyer(this, new BuyerBehaviour(shops.get("buy4assassins")), false);
			}
		};

		principal.setEntityClass("principalnpc");
		principal.setPosition(2, 18);
		principal.initHP(100);
		principal.setDescription("The lady walking around is Femme Fatale. She is always on the search for armor and weapons.");
		zone.add(principal);
	}
}
