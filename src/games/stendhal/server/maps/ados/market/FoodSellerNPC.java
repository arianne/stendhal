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
package games.stendhal.server.maps.ados.market;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
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
 * Food and drink seller,  at Ados Market
 */
public class FoodSellerNPC implements ZoneConfigurator {
	private final ShopList shops = SingletonRepository.getShopList();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		final SpeakerNPC npc = new SpeakerNPC("Adena") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(31, 8));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("We just got fresh apples and carrots from several #farms near Semos!");
				addReply(Arrays.asList("Semos Farm", "Semos", "Farm", "farms"), "We get all our food from different farms near Semos, but the route is #dangerous.");
				addReply(Arrays.asList("dangerous", "expensive"), "With all the soldiers fighting in the great battle, the route to Semos is left unprotected. So I am afraid, the prices are relatively high.");
				addJob("I sell goods from the #farms near Semos as soon as we get them.");
				new SellerAdder().addSeller(this, new SellerBehaviour(shops.get("adosfoodseller")));
				addGoodbye();
			}
		};

		npc.setEntityClass("marketsellernpc");
		npc.setDescription("Adena smiles towards you in a friendly way, although she is working very hard.");
		npc.setPosition(31, 8);
		npc.initHP(100);
		npc.setDirection(Direction.DOWN);
		zone.add(npc);
	}
}
