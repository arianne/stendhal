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
package games.stendhal.server.maps.athor.holiday_area;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
//TODO: take NPC definition elements which are currently in XML and include here
public class SunbatherNPC extends SpeakerNPCFactory {

	@Override
	protected SpeakerNPC instantiate(final String name) {
		final SpeakerNPC npc = new SpeakerNPC(name) {
			@Override
			public void say(final String text) {
				// He doesn't move around because he's "lying" on his towel.
				say(text, false);
			}
		};
		return npc;
	}
	
	@Override
	public void createDialog(final SpeakerNPC npc) {
		npc.addGreeting("Hey there!");
		npc.addQuest("I don't have a task for you, I'm perfectly happy!");
		npc.addJob("Don't remind me of my job, I'm on holiday!");
		npc.addHelp("In the desert there is dangerous quicksand.");
		npc.addGoodbye("Bye! I'll stock up on some more sunshine.");
	}
}
