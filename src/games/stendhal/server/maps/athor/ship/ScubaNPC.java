/* $Id: CaptainNPC.java,v 1.23 2013/06/10 22:13:14 bluelads99 Exp $ */
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
package games.stendhal.server.maps.athor.ship;

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.QuestCompletedSellerBehaviour;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.athor.ship.AthorFerry.Status;

/** Factory for the Scuba Diver on Athor Ferry. */

public class ScubaNPC implements ZoneConfigurator  {

	private Status ferrystate;
	private final ShopList shops = SingletonRepository.getShopList();

	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Edward") {

			@Override
			public void createDialog() {
				addGoodbye("So long...");
				addHelp("Hm, maybe you'd like to go on an adventure?");
				addOffer("To licensed divers I can sell #scuba #gear.");
				new SellerAdder().addSeller(this, new QuestCompletedSellerBehaviour("get_diving_license", "I can't sell #scuba #gear to just anyone!", shops.get("sellScubaStuff")), false);
				addJob("I'm an assistant on this ship.");

				//scuba gear phrases
				addReply("scuba gear","You need scuba gear to explore the beautiful world below the sea.");
				addReply("scuba","You need scuba gear to explore the beautiful world below the sea.");
				addReply("gear","You need scuba gear to explore the beautiful world below the sea.");
				//clue for the player.
				addReply("study","Go to a library and check out the Diver's Handbook.");

				//quest phrases;
				addReply("license","Scuba diving can be dangerous before I can give you scuba gear you need to pass an #exam.");
				addReply("Mizuno","Do I know that name? Hmm... why yes! Come to think of it we sometimes see a man by that name wandering the #swamp during our breaks on the mainland.");
				addReply("swamp","Ai it lies just north of the dock but, beware that marsh has been haunted since the days of #Blordrough.");
				addReply("Blordrough","The demon lord Blordrough waged war in these lands some years ago until the day his army was routed by a coalition of the wood elves and Deniran forces. The three armies fought tooth and nail but, in the end, the demon lord flooded the lake and fled out to sea.");
				add(ConversationStates.ATTENDING,
						"status",
						null,
						ConversationStates.ATTENDING,
						null,
						new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						npc.say(ferrystate.toString());
					}
				});

			}

			@Override
			protected void onGoodbye(final RPEntity player) {
				// Turn back to the sea.
				setDirection(Direction.LEFT);
			}
		};

		new AthorFerry.FerryListener() {

			@Override
			public void onNewFerryState(final Status status) {
				ferrystate = status;
				switch (status) {
				case ANCHORED_AT_MAINLAND:
				case ANCHORED_AT_ISLAND:
					// capital letters symbolize shouting
					npc.say("LET GO ANCHOR!");
					break;

				default:
					npc.say("ANCHORS AWEIGH! SET SAIL!");
					break;
				}
				// Turn back to the wheel
				npc.setDirection(Direction.DOWN);

			}
		};

		npc.setPosition(17, 40);
		npc.setEntityClass("pirate_sailornpc");
		npc.setDescription ("You see a well seasoned sailor, but he seems preoccupied with something.");
		npc.setDirection(Direction.LEFT);
		zone.add(npc);
	}
}
