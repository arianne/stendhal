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
package games.stendhal.server.maps.fado.forest;

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
import games.stendhal.server.entity.npc.behaviour.impl.QuestCompletedBuyerBehaviour;


/**
 * Builds an albino elf NPC .
 * He is a trader and takes part in a quest (maps/quests/ElvishArmor.java)
 *
 * @author kymara
 */
public class CraftsmanNPC implements ZoneConfigurator {
	private final ShopList shops = SingletonRepository.getShopList();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Lupos") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(3, 11));
				nodes.add(new Node(12, 11));
				nodes.add(new Node(12, 2));
				nodes.add(new Node(7, 2));
				nodes.add(new Node(7, 6));
				nodes.add(new Node(3, 6));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
			    //addGreeting("Welcome to this forest, south of Or'ril river.");
			        addJob("I'm a craftsman. One day I hope to craft such items as the green elves can make.");
				addHelp("My friend Orchiwald is a great story teller, he would speak with you about the albino elves and how we come to be here.");
				new BuyerAdder().addBuyer(this, new QuestCompletedBuyerBehaviour("elvish_armor", "I'm not able to buy anything from you right now.", shops.get("buyelvish")), false);
 				addGoodbye("Bye.");
			}
		};

		npc.setDescription("You see Lupos, an albino elf.");
		npc.setEntityClass("albinoelfnpc");
		npc.setPosition(3, 11);
		npc.initHP(100);
		npc.setDescription("You see Lupos. As you can see, he is an albino elf.");
		npc.setCollisionAction(CollisionAction.STOP);
		zone.add(npc);
	}
}
