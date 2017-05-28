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
package games.stendhal.server.maps.kirdneh.city;

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Builds a little girl NPC (Elisabeth) in Kirdneh city.
 *
 * @author Vanessa Julius idea by miasma
 */
public class LittleGirlNPC implements ZoneConfigurator {
	//
	// ZoneConfigurator
	//

	/**
	 * Configure a zone.
	 *
	 * @param zone
	 *            The zone to be configured.
	 * @param attributes
	 *            Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Elisabeth") {

			@Override
			protected void createPath() {
				setPath(null);

			}

			@Override
			protected void createDialog() {
				// greeting message in quest given (ChocolateForElisabeth)
				addJob("Job? I like to play with my #toys :)");
				addReply("toys", "There is a young boy who lives in Semos and gave me one of his teddies once :) So sweet!");
				addHelp("Ask my #mommy, maybe she can help you...");
				addReply("mommy", "She sits on the bench over there and enjoys the sun.");
				addOffer("I can't offer you anything, I'm just a #child.");
				addReply("child", "I'm 5, yay!");
				addQuest("I want chocolate :( Mommy wanted to take care of that.");
				addGoodbye("Boiboi :)");
			}
		};

		npc.setEntityClass("littlegirl2npc");
		npc.setPosition(92, 15);
		npc.initHP(100);
		npc.setDescription("You see Elisabeth. She seems to be hungry.");
		zone.add(npc);
	}
}
