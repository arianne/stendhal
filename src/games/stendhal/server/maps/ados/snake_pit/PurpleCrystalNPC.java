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
package games.stendhal.server.maps.ados.snake_pit;

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
public class PurpleCrystalNPC implements ZoneConfigurator {
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
		final SpeakerNPC crystal = new SpeakerNPC("Purple Crystal") {

			@Override
			protected void createPath() {
				// NPC doesn't move
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("This fierce atmosphere in this dungeon makes me tremble...");
				addHelp("These snakes down here are scary! Take care about their king!");
				addJob("I am a crystal. What more can I say?");
				addGoodbye("Farewell, return to me whenever you need my help.");

			}
		};

		crystal.setEntityClass("transparentnpc");
		crystal.setAlternativeImage("crystalpurplenpc");
		crystal.setPosition(47, 64);
		crystal.initHP(100);
		crystal.setDescription("You see a purple colored crystal. There is something eerie about it.");
		crystal.setResistance(0);

		zone.add(crystal);
	}

}
