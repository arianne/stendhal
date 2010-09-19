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

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;

/**
 * Inside Nalwor Assassin Headquarters - cellar .
 */
//TODO: take NPC definition elements which are currently in XML and include here
public class ChiefFalatheenDishwasherNPC extends SpeakerNPCFactory {
	private final ShopList shops = SingletonRepository.getShopList();
			@Override
			public void createDialog(final SpeakerNPC dishwasher) {
				dishwasher.addGreeting("You better have a good excuse for bothering me. I'm up to my neck in dishwater!");
				dishwasher.addJob("It is my job to wash all the dishes for all these pesky little brats.");
				dishwasher.addHelp("I can buy your vegetables and herbs.  Please see blackboards on wall for what I need.");
				dishwasher.addOffer("Look at blackboards on wall to see my prices.");
				dishwasher.addQuest("You could try to help me #escape from these hoodlums. Well... maybe not.");
				dishwasher.addGoodbye("Don't forget where I am now. Come back and see me some time. I do get lonely.");
				dishwasher.addReply("escape", "Yes! I want to pursue my dream. Mother Helena offered me a most wonderful job.  She needs a dishwasher. Lots of complaining customers!!!");
				new BuyerAdder().add(dishwasher, new BuyerBehaviour(shops.get("buyveggiesandherbs")), false);			    
			    
			   	}
}
