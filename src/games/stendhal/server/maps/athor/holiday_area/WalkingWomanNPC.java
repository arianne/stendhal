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

public class WalkingWomanNPC implements ZoneConfigurator  {

	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Kelicia") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				// walking along the beach
				nodes.add(new Node(5,49));
				nodes.add(new Node(6,48));
				nodes.add(new Node(6,47));
				nodes.add(new Node(8,47));
				nodes.add(new Node(8,46));
				nodes.add(new Node(11,46));
				nodes.add(new Node(11,45));
				nodes.add(new Node(13,45));
				nodes.add(new Node(13,44));
				nodes.add(new Node(15,44));
				nodes.add(new Node(15,43));
				nodes.add(new Node(17,43));
				nodes.add(new Node(17,42));
				nodes.add(new Node(19,42));
				nodes.add(new Node(19,41));
				nodes.add(new Node(20,41));
				nodes.add(new Node(20,40));
				nodes.add(new Node(22,40));
				nodes.add(new Node(22,39));
				nodes.add(new Node(24,39));
				nodes.add(new Node(24,38));
				nodes.add(new Node(26,38));
				nodes.add(new Node(26,37));
				nodes.add(new Node(27,37));
				nodes.add(new Node(27,36));
				nodes.add(new Node(29,36));
				nodes.add(new Node(29,35));
				nodes.add(new Node(31,35));
				nodes.add(new Node(31,34));
				nodes.add(new Node(34,34));
				nodes.add(new Node(34,33));
				nodes.add(new Node(35,33));
				nodes.add(new Node(35,32));
				nodes.add(new Node(41,32));
				nodes.add(new Node(41,31));
				nodes.add(new Node(45,31));
				nodes.add(new Node(45,30));
				nodes.add(new Node(47,30));
				nodes.add(new Node(47,29));
				nodes.add(new Node(52,29));
				nodes.add(new Node(52,28));
				nodes.add(new Node(76,28));
				// The same way back
				nodes.add(new Node(52,28));
				nodes.add(new Node(52,29));
				nodes.add(new Node(47,29));
				nodes.add(new Node(47,30));
				nodes.add(new Node(45,30));
				nodes.add(new Node(45,31));
				nodes.add(new Node(41,31));
				nodes.add(new Node(41,32));
				nodes.add(new Node(35,32));
				nodes.add(new Node(35,33));
				nodes.add(new Node(34,33));
				nodes.add(new Node(34,34));
				nodes.add(new Node(31,34));
				nodes.add(new Node(31,35));
				nodes.add(new Node(29,35));
				nodes.add(new Node(29,36));
				nodes.add(new Node(27,36));
				nodes.add(new Node(27,37));
				nodes.add(new Node(26,37));
				nodes.add(new Node(26,38));
				nodes.add(new Node(24,38));
				nodes.add(new Node(24,39));
				nodes.add(new Node(22,39));
				nodes.add(new Node(22,40));
				nodes.add(new Node(20,40));
				nodes.add(new Node(20,41));
				nodes.add(new Node(19,41));
				nodes.add(new Node(19,42));
				nodes.add(new Node(17,42));
				nodes.add(new Node(17,43));
				nodes.add(new Node(15,43));
				nodes.add(new Node(15,44));
				nodes.add(new Node(13,44));
				nodes.add(new Node(13,45));
				nodes.add(new Node(11,45));
				nodes.add(new Node(11,46));
				nodes.add(new Node(8,46));
				nodes.add(new Node(8,47));
				nodes.add(new Node(6,47));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			public void createDialog() {
				addGreeting("Hi!");
				addQuest("I have no jobs for you, my friend.");
				addJob("I'm just walking along the coast!");
				addHelp("I cannot help you...I'm just a girl...");
				addGoodbye("Bye!");
			}

		};
		npc.setPosition(5, 49);
		npc.setEntityClass("swimmer7npc");
		npc.setDescription ("The girl close to you is Kelicia, a girl who walks along the coast.");
		zone.add(npc);
	}
}
