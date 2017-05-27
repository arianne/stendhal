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

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Builds a NPC in a house on Ados market (name:Caroline) who is the daughter of fisherman Fritz
 *
 * @author Vanessa Julius
 *
 */
public class FishermansDaughterNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		createFishermansDaughterSellingNPC(zone);
	}

	public void createFishermansDaughterSellingNPC(final StendhalRPZone zone) {


		final SpeakerNPC npc = new SpeakerNPC("Caroline") {
			@Override
			protected void createPath() {
				setPath(null);

			}

			@Override
			protected void createDialog() {
				addGreeting("Hello, nice to meet you!");
				addHelp("Did you visit the Ados market already? There are so many #nice #people around and some make #awesome #food!");
				addReply("awesome food", "I tried the fish soup and grilled steaks, they are so delicious!");
				addReply("nice people", "Florence Boullabaisse is one of my best friends and Haunchy is a really nice guy.");
				addQuest("I am currently trying to figure out how I can start my own business... I love to cook and might start a #catering #service soon... Meanwhile I enjoy my new renovated house :)");
				addReply("catering service", "I've heard that some #hotels need some dinner for their guests... their kitchens are too small and they need someone who can easily cook and bring them some food they need.");
				addReply("hotels", "The huge one in Fado is known to newlyweds. It is a really nice location to celebrate your wedding at.");
				addJob("My father is #Fritz, the fisherman who walks outside infront of our house. Right now, I'm thinking about starting up my own business, a #catering #service.");
				addReply("storm", "The storm came out of nowhere while my dad, Fritz, was out catching fish. I was so happy when he returned safe!");
				addReply("Fritz","He is a really lovely dad. He was a fisherman before a huge huge #storm nearly destroyed his boat.");
				addOffer("I can't offer you anything at the moment, but hopefully soon.");
				addGoodbye("Thank you for visiting us here and have a nice day.");
			}

			@Override
			protected void onGoodbye(RPEntity player) {
				setDirection(Direction.DOWN);
			}
		};

		npc.setDescription("You see Caroline. She seems to be a punchy nice lady who tries to reach her goals.");
		npc.setEntityClass("fishermansdaughternpc");
		npc.setPosition(8, 3);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		zone.add(npc);
	}
}
