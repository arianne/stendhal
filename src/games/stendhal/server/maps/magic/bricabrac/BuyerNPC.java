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
package games.stendhal.server.maps.magic.bricabrac;

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
 * Builds an witch NPC She is a trader for bric-a-brac items.
 *
 * @author kymara
 */
public class BuyerNPC implements ZoneConfigurator {
	private final ShopList shops = SingletonRepository.getShopList();

	/**
	 * Configure a zone.
	 *
	 * @param zone
	 *            The zone to be configured.
	 * @param attributes
	 *            Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Vonda") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(4, 12));
				nodes.add(new Node(12, 12));
				nodes.add(new Node(12, 8));
				nodes.add(new Node(27, 8));
				nodes.add(new Node(27, 5));
				nodes.add(new Node(27, 10));
				nodes.add(new Node(8, 10));
				nodes.add(new Node(8, 12));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hello.");
				addJob("I potter around collecting odds and bobs. Sometimes I sell items, but mostly I like to keep them. If you have any relics to #trade, I would be very happy indeed.");
				addHelp("I could tell you about some of these wonderful items here. The white #pot, #coffins, #dress, #shield, #armor, #tools, #rug, #flowers, #clock and #'sewing machine' are all fascinating!");
				addReply(
						"pot",
						"You mean the white and blue one, the oriental pot, I suppose. That is an original made by the ancient oni people. It's very rare.");
				addReply(
						"coffins",
						"Those coffins were looted from some underground catacombs, I had to pay a pretty price for that pair.");
				addReply(
						"dress",
						"I do love that beautiful pink dress. I am told it was worn by the elven princess Tywysoga.");
				addReply(
						"shield",
						"That is a truly fearsome shield, is it not? There is some enscription on the back about devil knights, but I am afraid I do not understand it.");
				addReply(
						"rug",
						"That is a genuine rug from the far East. I have never seen one like it, only cheap copies. Please don't get muddy footprints on it!");
				addReply(
						"flowers",
						"Ah ha! These are flowers grown with elf magic. I bought them myself from a wonderful florist in Nalwor.");
				addReply(
						"clock",
						"That grandfather clock is one of my more modern pieces. If you know Woody the Woodcutter, you may recognise the handiwork.");
				addReply(
						"tools",
						"Those tools on the back wall are a true antique! They were used by the great grandfather of Xoderos of Semos, isn't that incredible!");
				addReply(
						"armor",
						"Ah, that mighty piece was made in Deniran. I'm afraid I know little more about it.");
				addReply(
						"sewing machine",
						"Oh you know that is my favourite. It was made by a man called Zinger, and it still works just as well as the day it was made.");
				addQuest("I have no favour to ask of you.");
				new BuyerAdder().addBuyer(this, new BuyerBehaviour(shops.get("buymagic")), false);
				addOffer("There is a list of prices for relics and magic items I would buy, over on that large book.");
				addGoodbye("Bye.");
			}
		};

		npc.setDescription("You see Vonda, a witch who seems to like clutter...");
		npc.setEntityClass("witch2npc");
		npc.setPosition(4, 12);
		npc.initHP(100);
		zone.add(npc);
	}
}
