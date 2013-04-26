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
package games.stendhal.server.maps.semos.tavern;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
 * Inside Semos Tavern - Level 1 (upstairs)
 */
public class RareWeaponsSellerNPC implements ZoneConfigurator {
	private final ShopList shops = SingletonRepository.getShopList();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildMcPegleg(zone);
	}

	private void buildMcPegleg(final StendhalRPZone zone) {
		// Adding a new NPC that buys some of the stuff that Xin doesn't
		final SpeakerNPC mcpegleg = new SpeakerNPC("McPegleg") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(16, 3));
				nodes.add(new Node(13, 3));
				nodes.add(new Node(13, 2));
				nodes.add(new Node(13, 3));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Yo matey! You look like you need #help.");
				addJob("I'm a trader of ... let's say ... #rare things.");
				addHelp("Not sure if I can trust you .... a #pirate with a bandy #leg has got to keep his #eye on new people.");
				addQuest("Perhaps if you find some #rare #armor or #weapon ...");
				addGoodbye("I see you!");
				add(ConversationStates.ATTENDING, Arrays.asList("weapon", "armor", "rare", "rare armor"),
				        ConversationStates.ATTENDING,
				        "Ssshh! I'm occasionally buying rare weapons and armor. Got any? Ask for my #offer", null);
				addOffer("Have a look at the blackboard on the wall to see my offers.");
				add(ConversationStates.ATTENDING, Arrays.asList("eye", "leg", "wood", "patch"),
				        ConversationStates.ATTENDING, "Not every day is a lucky day ...", null);
				add(ConversationStates.ATTENDING, "pirate", null, ConversationStates.ATTENDING,
				        "That's none of your business!", null);
				new BuyerAdder().addBuyer(this, new BuyerBehaviour(shops.get("buyrare")), false);
			}
		};

		// Add some atmosphere
		mcpegleg.setDescription("You see a dubious man with a patched eye and a wooden leg.");

		// Add our new NPC to the game world
		mcpegleg.setEntityClass("pirate_sailornpc");
		mcpegleg.setPosition(16, 3);
		mcpegleg.initHP(100);
		zone.add(mcpegleg);
	}
}
