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
package games.stendhal.server.maps.ados.market;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.Map;

/**
 * Builds a npc in a house on Ados market (name:Caroline) who is the daughter of fisherman Fritz
 * 
 * @author Vanessa Julius 
 *
 */
public class FishermansDaughterNPC implements ZoneConfigurator {

	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Caroline") {
		    
			protected void createPath() {
				setPath(null);
		
			}

			@Override
			protected void createDialog() {
				addGreeting("Hello, nice to meet you!");
				addHelp("Did you visit the Ados market already? There are so many #nice #people around and some make #awesome #food!");
				addReply("awesome food", "I tried the fish soup and grilled steaks, they are so delicious!");
				addReply("nice people", "Florence Boullabaisse is one of my best friends and Haunchy is a really nice guy.");
				addQuest("I currently figure out how I can start my own business... I love it to cook and will maybe start a #catering #service soon..."); 
				addReply("catering service", "I've heard that some #hotels around the town need some dinner for their guests...Their kitchens are too small and they need someone who can easily cook and bring them some food they need.");
				addReply("hotels", "The huge one in Fado is known under newlyweds. It is a really nice location for celebrating your wedding at.");
				addJob("My father is #Fritz, the fisherman who walks along there. Currently I try to think about starting up my own business, a #catering #service.");
				addReply("storm", "The storm came out of nothing while my dad, Fritz was out for catching some fish. I was so happy when he returned without being hurt from it!");
				addReply("Fritz","He is a really lovely dad. He was a fisherman before a huge huge #storm nearly destroyed his boat.");
				addOffer("I can't offer you anything at the moment, but hopefully soon.");
				addGoodbye("Thank you for visiting us here and have a nice day.");
				
			}
		};

		npc.setDescription("You see Caroline. She seems to be a punshy nice lady who tries to reach her goals.");
		npc.setEntityClass("fishermansdaughternpc");
		npc.setPosition(70, 78);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		zone.add(npc);
	}
}
