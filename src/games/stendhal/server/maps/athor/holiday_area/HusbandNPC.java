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
public class HusbandNPC extends SpeakerNPCFactory {

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
		npc.addGreeting("Hi!");
		npc.addQuest("We have no tasks, we're here on holiday.");
		npc.addJob("I am a coachman, but on this island there are no carriages!");
		npc.addHelp("Don't try to talk to my wife, she is very shy.");
		npc.addGoodbye("Bye!");
	}
}
