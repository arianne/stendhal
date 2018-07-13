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
package games.stendhal.server.maps.orril.dwarfmine;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.CollisionAction;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;

/*
 * Configure Orril Dwarf Mine (Underground/Level -2).
 */
public class IronBuyerNPC implements ZoneConfigurator {
	private final ShopList shops;

	public IronBuyerNPC() {
		this.shops = SingletonRepository.getShopList();
	}

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildDwarfMineArea(zone);
	}

	private void buildDwarfMineArea(final StendhalRPZone zone) {
		// NOTE: This is a female character ;)
		final SpeakerNPC loretta = new SpeakerNPC("Loretta") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(49, 68));
				nodes.add(new Node(45, 68));
				nodes.add(new Node(45, 72));
				nodes.add(new Node(45, 68));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I'm the supervisor responsible for maintaining the mine-cart rails in this mine. But, ironically, we ran out of cast iron to fix them with! Maybe you can #offer me some?");
				addHelp("If you want some good advice, you'll not go further south; there's an evil dragon living down there!");
				new BuyerAdder().addBuyer(this, new BuyerBehaviour(shops.get("buyiron")), true);
				addGoodbye("Farewell - and be careful: the other dwarves don't like strangers running around here!");
			}
		};

		loretta.setDescription("You see Loretta, an elderly female dwarf. She is working on the mine-cart rails.");
		loretta.setEntityClass("greendwarfnpc");
		loretta.setPosition(49, 68);
		loretta.setCollisionAction(CollisionAction.STOP);
		loretta.initHP(100);
		zone.add(loretta);
	}
}
