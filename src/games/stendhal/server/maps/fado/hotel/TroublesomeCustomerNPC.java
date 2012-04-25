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

	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Groongo Rahnnt") {
		    
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("Gah! Outrageous Place! Been waiting forever for someone to show up!");
				addHelp("HELP?! You want ME to ...help... YOU?! Now... Ask me a #task you can do for me!");
				addQuest("A task for you? Yes! Eventually! Was about TIME someone asked!"); 
				addJob("Ah! Finally... Want a #task?");
				addOffer("If you do your #task well, you get a nice tip from me!");
				addGoodbye("Bye bye!");
			}

			protected void onGoodbye(RPEntity player) {
				setDirection(Direction.RIGHT);
			}
		
		};
		
		npc.setDescription("You see Groongo Rahnt. He seems impatient to get the attention of someone!");
		npc.setEntityClass("customeronchairnpc");
		npc.setPosition(67, 30);
		npc.initHP(100);
		zone.add(npc);
	}
}
