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
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;

/*
 * Food and drink seller,  Inside Semos Tavern - Level 0 (ground floor)
 * Sells the flask required for Tad's quest IntroducePlayers
 */
public class BarMaidNPC implements ZoneConfigurator {
	private final ShopList shops = SingletonRepository.getShopList();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildMargaret(zone);
	}

	private void buildMargaret(final StendhalRPZone zone) {
		final SpeakerNPC margaret = new SpeakerNPC("Margaret") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(11, 4));
				nodes.add(new Node(18, 4));
				nodes.add(new Node(18, 3));
				nodes.add(new Node(11, 3));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addReply("flask", "If you wish to buy a flask please just tell me: #buy #flask. Or, you can ask what else I #offer.");
				addQuest("Oh nice that you ask me. Unfortunately I have nothing to do for you.");
				addJob("I am the bar maid for this fair tavern. You can #buy both imported and local beers, and fine food.");
				addHelp("This tavern is a great place to take a break and meet new people! Just ask if you want me to #offer you a drink.");
				new SellerAdder().addSeller(this, new SellerBehaviour(shops.get("food&drinks")));

				addGoodbye();
			}
		};

		//coupon for free beer

        margaret.add(ConversationStates.ATTENDING,
                (Arrays.asList("coupon", "coupons", "beer coupon", "free beer")),
                new PlayerHasItemWithHimCondition("coupon"),
                ConversationStates.ATTENDING,
                "Oh you found one of the coupons which I spread around some time ago. Enjoy the beer!",
                new MultipleActions(new DropItemAction("coupon"),
                					new EquipItemAction("beer"))
                );

        margaret.add(ConversationStates.ATTENDING,
        		(Arrays.asList("coupon", "coupons", "beer coupon", "free beer")),
                new NotCondition(new PlayerHasItemWithHimCondition("coupon")),
                ConversationStates.ATTENDING,
                "Don't lie, you don't own one of the rare coupons. It's hard to run a tavern nowadays, don't lie to me!",
                null
                );

		margaret.setEntityClass("tavernbarmaidnpc");
		margaret.setDescription("Margaret looks so warm and welcoming that you can't help but want to buy something from her.");
		margaret.setPosition(11, 4);
		margaret.initHP(100);
		margaret.setSounds(Arrays.asList("hiccup-01"));
		zone.add(margaret);
	}
}
