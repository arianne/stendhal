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
package games.stendhal.server.maps.kirdneh.city;

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
 * In Kirdneh open market .
 */
public class KirdnehArmorGuyNPC implements ZoneConfigurator {
    private final ShopList shops = SingletonRepository.getShopList();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildlawrence(zone);
	}

	private void buildlawrence(final StendhalRPZone zone) {
		final SpeakerNPC lawrence = new SpeakerNPC("Lawrence") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(63, 95));
				nodes.add(new Node(64, 95));
				nodes.add(new Node(64, 93));
				nodes.add(new Node(70, 93));
				nodes.add(new Node(70, 95));
				nodes.add(new Node(71, 95));
				nodes.add(new Node(71, 93));
				nodes.add(new Node(63, 93));
				setPath(new FixedPath(nodes, true));

			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I buy quality armor at a fair price.");
				addHelp("Look at the blackboard for what I buy and prices.");
				new BuyerAdder().addBuyer(this, new BuyerBehaviour(shops.get("buykirdneharmor")), false);
				addOffer("Look at the blackboard to see my prices and what I buy.");
				addQuest("I have no task for you.");
				addGoodbye("If you ever find a tooth from the mythical black dragon, please let me know.");

			}
		};

		lawrence.setEntityClass("man_002_npc");
		lawrence.setPosition(63, 95);
		lawrence.initHP(100);
		lawrence.setDescription("You see Lawrence. His working place is the market.");
		zone.add(lawrence);
	}
}
