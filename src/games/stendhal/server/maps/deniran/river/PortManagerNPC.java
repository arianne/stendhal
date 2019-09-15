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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * A port manager
 */
public class PortManagerNPC implements ZoneConfigurator {

	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Fiete") {
			@Override
			public void createDialog() {
				addGreeting("Moin! Ados ships #delayed. No packets available.");
				addJob("Me port manager. Busy job. Very important! But ships from Ados delayed, so taking #break");
				addReply(Arrays.asList("delayed", "break"),
						"Me works hard. Very hard. So break fine, when no ships.");
				addHelp("The capital city of Deniran is to the north.");
				addQuest("Lots of work, when ships come. But now, no ships, no work. Return later.");
				addGoodbye("Return later. Lot's of work, when ships come.");
			}

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(82, 52));
				nodes.add(new Node(100, 52));
				setPath(new FixedPath(nodes, true));
			}

		};
		npc.setPosition(82, 52);
		npc.setEntityClass("beardmannpc");
		npc.setDescription("You see a strong man, lazily pacing.");
		zone.add(npc);
	}
}
