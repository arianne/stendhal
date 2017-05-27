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
package games.stendhal.server.maps.semos.plains;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * A little boy who lives at a farm.
 *
 * @see games.stendhal.server.maps.quests.PlinksToy
 */
public class LittleBoyNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildSemosNorthPlainsArea(zone);
	}

	private void buildSemosNorthPlainsArea(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Plink") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(42, 105));
				nodes.add(new Node(42, 110));
				nodes.add(new Node(48, 110));
				nodes.add(new Node(47, 103));
				nodes.add(new Node(47, 100));
				nodes.add(new Node(53, 100));
				nodes.add(new Node(53, 93));
				nodes.add(new Node(49, 93));
				nodes.add(new Node(49, 98));
				nodes.add(new Node(46, 98));
				nodes.add(new Node(46, 100));
				nodes.add(new Node(38, 100));

				setPath(new FixedPath(nodes, true));
			}


			@Override
			public void createDialog() {
				// NOTE: These texts are only available after finishing the quest.
				addGreeting();
				addJob("I play all day.");
				addHelp("Be careful out east, there are wolves about!");
				addOffer("Hey, I will not give you my teddy! It's mine! *hug*");
				addGoodbye();
			}

		};
		npc.setEntityClass("plinknpc");
		npc.setDescription("You see a young boy called Plink. He is crying and needs some help...");
		npc.setPosition(38, 100);
		npc.initHP(100);
		zone.add(npc);
	}

}
