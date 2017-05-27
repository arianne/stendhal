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
package games.stendhal.server.maps.ados.city;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Builds a sad NPC (name: Andy) who lost his wife
 *
 * @author Erdnuggel (idea) and Vanessa Julius (implemented)
 *
 */

public class ManWithHatNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Andy") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(41, 6));
				nodes.add(new Node(41, 11));
                nodes.add(new Node(64, 11));
                nodes.add(new Node(64, 6));
                nodes.add(new Node(63, 6));
                nodes.add(new Node(63, 10));
                nodes.add(new Node(42, 10));
                nodes.add(new Node(42, 6));
                nodes.add(new Node(41, 6));
               	setPath(new FixedPath(nodes, true));

			}

			@Override
			protected void createDialog() {
				addGreeting("Hello.");
				addHelp("When I lived together with my beloved wife, we used to travel a lot. We loved the beach on Athor Island! But these memories make me sad now.");
				addJob("I stopped working after my wife died.");
				addOffer("I have nothing to offer you.");
				addGoodbye("Goodbye, thank you for talking with me.");

			}
		};

		npc.setDescription("You see a man with a hat. His name is Andy and he looks really sad.");
		npc.setEntityClass("manwithhatnpc");
		npc.setPosition(41, 6);
		npc.setDirection(Direction.RIGHT);
		npc.initHP(100);
		zone.add(npc);
	}
}
