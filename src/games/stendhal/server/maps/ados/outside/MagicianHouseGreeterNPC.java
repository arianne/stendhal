/***************************************************************************
 *                   (C) Copyright 2011-2012 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.ados.outside;

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Builds a NPC outside Magician house in Ados  (name:Venethiel) who is the pupil of Magician Haizen
 *
 * @author geomac
 */
public class MagicianHouseGreeterNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		createMagicianHouseGreeterNPC(zone);
	}

	private void createMagicianHouseGreeterNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Venethiel") {

			@Override
			protected void createDialog() {
				addGreeting("Hello, I am so excited about the magical #maze!");
				addHelp("If you get tired, you'll have to log off to get back to #Haizen.");
				addReply("maze", "I'm afraid you will lose your way, but there are #scrolls for you to find.");
				addReply("scrolls", "You only have ten minutes to pick up the scrolls in the #maze.");
				addQuest("I am asking players to complete the #maze. Haizen will then make me his #assistant.");
				addReply("assistant", "One day, I may learn how to use magic.");
				addReply("Haizen", "He is teaching me about magic.");
				addOffer("I can offer you some #advice.");
				addReply("advice", "It would be helpful to look upon the mini map often.");
				addJob("I am hoping to be Haizens #assistant soon.");
				addGoodbye("Thank you and have a nice day.");
			}

			@Override
			protected void onGoodbye(RPEntity player) {
				setDirection(Direction.DOWN);
			}
		};

		npc.setDescription("You see Venethiel. She wants to learn about magic.");
		npc.setEntityClass("magicianhousegreeternpc");
		npc.setPosition(70, 52);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		zone.add(npc);
	}
}
