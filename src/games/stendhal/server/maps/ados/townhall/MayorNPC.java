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
package games.stendhal.server.maps.ados.townhall;

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
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;

/**
 * Builds ados mayor NPC.
 * He may give an items quest later
 * Now he sells ados scrolls
 * @author kymara
 */
public class MayorNPC implements ZoneConfigurator {
	private final ShopList shops = SingletonRepository.getShopList();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildMayor(zone);
	}

	private void buildMayor(final StendhalRPZone zone) {
		final SpeakerNPC mayor = new SpeakerNPC("Mayor Chalmers") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(3, 9));
				nodes.add(new Node(8, 9));
				nodes.add(new Node(8, 16));
				nodes.add(new Node(25, 16));
				nodes.add(new Node(25, 13));
				nodes.add(new Node(37, 13));
				nodes.add(new Node(25, 13));
				nodes.add(new Node(25, 16));
				nodes.add(new Node(8, 16));
				nodes.add(new Node(8, 9));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("On behalf of the citizens of Ados, welcome.");
				addJob("I'm the mayor of Ados. I can #offer you the chance to return here easily.");
				addHelp("Ask me about my #offer to return here.");
				//addQuest("I don't know you well yet. Perhaps later in the year I can trust you with something.");
				new SellerAdder().addSeller(this, new SellerBehaviour(shops.get("adosscrolls")));
				addGoodbye("Good day to you.");
			}
		};

		mayor.setDescription("You see the respected mayor of Ados.");
		mayor.setEntityClass("badmayornpc");
		mayor.setPosition(3, 9);
		mayor.setCollisionAction(CollisionAction.STOP);
		mayor.initHP(100);
		zone.add(mayor);
	}
}
