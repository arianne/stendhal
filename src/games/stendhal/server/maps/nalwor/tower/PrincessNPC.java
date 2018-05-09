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
package games.stendhal.server.maps.nalwor.tower;

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
 * Builds a Princess NPC who lives in a tower.
 *
 * @author kymara
 */
public class PrincessNPC implements ZoneConfigurator {
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
		final SpeakerNPC npc = new SpeakerNPC("Tywysoga") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(17, 13));
				nodes.add(new Node(10, 13));
				nodes.add(new Node(10, 4));
				nodes.add(new Node(3, 4));
				nodes.add(new Node(3, 3));
				nodes.add(new Node(7, 3));
				nodes.add(new Node(7, 9));
				nodes.add(new Node(12, 9));
				nodes.add(new Node(12, 13));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hail to thee, human.");
				addJob("I'm a princess. What can I do?");
				addHelp("A persistent person could do a #task for me.");
				addOffer("I don't trade. My parents would have considered it beneath me.");
 				addGoodbye("Goodbye, strange one.");
			}
		};

		npc.setDescription("You see a beautiful but forlorn High Elf.");
		npc.setEntityClass("elfprincessnpc");
		npc.setPosition(17, 13);
		npc.setCollisionAction(CollisionAction.STOP);
		npc.initHP(100);
		zone.add(npc);
	}
}
