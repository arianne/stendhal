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
package games.stendhal.server.maps.wofol.house4;

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
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;

/*
 * Inside the Kobold City, interior called house4
 */
public class TraderNPC implements ZoneConfigurator {
	private final ShopList shops = SingletonRepository.getShopList();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildTrader(zone);
	}

	private void buildTrader(final StendhalRPZone zone) {
		final SpeakerNPC trader = new SpeakerNPC("Wrvil") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(4, 4));
				nodes.add(new Node(4, 9));
				nodes.add(new Node(12, 9));
				nodes.add(new Node(12, 4));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Welcome to the Kobold City of Wofol. I hope you come in peace.");
				addJob("I run a buying and selling #trade with kobolds - or whoever else passes by. I am one of the few Kobolds who can speak with non-Kobolds.");
				addHelp("I #deal in all sorts of items.");
				addQuest("Try Alrak the mountain dwarf who lives here with the kobolds. He'd probably have more than one task to give you.");
				new SellerAdder().addSeller(this, new SellerBehaviour(shops.get("sellstuff2")), false);
				new BuyerAdder().addBuyer(this, new BuyerBehaviour(shops.get("buystuff2")), false);
				addOffer("Please look at the each blackboard on the wall to see what I buy and sell at the moment.");
				addGoodbye("Bye, and please don't attack too many of my friends.");

			}
		};

		trader.setEntityClass("../monsters/kobold/veteran_kobold");
		trader.setPosition(4, 4);
		trader.initHP(100);
		trader.setDescription("You see Wrvil. He can equip and maybe make you rich at the same time.");
		zone.add(trader);
	}
}
