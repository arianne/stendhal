/***************************************************************************
 *                   (C) Copyright 2003-2019 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.deniran.river;

import java.util.LinkedList;

/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2019 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SilentNPC;


public class DeniranArmy implements ZoneConfigurator  {


	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPCs(zone);
	}

	private void buildNPCs(StendhalRPZone zone) {
		final LinkedList<SilentNPC> npclist = new LinkedList<SilentNPC>();
		for(int i=0; i<20; i++) {
			for(int j=0; j<5; j++) {
				final SilentNPC npc = new SilentNPC();
				//npc.setIdea("defence");
				npc.setEntityClass("../monsters/human/deniran_stormtrooper");
				npc.setDescription("you see Deniran army soldier.");
				npc.setPosition(17+i, 83+j);
				npc.setDirection(Direction.DOWN);
				npc.setName("Deniran soldier");
				zone.add(npc);
				npclist.add(npc);
			}
			for(int j=0; j<5; j++) {
				final SilentNPC npc = new SilentNPC();
				//npc.setIdea("defence");
				npc.setEntityClass("../monsters/human/deniran_stormtrooper");
				npc.setDescription("you see Deniran army soldier.");
				npc.setPosition(83+i, 83+j);
				npc.setDirection(Direction.DOWN);
				npc.setName("Deniran soldier");
				zone.add(npc);
				npclist.add(npc);
			}

		}
	};
}
