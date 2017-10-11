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
package games.stendhal.server.maps.amazon.fleamarket;

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
 * In Amazon Island ne .
 */
public class AmazonMermaidArmorNPC implements ZoneConfigurator {
    private final ShopList shops = SingletonRepository.getShopList();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildmermaid(zone);
	}

	private void buildmermaid(final StendhalRPZone zone) {
		final SpeakerNPC mermaid = new SpeakerNPC("Nicklesworth") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(8, 92));
				nodes.add(new Node(9, 92));
				nodes.add(new Node(9, 93));
				nodes.add(new Node(11, 93));
				nodes.add(new Node(11, 94));
				nodes.add(new Node(13, 94));
				nodes.add(new Node(13, 96));
				nodes.add(new Node(14, 96));
				nodes.add(new Node(14, 98));
				nodes.add(new Node(16, 98));
				nodes.add(new Node(16, 97));
				nodes.add(new Node(15, 97));
				nodes.add(new Node(15, 95));
				nodes.add(new Node(14, 95));
				nodes.add(new Node(14, 94));
				nodes.add(new Node(13, 94));
				nodes.add(new Node(13, 93));
				nodes.add(new Node(12, 93));
				nodes.add(new Node(12, 92));
				nodes.add(new Node(10, 92));
				nodes.add(new Node(10, 91));
				nodes.add(new Node(9, 91));
				nodes.add(new Node(9, 82));
				nodes.add(new Node(8, 82));
				setPath(new FixedPath(nodes, true));

			}

			@Override
			protected void createDialog() {
				addGreeting("Howdy! You've come a very long way to be here. Welcome.");
				addJob("I buy good, quality cloaks. Can't get these women to wear them yet, but I am trying.");
				addHelp("Not much I can really help you with unless you have some of the cloaks I am looking for.");
				new BuyerAdder().addBuyer(this, new BuyerBehaviour(shops.get("buyamazoncloaks")), false);
				addOffer("I hate to say it, but look at the blackboard over yonder to see my prices and what I buy.");
				addQuest("There's not a thing you can do for me, thanks.");
				addGoodbye("Bye. Don't you just HATE this place? ;) Say hey to the amazoness giant while you are here.");

			}
		};

		mermaid.setEntityClass("marmaidnpc");
		mermaid.setPosition(8, 92);
		mermaid.initHP(100);
		mermaid.setDescription("You see Nicklesworth. Isn't she a lovely mermaid?");
		zone.add(mermaid);
	}
}
