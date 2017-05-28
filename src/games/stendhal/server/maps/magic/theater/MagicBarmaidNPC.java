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
package games.stendhal.server.maps.magic.theater;

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

/*
 * Inside Magic Theater)
 */
public class MagicBarmaidNPC implements ZoneConfigurator {
	private final ShopList shops = SingletonRepository.getShopList();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildmagicbarmaid(zone);
	}

	private void buildmagicbarmaid(final StendhalRPZone zone) {
		final SpeakerNPC magicbarmaid = new SpeakerNPC("Trillium") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(13, 3));
				nodes.add(new Node(19, 3));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hi. Hope you are enjoying our wonderful theater.");
				addJob("I sell the most delectable foods in Magic City.");
				addHelp("If you are hungry, check out the blackboard for foods and prices.");
				new SellerAdder().addSeller(this, new SellerBehaviour(shops.get("sellmagic")), false);
				addOffer("See the blackboard for my prices.");
				addQuest("I have no need of your help, thanks.");
				addReply("licorice", "Poor Baldemar, he is so very alergic to licorice.");
				addGoodbye("Great to see you. Come again.");
			}
		};

		magicbarmaid.setEntityClass("woman_015_npc");
		magicbarmaid.setPosition(13, 3);
		magicbarmaid.initHP(100);
		magicbarmaid.setDescription("You see Trillium. She is the Magic City barmaid of the theater and offers nice drinks and food.");
		zone.add(magicbarmaid);
	}
}
