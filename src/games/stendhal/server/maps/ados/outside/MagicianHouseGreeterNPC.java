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
package games.stendhal.server.maps.ados.outside;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.Map;

/**
 * Builds a NPC outside Magician house in Ados  (name:Venethiel) who is the pupil of Magician Haizen
 * 
 * @author geomac 
 *
 */
public class MagicianHouseGreeterNPC implements ZoneConfigurator {

	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		createMagicianHouseGreeterNPC(zone);
	}

	public void createMagicianHouseGreeterNPC(final StendhalRPZone zone) {


		final SpeakerNPC npc = new SpeakerNPC("Venethiel") {
			@Override
			protected void createPath() {
				setPath(null);

			}
			
			@Override
			protected void createDialog() {
				addGreeting("Hello, Can you #help me!");
				addHelp("Will you ask Haizen for a #maze to complete? There are #scrolls for you, if you are fast enough!");
				addReply("maze", "I'm afraid you will lose your way!");
				addReply("scrolls", "You only have ten minutes to pick up the scrolls.");
				addQuest("I am asking players to complete the #maze. Haizen will then make me his #assistant"); 
				addReply("assistant", "If you get tired, you'll have to log off to back to #Haizen.");
				addReply("Haizen", "He is teaching me about magic.");
				addOffer("I can offer you some #advice.");
				addReply("advice", "It would be helpful to look upon the mini map often.");
				addGoodbye("Thank you and have a nice day.");
			}
			
			protected void onGoodbye(RPEntity player) {
				setDirection(Direction.DOWN);
			}
		};

		npc.setDescription("You see Venethiel. She wants to learn about magic.");
		npc.setEntityClass("kid5npc");
		npc.setPosition(70, 52);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		zone.add(npc);
	}
}