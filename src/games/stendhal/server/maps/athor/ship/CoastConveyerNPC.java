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
package games.stendhal.server.maps.athor.ship;

import java.util.Arrays;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.athor.ship.AthorFerry.Status;

/** Factory for cargo worker on Athor Ferry. */

public class CoastConveyerNPC implements ZoneConfigurator  {

	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}

	private static StendhalRPZone islandDocksZone;
	private static StendhalRPZone mainlandDocksZone;

	private StendhalRPZone getIslandDockZone() {
		if (islandDocksZone == null) {

			islandDocksZone = SingletonRepository.getRPWorld().getZone("0_athor_island");
		}

		return islandDocksZone;
	}


	private Status ferryState;

	private void buildNPC(StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Jackie") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			public void createDialog() {

				addGoodbye("Goodbye!");
				addGreeting("Ahoy, Matey! How can I #help you?");
				addHelp("Ye can #disembark, but only when we're anchored a harbor. Just ask me for the #status if ye have no idea where we are.");
				addJob("I'm taking passengers who want to #disembark to the coast with me rowing boat.");

				add(ConversationStates.ATTENDING, "status",
						null,
						ConversationStates.ATTENDING,
						null,
						new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						npc.say(ferryState.toString());
					}
				});

				add(ConversationStates.ATTENDING,
						Arrays.asList("disembark", "leave"),
						null,
						ConversationStates.ATTENDING,
						null,
						new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						switch (ferryState) {
						case ANCHORED_AT_MAINLAND:
							npc.say("Do ye really want me to take ye to the mainland with me skiff?");
							npc.setCurrentState(ConversationStates.SERVICE_OFFERED);
							break;
						case ANCHORED_AT_ISLAND:
							npc.say("Do ye really want me to take ye to the island with me skiff?");
							npc.setCurrentState(ConversationStates.SERVICE_OFFERED);
							break;

						default:
							npc.say(ferryState.toString()
									+ " Ye can only get off the boat when it's anchored near a harbor.");

						}
					}
				});


				add(ConversationStates.SERVICE_OFFERED,
						ConversationPhrases.YES_MESSAGES,
						null,
						ConversationStates.ATTENDING, null,
						new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						switch (ferryState) {
						case ANCHORED_AT_MAINLAND:
							player.teleport(getMainlandDocksZone(), 100, 100, Direction.LEFT, null);
							npc.setCurrentState(ConversationStates.IDLE);
							break;
						case ANCHORED_AT_ISLAND:
							player.teleport(getIslandDockZone(), 16, 89, Direction.LEFT, null);
							npc.setCurrentState(ConversationStates.IDLE);
							break;

						default:
							npc.say("Too bad! The ship has already set sail.");

						}

					}
				});

				add(ConversationStates.SERVICE_OFFERED,
						ConversationPhrases.NO_MESSAGES,
						null,
						ConversationStates.ATTENDING,
						"Aye, matey!", null);

			}};
			new AthorFerry.FerryListener() {


				@Override
				public void onNewFerryState(final Status status) {
					ferryState = status;
					switch (status) {
					case ANCHORED_AT_MAINLAND:
						npc.say("Attention: The ferry has arrived at the mainland! You can now #disembark.");
						break;
					case ANCHORED_AT_ISLAND:
						npc.say("Attention: The ferry has arrived at the island! You can now #disembark.");
						break;
					default:
						npc.say("Attention: The ferry has set sail.");
						break;
					}

				}
			};

			npc.setPosition(29, 34);
			npc.setEntityClass("pirate_sailor2npc");
			npc.setDescription ("Jackie helps passangers to disembark to the coast. She is a real pirate girl!");
			npc.setDirection(Direction.LEFT);
			zone.add(npc);
	}

	private static StendhalRPZone getMainlandDocksZone() {
		if (mainlandDocksZone == null) {
			mainlandDocksZone = SingletonRepository.getRPWorld().getZone("0_ados_coast_s_w2");
		}
		return mainlandDocksZone;
	}
}
