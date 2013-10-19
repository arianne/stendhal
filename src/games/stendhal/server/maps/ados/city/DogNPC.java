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
package games.stendhal.server.maps.ados.city;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.UseListener;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.PassiveNPC;

import java.util.Arrays;
import java.util.Map;

/**
 * A playful puppy
 * 
 * @author AntumDeluge
 */
public class DogNPC implements ZoneConfigurator {
	
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final PassiveNPC dog = new Puppy();
		
		dog.put("menu", "Pet|Use");
		// Not visible, but used for the emote action
		dog.setName("Tommy");
		dog.setPosition(42, 35);
		dog.setDescription("You see Tommy.");
		dog.setEntityClass("animal/puppy");
		dog.setBaseSpeed(0.5);
		dog.moveRandomly();
		dog.setRandomMovementRadius(5, true);
        dog.setSounds(Arrays.asList("dog-small-bark-1", "dog-small-bark-2"));
		zone.add(dog);
	}

	/**
	 * A puppy that can be petted.
	 */
	private static class Puppy extends PassiveNPC implements UseListener {
		@Override
		public boolean onUsed(RPEntity user) {
			if (nextTo(user)) {
				say("!me wags tail.");
				return true;
			}
			return false;
		}
	}
}