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
package games.stendhal.server.maps.athor.holiday_area;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.athor.ship.AthorFerry;
import games.stendhal.server.maps.athor.ship.AthorFerry.Status;


/**
 * Factory for an NPC who brings players from the docks to Athor Ferry in a
 * rowing boat.
 */
//TODO: take NPC definition elements which are currently in XML and include here
public class FerryConveyerNPC extends SpeakerNPCFactory {

	protected Status ferrystate;
	private static StendhalRPZone shipZone;

	public static StendhalRPZone getShipZone() {
		if (shipZone == null) {
			shipZone = SingletonRepository.getRPWorld().getZone("0_athor_ship_w2");
		}
		return shipZone;
	}

	@Override
	public void createDialog(final SpeakerNPC npc) {
		npc.addGoodbye("Goodbye!");
		npc
				.addGreeting("Welcome to the Athor #ferry service! How can I #help you?");
		npc
				.addHelp("You can #board the #ferry for only "
						+ AthorFerry.PRICE
						+ " gold, but only when it's anchored near this harbor. Just ask me for the #status if you want to know where the ferry is.");
		npc
				.addJob("If passengers want to #board the #ferry to the mainland, I take them to the ship with this rowing boat.");
		npc
				.addReply(
						"ferry",
						"The ferry sails regularly between this island and the mainland, Faiumoni. You can #board it when it's here. Ask me for the #status to find out where it is currently.");
		npc.add(ConversationStates.ATTENDING, "status", null,
				ConversationStates.ATTENDING, null, new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						npc.say(ferrystate.toString());
					}
				});

		npc.add(ConversationStates.ATTENDING, "board", null,
				ConversationStates.ATTENDING, null, new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {

						if (ferrystate == Status.ANCHORED_AT_ISLAND) {
							npc.say("In order to board the ferry, you have to pay "
											+ AthorFerry.PRICE
											+ " gold. Do you want to pay?");
							npc.setCurrentState(ConversationStates.SERVICE_OFFERED);
						} else {
							npc.say(ferrystate.toString()
											+ " You can only board the ferry when it's anchored at the island.");
						}
					}
				});

		npc.add(ConversationStates.SERVICE_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.ATTENDING, null, new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						if (player.drop("money", AthorFerry.PRICE)) {
							player.teleport(getShipZone(), 27, 33, Direction.LEFT, null);

						} else {
							npc.say("Hey! You don't have enough money!");
						}
					}
				});

		npc.add(ConversationStates.SERVICE_OFFERED,
				ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.ATTENDING,
				"You don't know what you're missing, landlubber!", null);

		new AthorFerry.FerryListener() {

		
			public void onNewFerryState(final Status status) {
				ferrystate = status;
				switch (status) {
				case ANCHORED_AT_ISLAND:
					npc.say("Attention: The ferry has arrived at this coast! You can now #board the ship.");
					break;
				case DRIVING_TO_MAINLAND:
					npc.say("Attention: The ferry has taken off. You can no longer board it.");
					break;
				default:
					break;
				}
			}
		};
	}

}
