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
public class SwimmerNPC extends SpeakerNPCFactory {

	@Override
	public void createDialog(final SpeakerNPC npc) {
		npc.addGreeting("Don't disturb me, I'm trying to establish a record!");
		npc.addQuest("I don't have a task for you, I'm too busy.");
		npc.addJob("I am a swimmer!");
		npc.addHelp("Try the diving board! It's fun!");
		npc.addGoodbye("Bye!");
	};
}
