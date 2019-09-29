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
package games.stendhal.server.maps.fado.hotel;

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.impl.MonologueBehaviour;

/**
 * Provides Groongo Rahnnt, The Troublesome Customer in Fado's Hotel Restaurant.
 *
 * Groongo Rahnnt offers a quest to bring him a decent meal he's been awaiting to order.
 * Groongo Rahnnt offered quest will involve Stefan, the chef of Fado's Hotel Restaurant
 *
 * @author omero
 *
 */
public class TroublesomeCustomerNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		final String[] rants = {
			"... Bah! I wonder how long a hungry customer has to wait before a waiter shows up...",
			"... Boh! From time to time I'd like to have a decent meal...",
			"... Doh! No wonder this place is almost deserted... One waits forever before enjoying a decent meal...",
			"... Gah! This place must be run by invisible waiters and lazy chefs...",
			"... Mah! I counted all the tiles on the floor already... Twice...",
			"... Meh! I'll start notching the table legs for every minute I'm spending waiting at this table here...",
			"... Ugh! I should start counting ALL the bugs infesting this place..."
		};
		//minutes between rants, 5
		new MonologueBehaviour(buildNPC(zone), rants, 5);
	}

	private SpeakerNPC buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Groongo Rahnnt") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("Gah! About time someone showed up for a #task eventually...");
				addHelp("You should be asking me for a #task instead!");
				addJob("Ah! Finally someone wanting a #task...");
				addOffer("Do a #task for me and you get a generous tip!");
				addGoodbye("You... You... Buzz off now!");

				/**
                 * Additional behavior code is in games.stendhal.server.maps.quests.MealForGroongo
                 */

			}

			@Override
			protected void onGoodbye(RPEntity player) {
				setDirection(Direction.RIGHT);
			}

		};

		// Finalize Groongo Rahnnt, the Fado's Hotel Restaurant Troublesome Customer
		npc.setEntityClass("troublesomecustomernpc");
		npc.setDescription("You see Groongo Rahnnt. He seems impatient to get the attention of someone!");
		npc.setPosition(71, 33);
		npc.setDirection(Direction.RIGHT);
		npc.initHP(100);
		zone.add(npc);

		return npc;

	}
}
