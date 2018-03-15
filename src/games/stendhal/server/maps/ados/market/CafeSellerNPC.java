/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.ados.market;

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;

/**
 * Mia works in the Botanical Gardens cafe.
 */
public class CafeSellerNPC implements ZoneConfigurator {
	private final ShopList shops = SingletonRepository.getShopList();

	/**
	 * region that this NPC can give information about
	 */
	@Override
	public void configureZone(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Mia") {

			@Override
			public void createDialog() {
				addGreeting("Welcome to our cafe at Ados Botanical Gardens!");
				addHelp("Don't forget to look at all the signs which explain where the plants come from!");
				addQuest("You're so nice! You could try asking Calla, she always seems to know someone who needs help.");
				addJob("I sell drinks and snacks here at the cafe. I'd love to be able to say I made the food, too, but unfortunately we have to import everything.");
				addOffer("You can buy drinks and snacks, have a look at our menu here. Everything is imported in, so it's expensive but the best around!");
				new SellerAdder().addSeller(this, new SellerBehaviour(shops.get("cafe")), false);

				// just to be nice :)
				addEmotionReply("thanks", "warmly thanks");
				addEmotionReply("smile", "smiles at");

				addGoodbye("Come back soon!");
			}

	        @Override
	        protected void onGoodbye(RPEntity player) {
	        	setDirection(Direction.DOWN);
	        }

			@Override
			protected void createPath() {
				setPath(null);
			}
		};
		npc.setPosition(69, 114);
		npc.setDescription("You see sweet Mia, ready to serve customers with a pretty smile.");
		npc.setEntityClass("cafesellernpc");
        npc.setDirection(Direction.DOWN);
		zone.add(npc);
	}

}
