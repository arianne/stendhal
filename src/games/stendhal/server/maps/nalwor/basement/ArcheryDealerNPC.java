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
package games.stendhal.server.maps.nalwor.basement;

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
 * Inside Nalwor Inn basement .
 */

public class ArcheryDealerNPC implements ZoneConfigurator  {

	private final ShopList shops = SingletonRepository.getShopList();

	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Merenwen") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(10,5));
				nodes.add(new Node(16,5));
				nodes.add(new Node(16,6));
				nodes.add(new Node(10,6));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			public void createDialog() {
				addGreeting("Well met, kind stranger.");
				addJob("I buy archery equipment for our village.");
				addHelp("I can offer you no help. Sorry.");
				addOffer("Check the blackboard for prices.");
				addQuest("I have no quest for you.");
				addGoodbye("Have a happy. Bye.");
				new BuyerAdder().addBuyer(this, new BuyerBehaviour(shops.get("buyarcherstuff")), false);
			}};
			npc.setPosition(10, 5);
			npc.setDescription("You see the beautiful mage elf Merenwen. She buys some nice archery stuff.");
			npc.setEntityClass("mageelfnpc");
			zone.add(npc);
	}
}
