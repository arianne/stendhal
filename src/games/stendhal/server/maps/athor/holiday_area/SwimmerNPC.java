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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

public class SwimmerNPC implements ZoneConfigurator  {

	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Enrique") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(67,68));
				nodes.add(new Node(67,63));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			public void createDialog() {
				addGreeting("Don't disturb me, I'm trying to establish a record!");
				addQuest("I don't have a task for you, I'm too busy.");
				addJob("I am a swimmer!");
				addHelp("Try the diving board! It's fun!");
				addGoodbye("Bye!");
			}

		};
		npc.setPosition(67, 63);
		npc.setEntityClass("swimmer3npc");
		npc.setDescription ("You see Enrique swimming around in a pool while he tries to establish a record.");
		zone.add(npc);
	}
}
