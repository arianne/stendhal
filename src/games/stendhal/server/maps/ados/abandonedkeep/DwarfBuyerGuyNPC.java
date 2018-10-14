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
package games.stendhal.server.maps.ados.abandonedkeep;

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

/**
 * Inside Ados Abandoned Keep - level -3 .
 */
public class DwarfBuyerGuyNPC implements ZoneConfigurator  {

    private final ShopList shops = SingletonRepository.getShopList();

	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Ritati Dragontracker") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(25,32));
				nodes.add(new Node(38,32));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			public void createDialog() {

				addGreeting("What do you want?");
				addJob("I buy odds and ends. Somebody has to do it.");
				addHelp("Look at me! I am reduced to buying trinkets! How can I help YOU?");
				addOffer("Don't bother me unless you have something I want! Check the blackboard for prices.");
				addQuest("Unless you want to #own this place, you cannot do anything for me.");
				addGoodbye("Be off with you!");
			    addReply("own", "What? Why you couldn't even begin to come up with enough money for that!");
			    // see games.stendhal.server.maps.quests.mithrilcloak.GettingTools for further behaviour
			    addReply("buy", "I don't sell anything but you can look at my blackboard for what I buy. Or ask about #specials.");
			    addReply("YOU", "Yes, I am talking to YOU! Who else would I be talking to!");

				new BuyerAdder().addBuyer(this, new BuyerBehaviour(shops.get("buyoddsandends")), false);
			}};

			npc.setPosition(25, 32);
			npc.setCollisionAction(CollisionAction.STOP);
			npc.setEntityClass("olddwarfnpc");
			npc.setDescription("You see Ritati Dragontracker who buys odds end ends.");
			zone.add(npc);
	}
}
