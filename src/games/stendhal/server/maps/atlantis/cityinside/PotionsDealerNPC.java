/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

package games.stendhal.server.maps.atlantis.cityinside;

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.sign.ShopSign;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.SoundEvent;


public class PotionsDealerNPC implements ZoneConfigurator {

	private SpeakerNPC seller;


	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		initNPC(zone);
		initShop(zone);
	}

	private void initNPC(final StendhalRPZone zone) {
		seller = new SpeakerNPC("Mirielle");
		seller.setEntityClass("atlantisfemale05npc");

		seller.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						seller.say("Greetings! Welcome to the Atlantis Potions Shop.");
						seller.addEvent(new SoundEvent("npc/hello_female-01", SoundLayer.CREATURE_NOISE));
						seller.notifyWorldAboutChanges();
					}
				});

		seller.add(ConversationStates.ANY,
				ConversationPhrases.GOODBYE_MESSAGES,
				null,
				ConversationStates.IDLE,
				null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						seller.say("Goodbye. Come again.");
						seller.addEvent(new SoundEvent("npc/goodbye_female-01", SoundLayer.CREATURE_NOISE));
						seller.notifyWorldAboutChanges();
					}
				});

		seller.addOffer("Check the blackboard for a list of items that I sell.");

		seller.setIdleDirection(Direction.DOWN);
		seller.setPosition(8, 7);
		zone.add(seller);
	}

	private void initShop(final StendhalRPZone zone) {
		final String shopName = "atlantispotions";

		new SellerAdder().addSeller(seller, new SellerBehaviour(ShopList.get().get(shopName)), false);

		final ShopSign sign = new ShopSign(shopName, "Atlantis Potions Shop", seller.getName() + " sells the following items", true);
		sign.setEntityClass("blackboard");
		sign.setPosition(11, 12);

		zone.add(sign);
	}
}
