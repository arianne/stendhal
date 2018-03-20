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
package games.stendhal.server.maps.fado.house;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Builds Josephine NPC (Cloak Collector).
 *
 * @author kymara
 */
public class WomanNPC implements ZoneConfigurator {
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

	//
	// IL0_womanNPC - Josephine, the Cloaks Collector
	//

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC woman = new SpeakerNPC("Josephine") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(3, 4));
				nodes.add(new Node(16, 4));
				nodes.add(new Node(16, 7));
				nodes.add(new Node(3, 7));
				nodes.add(new Node(3, 6));
				nodes.add(new Node(5, 6));
				nodes.add(new Node(5, 4));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				//addGreeting();
				addJob("If I could, I'd design dresses!");
				addHelp("You can get help from Xhiphin Zohos, he's usually just outside. *giggle* I wonder why!");
				addGoodbye("Bye bye now!");
			}
		};

		woman.setDescription("You see a fashionably dressed young woman. She looks like a bit of a flirt.");
		woman.setEntityClass("youngwomannpc");
		woman.setPosition(3, 4);
		woman.initHP(100);
		zone.add(woman);
	}
}
