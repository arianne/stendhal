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
package games.stendhal.server.maps.nalwor.assassinhq;

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;

/**
 * Inside Nalwor Assassin Headquarters - cellar .
 */
public class ChiefFalatheenDishwasherNPC implements ZoneConfigurator  {

	private final ShopList shops = SingletonRepository.getShopList();

	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Chief Falatheen Humble Dishwasher") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			public void createDialog() {
				addGreeting("You better have a good excuse for bothering me. I'm up to my neck in dishwater!");
				addJob("It is my job to wash all the dishes for all these pesky little brats.");
				addHelp("I can buy your vegetables and herbs.  Please see blackboards on wall for what I need.");
				addOffer("Look at blackboards on wall to see my prices.");
				addQuest("You could try to help me #escape from these hoodlums. Well... maybe not.");
				addGoodbye("Don't forget where I am now. Come back and see me some time. I do get lonely.");
				addReply("escape", "Yes! I want to pursue my dream. Mother Helena offered me a most wonderful job.  She needs a dishwasher. Lots of complaining customers!!!");
				new BuyerAdder().addBuyer(this, new BuyerBehaviour(shops.get("buyveggiesandherbs")), false);
			}};
			npc.setPosition(20, 3);
			npc.setDescription("You see a strong looking man. He ate lots of healthy vegetables to look like that!");
			npc.setEntityClass("../monsters/human/risecia_leader");
			zone.add(npc);
	}
}
