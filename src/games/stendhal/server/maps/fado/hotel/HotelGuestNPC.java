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
package games.stendhal.server.maps.fado.hotel;

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Builds a NPC in a house on Ados market (name:Damon) who is the daughter of fisherman Fritz
 *
 * @author Vanessa Julius
 *
 */
public class HotelGuestNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Damon") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("Hi, you!");
				addHelp("Oh I just came to town to visit my younger brother Stefan. My hometown is Ados but after I stepped into some trouble with my former girlfriend Caroline there, I decided to travel around a bit. I saw a #spooky #castle on my way to Fado...");
				addReply("spooky castle", "A really spooky one! With skeletons as guards around. Good that I'm not scared of anything! But I bet that something useful is #hidden #inside...");
				addReply("hidden inside", "Well unfortunetly I promised to my brother to visit him so soon as possible and I straight went to Fado then... too bad.");
				addQuest("A task for you? Me? Oh no.. You got to be #kidding me.");
				addReply("kidding", "Do I really look like someone who needs help? Well I don't! At least not at the moment.");
				addJob("Sometimes I'm here, sometimes I'm there. Fast like the #wind and smooth as a tiger.");
				addReply("wind", "No I'm not the storm #everyone talks about in Ados.");
				addReply("everyone", "My hysterical girlfriend Caroline and her father saw it somewhere far away on the sea. Well, I didn't even get a breeze of it.");
				addOffer("Well you don't offer me anything so I don't offer anything to you. That is life.");
				addGoodbye("Bye bye!");
			}

		@Override
		protected void onGoodbye(RPEntity player) {
			setDirection(Direction.RIGHT);
		}

		};

		npc.setDescription("You see Damon. His eyes even glow in the dark.");
		npc.setEntityClass("hotelguestnpc");
		npc.setPosition(77, 23);
		npc.initHP(100);
		zone.add(npc);
	}
}
