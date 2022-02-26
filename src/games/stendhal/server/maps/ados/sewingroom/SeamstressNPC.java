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
package games.stendhal.server.maps.ados.sewingroom;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.CloneManager;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;

/*
 * Ados City, house with a woman who makes sails for the ships
 */
public class SeamstressNPC implements ZoneConfigurator {
	private final ShopList shops = SingletonRepository.getShopList();

	// clone to be used in twilight zone
	private static SpeakerNPC clone;

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildSeamstress(zone);
	}

	private void buildSeamstress(final StendhalRPZone zone) {
		final SpeakerNPC seamstress = new SpeakerNPC("Ida") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(7, 7));
				nodes.add(new Node(7, 14));
				nodes.add(new Node(12, 14));
				nodes.add(new Node(12, 7));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hello there.");
				addJob("I'm a seamstress. I make sails for ships, like the Athor ferry. If you could #offer me material I'd be grateful.");
				addHelp("If you want to go to the island Athor on the ferry, just go south once you've departed from Ados, and look for the pier.");
				new BuyerAdder().addBuyer(this, new BuyerBehaviour(shops.get("buycloaks")), false);
				addOffer("I buy cloaks, because we are short of material to make sails with. The better the material, the more I pay. My notebook on the table has the price list.");
				addGoodbye("Bye, thanks for stepping in.");
			}
		};

		seamstress.setEntityClass("woman_002_npc");
		seamstress.setPosition(7, 7);
		seamstress.initHP(100);
		seamstress.setDescription("Ida is a well known seamstress in the shipping industry. Although she is more into ships, she could maybe help you as well.");
		zone.add(seamstress);

		// initialize clone to be placed in twilight zone
		clone = CloneManager.get().clone(seamstress, "twilight_ida");
	}

	/**
	 * Retrieves the cloned instance.
	 *
	 * @return
	 * 		SpeakerNPC
	 */
	public static SpeakerNPC getClone() {
		return clone;
	}
}
