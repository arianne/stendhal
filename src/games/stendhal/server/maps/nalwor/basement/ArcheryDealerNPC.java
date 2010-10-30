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
package games.stendhal.server.maps.nalwor.basement;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;

/**
 * Inside Nalwor Inn basement .
 */
//TODO: take NPC definition elements which are currently in XML and include here
public class ArcheryDealerNPC extends SpeakerNPCFactory {
	private final ShopList shops = SingletonRepository.getShopList();

	@Override
	public void createDialog(final SpeakerNPC magearcher) {
		magearcher.addGreeting("Well met, kind stranger.");
		magearcher.addJob("I buy archery equipment for our village.");
		magearcher.addHelp("I can offer you no help. Sorry.");
		magearcher.addOffer("Check the blackboard for prices.");
		magearcher.addQuest("I have no quest for you.");
		magearcher.addGoodbye("Have a happy. Bye.");
		new BuyerAdder().add(magearcher, new BuyerBehaviour(shops.get("buyarcherstuff")), false);			    

	}
}
