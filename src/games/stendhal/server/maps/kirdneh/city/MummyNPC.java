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

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Builds a information giving NPC in Kirdneh city.
 *
 * @author Vanessa Julius idea by miasma
 */
public class MummyNPC implements ZoneConfigurator {
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
		final SpeakerNPC npc = new SpeakerNPC("Carey") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				//Greeting message in quest given (ChocolateForElisabeth)
				addJob("I'm a fulltime mom and I love it.");
				addHelp("I've heard that some of the #houses around the town are still for sale.");
				addReply("houses", "They are huge! A friend of mine owns one and invited me a few times.");
				addOffer("Oh I have no #offers for you, sorry.");
				addReply("offers", "Did you visit the Kirdneh market already? It smells nice there.");
				addQuest("I don't have a quest for you but my daughter #Elisabeth searches for chocolate.");
				addReply("Elisabeth", "She is such a lovely child, I will always care of her!");
				addGoodbye("Thank you for meeting us here.");
			}

			@Override
			protected void onGoodbye(RPEntity player) {
				setDirection(Direction.DOWN);
			}
		};

		npc.setEntityClass("mothernpc");
		npc.setPosition(84, 9);
		npc.initHP(100);
		npc.setDescription("You see Carey. She takes care of her daughter Elisabeth.");
		zone.add(npc);
	}
}
