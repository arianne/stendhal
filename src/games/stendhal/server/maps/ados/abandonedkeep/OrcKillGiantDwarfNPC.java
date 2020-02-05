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
package games.stendhal.server.maps.ados.abandonedkeep;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Builds the orc kill diant dwarf NPC.
 *
 * @author Teiv
 */
public class OrcKillGiantDwarfNPC implements ZoneConfigurator {

	//
	// ZoneConfigurator
	//

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}


	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC zogfangNPC = new SpeakerNPC("Zogfang") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(5, 106));
				nodes.add(new Node(15, 106));
				nodes.add(new Node(15, 109));
				nodes.add(new Node(12, 109));
				nodes.add(new Node(12, 112));
				nodes.add(new Node(12, 114));
				nodes.add(new Node(5, 114));
				setPath(new FixedPath(nodes, true));

			}

			@Override
			protected void createDialog() {
				addGreeting("Hello my fine fellow. Welcome to Ados Abandoned Keep, our humble dwelling!");
				addJob("I wait for able bodied warriors to defeat the remaining dwarves in our area.");
				addReply("dwarf", "When we got here, we had to run the dwarves from this area. There are only a few left. Will you help us with this #task?");
				addHelp("When we got here, we had to run the dwarves from this area. There are only a few left. Will you help us with this #task?");
				addGoodbye("I wish you well on your journeys.");
			}
		};

		zogfangNPC.setEntityClass("orcbuyernpc");
		zogfangNPC.setPosition(10, 107);
		zogfangNPC.initHP(1000);
		zogfangNPC.setDescription(
				"You see Zogfang, an old and stinking mountain orc. " +
				"He awaits for warriors to help him clear his humble dwelling from some dwarves.");
		zone.add(zogfangNPC);
	}
}
