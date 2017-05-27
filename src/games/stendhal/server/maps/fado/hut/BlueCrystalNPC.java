/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.fado.hut;

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * A crystal NPC
 *
 * @author AntumDeluge
 *
 */
public class BlueCrystalNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 *
	 * @author AntumDeluge
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {

		// Create the NPC
		final SpeakerNPC crystal = new SpeakerNPC("Blue Crystal") {

			@Override
			protected void createPath() {
				// NPC doesn't move
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("Nice to meet you! This hut here is lovely.");
				addHelp("Lupos is always searching for handmade elvish equipment.");
				addJob("I am a crystal. What more can I say?");
				addGoodbye("Farewell, return to me whenever you need my help.");
			}
		};

		crystal.setEntityClass("transparentnpc");
		crystal.setAlternativeImage("crystalbluenpc");
		crystal.setPosition(9, 8);
		crystal.initHP(100);
		crystal.setDescription("You see a blue coloured crystal. Somehow, your shoulders feel lighter.");
		crystal.setResistance(0);

		zone.add(crystal);
	}

}
