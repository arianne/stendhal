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
package games.stendhal.server.maps.semos.city;

import java.util.Arrays;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.UseListener;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.SilentNPC;

/**
 * A playful puppy
 *
 * @author AntumDeluge
 */
public class PuppyNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SilentNPC dog = new Puppy();

		dog.put("menu", "Pet|Use");
		// Not visible, but used for the emote action
		dog.setName("Puppy");
		dog.setPosition(23, 54);
		dog.setDescription("You see a playful puppy.");
		dog.setEntityClass("animal/puppy");
		dog.setBaseSpeed(0.5);
		dog.moveRandomly();
		dog.setRandomMovementRadius(25, true);
		dog.setSounds(Arrays.asList("dog-small-bark-1", "dog-small-bark-2"));
		zone.add(dog);
	}

	/**
	 * A puppy that can be petted.
	 */
	private static class Puppy extends SilentNPC implements UseListener {
		/*@Override
		protected void createPath() {
			final List<Node> nodes = new LinkedList<Node>();
			nodes.add(new Node(23, 54));
			nodes.add(new Node(17, 54));
			nodes.add(new Node(17, 49));
			nodes.add(new Node(19, 49));
			nodes.add(new Node(19, 58));
			nodes.add(new Node(23, 58));
			setPath(new FixedPath(nodes, true));
		}*/

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
