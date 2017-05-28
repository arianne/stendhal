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
package games.stendhal.server.maps.kirdneh.city;

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
 * In Kirdneh open market .
 */
public class KirdnehFishyMarketNPC implements ZoneConfigurator {
    private final ShopList shops = SingletonRepository.getShopList();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildfishyguy(zone);
	}

	private void buildfishyguy(final StendhalRPZone zone) {
		final SpeakerNPC fishyguy = new SpeakerNPC("Fishmonger") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(63, 89));
				nodes.add(new Node(63, 88));
				nodes.add(new Node(64, 88));
				nodes.add(new Node(64, 87));
				nodes.add(new Node(68, 87));
				nodes.add(new Node(68, 89));
				setPath(new FixedPath(nodes, true));

			}

			@Override
			protected void createDialog() {
				addGreeting("Ahoy, me hearty! Back from yer swashbucklin, ah see.");
				addJob("By the Powers! I be buyin. You be sellin?");
				addReply("yes", "Well, shiver me timbers! Check out that blackboard o'er thar fer me prices an' what i be buyin");
				addReply("aye", "Well, shiver me timbers! Check out that blackboard o'er thar fer me prices an' what i be buyin");
				addReply("no", "You lily-livered scallywag! Why ye be wastin me time?");
				addHelp("An' just what do ya think a buccanneer such as meself could possibly help ye with?");
				new BuyerAdder().addBuyer(this, new BuyerBehaviour(shops.get("buyfishes")), false);
				addOffer("Check out that thar blackboard fer how many dubloons I be givin.");
				addQuest("Ye don't ha'e the guts ta do whut I need done.");
				addGoodbye("Arrgh, avast an' be gone with ye!");

			}
		};

		fishyguy.setEntityClass("sailor1npc");
		fishyguy.setPosition(63, 89);
		fishyguy.initHP(100);
		fishyguy.setDescription("You see a Fishmonger. He stinks a bit from the fish he buys.");
		zone.add(fishyguy);
	}
}
