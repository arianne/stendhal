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
package games.stendhal.server.maps.nalwor.hell;

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

public class ReaperClones implements ZoneConfigurator {
	@Override
	public void configureZone(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		buildNPCS(zone);
	}

	private void buildNPCS(final StendhalRPZone zone) {
		SpeakerNPC npc = ReaperNPC.createNPC("Grim\u00A0Reaper");
		npc.setPosition(5, 7);
		zone.add(npc);
		npc = Reaper2NPC.createNPC("repaeR\u00A0mirG");
		npc.setPosition(10, 7);
		zone.add(npc);
	}
}
