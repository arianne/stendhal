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
package games.stendhal.server.maps.ados.magician_house;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;

public class WizardNPC implements ZoneConfigurator {
	private final ShopList shops = SingletonRepository.getShopList();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildMagicianHouseArea(zone);
	}

	private void buildMagicianHouseArea(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Haizen") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(7, 2));
				nodes.add(new Node(7, 4));
				nodes.add(new Node(13, 4));
				nodes.add(new Node(13, 9));
				nodes.add(new Node(9, 9));
				nodes.add(new Node(9, 8));
				nodes.add(new Node(9, 9));
				nodes.add(new Node(2, 9));
				nodes.add(new Node(2, 3));
				nodes.add(new Node(7, 3));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I am a wizard who sells #magic #scrolls. Just ask me for an #offer!");
				addHelp("You can take powerful magic with you on your adventures with the aid of my #magic #scrolls!");

				new SellerAdder().addSeller(this, new SellerBehaviour(shops.get("scrolls")));

				add(
				        ConversationStates.ATTENDING,
				        Arrays.asList("magic", "scroll", "scrolls", "magic scrolls"),
				        null,
				        ConversationStates.ATTENDING,
				        "I #offer scrolls that help you to travel faster: #'home scrolls' and the #markable #'empty scrolls'. For the more advanced customer, I also have #'summon scrolls'!",
				        null);
				add(ConversationStates.ATTENDING, Arrays.asList("home", "home scroll"), null,
				        ConversationStates.ATTENDING,
				        "Home scrolls take you home immediately, a good way to escape danger!", null);
				add(
				        ConversationStates.ATTENDING,
				        Arrays.asList("empty", "marked", "empty scroll", "markable", "marked scroll"),
				        null,
				        ConversationStates.ATTENDING,
				        "Empty scrolls are used to mark a position. Those marked scrolls can take you back to that position. They are a little expensive, though.",
				        null);
				add(
				        ConversationStates.ATTENDING,
				        "summon",
				        null,
				        ConversationStates.ATTENDING,
				        "A summon scroll empowers you to summon animals to you; advanced magicians will be able to summon stronger monsters than others. Of course, these scrolls can be dangerous if misused.",
				        null);

				addGoodbye();
			}
		};

		npc.setEntityClass("wisemannpc");
		npc.setPosition(7, 2);
		npc.initHP(100);
		npc.setDescription("You see the mighty magician Haizen. He is able to let people teleport around with his scrolls.");
		zone.add(npc);
	}
}
