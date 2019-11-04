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
package games.stendhal.server.maps.northpole;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Configure Kris Kringle - jolly old man that lives at the northpole.
 *
 * @author kymara   edited by tigertoes for Kris Kringle
 */
public class JollyOldManAtNorthpoleNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildkriskringle(zone);
	}

	private void buildkriskringle(final StendhalRPZone zone) {
		final SpeakerNPC kriskringle = new SpeakerNPC("Kris Kringle") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(14,4));
				nodes.add(new Node(14,14));
				nodes.add(new Node(16,14));
				nodes.add(new Node(16,10));
				nodes.add(new Node(22,10));
				nodes.add(new Node(22,3));
				nodes.add(new Node(26,3));
				nodes.add(new Node(26,10));
				nodes.add(new Node(16,10));
				nodes.add(new Node(16,15));
				nodes.add(new Node(2,15));
				nodes.add(new Node(2,7));
				nodes.add(new Node(4,7));
				nodes.add(new Node(4,4));
				setPath(new FixedPath(nodes, true));

			}

			@Override
			protected void createDialog() {
				addGreeting("Ho Ho Ho. You have come just in time. I need some help with these brats. They have stolen all the candy that I need for filling stockings. Please get it back. Will you help?");
				addJob("I am someone who tries to make everyone happy at Christmas time.");
				addHelp("I am the one that needs help.");
				addOffer("I offer you some nice rewards. Seek to gather all the candy and return.");
				addGoodbye("Farewell. You will be able to sample the candy, but remember I need most of it.");
			} //remaining behaviour defined in quest
		};

		kriskringle.setDescription("You see Kris Kringle - a wonderfully jolly man that is having difficulty with bratty elf kids.");
		kriskringle.setEntityClass("kriskringle");
		kriskringle.setPosition(14, 4);
		kriskringle.initHP(100);
		zone.add(kriskringle);
	}
}
