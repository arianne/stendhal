

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

package games.stendhal.server.maps.fado.city;

import java.util.Arrays;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.SpeakerNPC;


/**
 * Creates a Small Boy NPC
 *
 * @author jackrabbit
 */
public class SmallBoyNPC implements ZoneConfigurator {
	//
	// ZoneConfigurator
	//

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildSmallBoyNPC(zone);
	}

	//
	// A Small Boy NPC a bit below the tavern
	//
	private void buildSmallBoyNPC(final StendhalRPZone zone) {
		final SpeakerNPC boynpc = new SpeakerNPC("Bobby") {

			@Override
			protected void createPath() {
				// does not move
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("Hm?");
				addHelp("I wonder if a #balloon could fly high enough to touch the clouds...");
				addJob("A Job? Is that something you can eat?");
				addReply("balloon", "One day, i will have enough balloons to fly away!");
				addReply(Arrays.asList("xkcd", "tables", "sql", "student", "drop", "table"),
						"Yes, it's true, my full name is Robert'); DROP TABLE students;-- but you can call me Bobby.");
				addGoodbye("Good bye.");
			}

			@Override
			protected void onGoodbye(RPEntity player) {
				setDirection(Direction.RIGHT);
			}

		};

		boynpc.setOutfit(0, 48, 0, null, 4, null, 7, 1, 0);
		boynpc.setPosition(42, 30);
		boynpc.setDirection(Direction.RIGHT);
		boynpc.initHP(100);
		boynpc.setDescription("You see Bobby. He is looking at the sky and seems to be daydreaming.");
		zone.add(boynpc);
	}
}
