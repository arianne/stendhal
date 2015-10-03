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

import games.stendhal.common.Direction;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.athor.ship.AthorFerry.Status;

import java.util.Map;

/** Factory for the Scuba Diver on Athor Ferry. */

public class ScubaNPC implements ZoneConfigurator  {

	private Status ferrystate;

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
				addJob("I'm an assistant on this ship.");
				
				//scuba gear phrases
				addReply("scuba gear","You need scuba gear to explore the beautiful world below the sea.");
				addReply("scuba","You need scuba gear to explore the beautiful world below the sea.");
				addReply("gear","You need scuba gear to explore the beautiful world below the sea.");
				
				//quest phrases;
				addReply("license","Do you want to get the diving license?");
				add(ConversationStates.ATTENDING,
						"status",
						null,
						ConversationStates.ATTENDING,
						null,
						new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						npc.say(ferrystate.toString());
						//.getCurrentDescription());
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
