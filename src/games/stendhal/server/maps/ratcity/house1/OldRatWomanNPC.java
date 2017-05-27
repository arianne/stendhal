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
package games.stendhal.server.maps.ratcity.house1;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Builds a Rat Mother NPC.
 *
 * @author Norien
 */
public class OldRatWomanNPC implements ZoneConfigurator {

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
		final SpeakerNPC woman = new SpeakerNPC("Agnus") {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(3, 4));
				nodes.add(new Node(3, 13));
				nodes.add(new Node(12, 13));
				nodes.add(new Node(12, 4));

				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hello there.");
				addJob("Leave it to my children to not check in once in a while.");
				addHelp("I have no help to offer you.");
				addGoodbye("Bye");
				// remaining behaviour defined in games.stendhal.server.maps.quests.FindRatChildren
			}
		};
		woman.setDescription("You see an old ratwoman. She appears somehow worried.");


		woman.setEntityClass("oldratwomannpc");

		woman.setPosition(3, 4);
		// She has low HP
		woman.initHP(30);
		woman.setBaseHP(100);
		zone.add(woman);
	}
}
