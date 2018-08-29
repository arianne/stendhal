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
package games.stendhal.server.maps.amazon.hut;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.CollisionAction;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Builds the princess in Princess Hut on amazon island.
 *
 * @author Teiv
 */
public class PrincessNPC implements ZoneConfigurator {
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
		final SpeakerNPC princessNPC = new SpeakerNPC("Princess Esclara") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(6, 13));
				nodes.add(new Node(14, 13));
				nodes.add(new Node(14, 4));
				nodes.add(new Node(6, 4));
				nodes.add(new Node(6, 3));
				nodes.add(new Node(4, 3));
				nodes.add(new Node(4, 7));
				nodes.add(new Node(6, 7));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
			        addGreeting("Huh, what are you doing here?");
				addReply("sorry", "Well, so you should be, sneaking up on me like that!");
				addReply("look", "You had better not poke around, this is all mine!");
				addReply("nothing", "Go away and do this somewhere else but not in my hut!");
				addJob("Job? You expect that a princess like me would need to work? Ha!");
				addHelp("Beware of my sisters on the island, they do not like strangers.");
				addOffer("There is nothing to offer you.");
				addGoodbye("Goodbye, and beware of the barbarians.");
			}
		};

		princessNPC.setEntityClass("amazoness_princessnpc");
		princessNPC.setPosition(6, 13);
		princessNPC.setCollisionAction(CollisionAction.STOP);
		princessNPC.initHP(100);
		princessNPC.setDescription("You see Princess Esclara. She smells of coconut and pineapples...");
		zone.add(princessNPC);
	}
}
