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
package games.stendhal.server.maps.orril.river;

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Configure Orril River South Campfire (Outside/Level 0).
 */
public class CampingGirlNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildCampfireArea(zone);
	}

	private void buildCampfireArea(final StendhalRPZone zone) {
		final SpeakerNPC sally = new SpeakerNPC("Sally") {

			@Override
			protected void createPath() {
				// NPC does not move
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("Hello wanderer... Please sit down and relax! I would ask a #favor but you probably on your own #task or #job already... ");
				addJob("I have no jobs for you dear wanderer. I'm just a little scout girl with a #favor or a #task to ask...");
				addOffer("I could offer you to perform a #favor or a #task for me... If you were interested, of course!");
				addHelp("Help?! Oh WOW ... You asking me?! Hear me then: " +
				        "You can find lots of useful stuff in a forest, like wood and mushrooms, for example. " +
						"But beware, some mushrooms are poisonous! Now, if you only cold do a little #favor, a #task...");
				addGoodbye();
				// Sally remaining behavior defined in maps.quests.Campfire.
			}
		};

		sally.setEntityClass("littlegirlnpc");
		sally.setPosition(40, 61);
		sally.setDirection(Direction.RIGHT);
		sally.initHP(100);
		sally.setDescription("You see Sally, the little scout girl. She is the daughter of Leander, the Semos baker and currently camping near the river.");
		zone.add(sally);
	}
}
