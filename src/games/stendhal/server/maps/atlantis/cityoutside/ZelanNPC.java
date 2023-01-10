/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.atlantis.cityoutside;

import java.util.Arrays;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.CollisionAction;
import games.stendhal.server.entity.npc.SpeakerNPC;


public class ZelanNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		zone.add(buildNPC());
	}

	private SpeakerNPC buildNPC() {
		final SpeakerNPC zelan = new SpeakerNPC("Zelan");

		// appearance & behavior
		zelan.setEntityClass("atlantismale01npc");
		zelan.setCollisionAction(CollisionAction.STOP);

		// location & path
		zelan.setPathAndPosition(new FixedPath(Arrays.asList(
				new Node(63, 66),
				new Node(75, 66)), true));

		// dialog
		zelan.addGreeting();
		zelan.addGoodbye();

		return zelan;
	}
}
