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
package games.stendhal.server.maps.orril.magician_house;

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
import games.stendhal.server.entity.npc.behaviour.adder.HealerAdder;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;

/**
 * Configure Orril Jynath House (Inside/Level 0).
 */
public class WitchNPC implements ZoneConfigurator {
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
		buildJynathHouse(zone);
	}

	private void buildJynathHouse(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Jynath") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(24, 7));
				nodes.add(new Node(21, 7));
				nodes.add(new Node(21, 9));
				nodes.add(new Node(15, 9));
				nodes.add(new Node(15, 12));
				nodes.add(new Node(13, 12));
				nodes.add(new Node(13, 27));
				nodes.add(new Node(22, 27));
				nodes.add(new Node(13, 27));
				nodes.add(new Node(13, 12));
				nodes.add(new Node(15, 12));
				nodes.add(new Node(15, 9));
				nodes.add(new Node(21, 9));
				nodes.add(new Node(21, 7));
				nodes.add(new Node(24, 7));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I'm a witch, since you ask. I grow #collard as a hobby.");
				addReply("collard",	"That cabbage in the pot. Be careful of it!");
				/*
				 * addHelp("You may want to buy some potions or do some #task
				 * for me.");
				 */
				addHelp("I can #heal you, and I can #offer you powerful #scrolls that are #magic.");
				new SellerAdder().addSeller(this, new SellerBehaviour(shops.get("scrolls")));
				new HealerAdder().addHealer(this, 250);
				add(
				        ConversationStates.ATTENDING,
				        Arrays.asList("magic", "scroll", "scrolls"),
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
				addGoodbye("Goodbye - and careful not to touch that orb, it leads somewhere very dangerous!");
			}
		};

		npc.setEntityClass("witchnpc");
		npc.setShadowStyle("48x64_float");
		npc.setPosition(24, 7);
		npc.initHP(100);
		npc.setDescription("You see Jynath, the witch. She is riding on a broom.");
		npc.setSounds(Arrays.asList("witch-cackle-1"));
		zone.add(npc);
	}
}
