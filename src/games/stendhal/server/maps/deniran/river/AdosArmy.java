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
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SilentNPC;


public class AdosArmy implements ZoneConfigurator  {


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
				npc.setEntityClass("youngsoldiernpc");
				npc.setDescription("you see Ados army soldier.");
				npc.setPosition(55+i, 97+j);
				npc.setDirection(Direction.DOWN);
				npc.setName("Ados soldier");
				zone.add(npc);
				npclist.add(npc);
			}

		}
	};
}
