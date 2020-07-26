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

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.athor.ship.AthorFerry;
import games.stendhal.server.maps.athor.ship.AthorFerry.Status;


/**
 * Factory for an NPC who brings players from the docks to Athor Ferry in a
 * rowing boat.
 */

public class FerryConveyerNPC implements ZoneConfigurator  {

	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}

	protected Status ferrystate;
	private static StendhalRPZone shipZone;

	public static StendhalRPZone getShipZone() {
		if (shipZone == null) {
			shipZone = SingletonRepository.getRPWorld().getZone("0_athor_ship_w2");
		}
		return shipZone;
	}

	private void buildNPC(StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Jessica") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			public void createDialog() {

				addGoodbye("Goodbye!");
				addGreeting("Welcome to the Athor #ferry service! How can I #help you?");
				addHelp("You can #board the #ferry for only "
						+ AthorFerry.PRICE
						+ " gold, but only when it's anchored near this harbor. Just ask me for the #status if you want to know where the ferry is.");
				addJob("If passengers want to #board the #ferry to the mainland, I take them to the ship with this rowing boat.");
				addReply(
						"ferry",
				"The ferry sails regularly between this island and the mainland, Faiumoni. You can #board it when it's here. Ask me for the #status to find out where it is currently.");
				add(ConversationStates.ATTENDING, "status", null,
						ConversationStates.ATTENDING, null, new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						npc.say(ferrystate.toString());
					}
				});

				add(ConversationStates.ATTENDING, "board", null,
						ConversationStates.ATTENDING, null, new ChatAction() {
					@Override
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

				add(ConversationStates.SERVICE_OFFERED,
						ConversationPhrases.YES_MESSAGES, null,
						ConversationStates.ATTENDING, null, new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						if (player.drop("money", AthorFerry.PRICE)) {
							player.teleport(getShipZone(), 27, 33, Direction.LEFT, null);

						} else {
							npc.say("Hey! You don't have enough money!");
						}
					}
				});

				add(ConversationStates.SERVICE_OFFERED,
						ConversationPhrases.NO_MESSAGES, null,
						ConversationStates.ATTENDING,
						"You don't know what you're missing, landlubber!", null);

			}

			@Override
			protected void onGoodbye(final RPEntity player) {
				setDirection(Direction.LEFT);
			}};

			new AthorFerry.FerryListener() {


				@Override
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

			npc.setPosition(16, 88);
			npc.setEntityClass("woman_008_npc");
			npc.setDescription ("You see Jessica. She takes passengers to the ship with her rowing boat.");
			npc.setDirection(Direction.LEFT);
			zone.add(npc);
	}

}
