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
package games.stendhal.server.maps.ados.tunnel;

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Outside entrance to dragon lair in -1_ados_outside_w.
 */
public class WishmanNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildWishman(zone);
	}

	private void buildWishman(final StendhalRPZone zone) {
		final SpeakerNPC wishman = new SpeakerNPC("Wishman") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			public void createDialog() {
				addGreeting("Greetings, my fellow traveler. What may I do for you?");
				addOffer("We are content here. A place to call home and our wonderful dragons.");
				addJob("I am ever vigilant for those who wish to harm our dragons. They're all that are left of our glorious weyr.");
				addHelp("Perhaps you wish instead to visit with my brethren back a ways in the tunnel. Mind that you watch out for the assassins. They have taken over the tunnels.");
				addGoodbye("Farewell. May your days be many and your heart be free.");
				// all other behaviour is defined in the quest.
			}
		};

		wishman.setDescription("You see Wishman, once a mighty storm trooper in Blordrough's dark legion, now guardian of all thats left of their dragons.");
		wishman.setEntityClass("../monsters/human_blordrough/storm_trooper");
		wishman.setPosition(30, 28);
		wishman.initHP(100);
		zone.add(wishman);
	}
}
