

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

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.Outfit;

import java.util.Map;


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
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildSmallBoyNPC(zone, attributes);
	}

	//
	// A Small Boy NPC a bit below the tavern
	//
	private void buildSmallBoyNPC(final StendhalRPZone zone, final Map<String, String> attributes) {
		final SpeakerNPC boynpc = new SpeakerNPC("Bobby") {

			@Override
			protected void createPath() {
				// does not move
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("Hm?");
				addHelp("I wonder if balloons can fly high enough to touch the clouds");
				addJob("A Job? Is that something you can eat?");
				addGoodbye("Good bye.");
			}

			@Override
			protected void onGoodbye(Player player) {
				setDirection(Direction.RIGHT);
			}
			
		};

		boynpc.setOutfit(new Outfit(0,33,18,03,0));
		boynpc.setPosition(42, 30);
		boynpc.initHP(100);
		boynpc.setDescription("You see Bobby. He is looking at the sky and seems to be daydreaming.");
		zone.add(boynpc);
	}
}
