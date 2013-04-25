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

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.impl.MonologueBehaviour;

import java.util.Map;

/**
 * Provides a Troublesome Customer in Fado's Hotel.
 * 
 * Offers a quest to bring him the meal he's been awaiting to order.
 * 
 * @author omero 
 *
 */
public class TroublesomeCustomerNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		final String[] rants = {
			"... Bah! I wonder how long a hungry customer has to wait before a waiter shows up...",
			"... Gah! This place must be run by invisible waiters and lazy chefs...",
			"... Boh! From time to time I'd also like to have a decent meal...",
			"... Mah! I counted all the tiles on the floor already... Twice...",
			"... Doh! No wonder this place is almost deserted... One waits forever before enjoying a decent meal...",
			"... Meh! I'll start notching the table legs for every minute I spend waiting forever here..."
		};
		new MonologueBehaviour(buildNPC(zone), rants, 1);
	}

	private SpeakerNPC buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Groongo Rahnnt") {
		    
			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("Gah! It was about time that someone showed up eventually!");
				addHelp("HELP?! You want ME to ...help... YOU?! Ask me for a #task and I'll give you one at once!"); 
				addJob("Ah! Finally... Want a #task?");
				addOffer("Do a #task for me and you get a generous tip!");
				addGoodbye("Buzz off now!");
                
				/**
                 * Additional behavior code is in games.stendhal.server.maps.quests.MealForGroongo
                 */

			}

			@Override
			protected void onGoodbye(RPEntity player) {
				setDirection(Direction.RIGHT);
			}
		
		};

		npc.setEntityClass("customeronchairnpc");
		npc.setDescription("You see Groongo Rahnt. He seems impatient to get the attention of someone!");
		npc.setPosition(70, 24);
		npc.setDirection(Direction.RIGHT);
		npc.initHP(100);
		zone.add(npc);

		return npc;

	}
}
