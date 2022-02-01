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
import games.stendhal.server.entity.npc.CloneManager;
import games.stendhal.server.entity.npc.SpeakerNPC;

public class ReaperClones implements ZoneConfigurator {
	@Override
	public void configureZone(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		buildNPCS(zone);
	}

	private void buildNPCS(final StendhalRPZone zone) {
		final CloneManager cm = CloneManager.get();

		SpeakerNPC npc = ReaperNPC.createNPC("Grim Reaper clone");
		npc.setTitle("Grim Reaper");
		npc.setPosition(5, 7);
		zone.add(npc);
		// NOTE: when using name to register, must be called after added to zone
		cm.registerAsClone("Grim Reaper", npc.getName());
		npc = Reaper2NPC.createNPC("repaeR mirG clone");
		npc.setTitle("repaeR mirG");
		npc.setPosition(10, 7);
		zone.add(npc);
		cm.registerAsClone("repaeR mirG", npc.getName());
	}
}
