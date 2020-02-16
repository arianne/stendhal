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
package games.stendhal.server.maps.mithrilbourgh.stores;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.mapstuff.sign.Sign;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;

/**
 * Builds an NPC to buy previously un bought weapons.
 * He is the QM of the Mithrilbourgh Army, who are short of boots and helmets
 *
 * @author kymara
 */
public class BuyerNPC implements ZoneConfigurator {
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
		final SpeakerNPC npc = new SpeakerNPC("Diehelm Brui") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(10, 4));
				nodes.add(new Node(10, 8));
				nodes.add(new Node(15, 8));
				nodes.add(new Node(15, 2));
				nodes.add(new Node(3, 2));
				nodes.add(new Node(3, 7));
				nodes.add(new Node(10, 7));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Welcome to the supply stores for the Mithrilbourgh Army.");
				addJob("I proud to be the Quartermaster of the Mithrilbourgh Army. We have plenty of #ammunition. However, we are lacking in #boots and #helmets.");
				addReply("boots", "I seem to hand out stone boots very regularly, but our careless soldiers  always lose them. Thus, I buy any good boots that you can #offer, see the blue book for a price list.");
				addReply("helmets", "I do not have a good source of helmets. Any you can #trade with me would be appreciated, at the moment we only have enough for the lieutenants, and none for the soldiers. The red book has details.");
				addReply("ammunition", "I sell arrows, wooden arrows are the cheapest, power arrows the most costly. Check the board for all the prices.");
				addHelp("As Quartermaster, I take #offers for supplies which we are short of.");
				addOffer("I buy #boots and #helmets on behalf of the Mithrilbourgh Army, and I sell surplus stock of #ammunition.");
				addQuest("The Mithrilbourgh Army is not in need your services at present.");
				new BuyerAdder().addBuyer(this, new BuyerBehaviour(shops.get("boots&helm")), false);
				new SellerAdder().addSeller(this, new SellerBehaviour(shops.get("sellarrows")), false);
 				addGoodbye("Bye.");
			}
		};
		npc.setDescription("You see Diehelm Brui, the Quartermaster.");
		npc.setEntityClass("recruiter3npc");
		npc.setPosition(10, 4);
		npc.initHP(100);
		zone.add(npc);

		// Add a book with the shop offers
		final Sign book = new Sign();
		book.setPosition(12, 3);
		book.setText(" -- Buying -- \n steel boots\t 1000\n golden boots\t 1500\n shadow boots\t 2000\n stone boots\t 2500\n chaos boots\t 4000\n green thing boots\t 6000\n xeno boots\t 8000");
		book.setEntityClass("book_blue");
		book.setResistance(10);
		zone.add(book);

		final Sign book2 = new Sign();
		book2.setPosition(13, 4);
		book2.setText(" -- Buying -- \n golden helmet\t 3000\n shadow helmet\t 4000\n horned golden helmet 5000\n chaos helmet\t 6000\n magic chain helmet\t 8000\n xeno helmet\t 8000\n black helmet\t 10000");
		book2.setEntityClass("book_red");
		book2.setResistance(10);
		zone.add(book2);
	}
}
