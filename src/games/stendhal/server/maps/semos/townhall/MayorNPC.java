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
package games.stendhal.server.maps.semos.townhall;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.CollisionAction;
import games.stendhal.server.entity.npc.SpeakerNPC;

public class MayorNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildSemosTownhallAreaMayor(zone);
	}

	/**
	 * Adds a Mayor to the townhall who gives out daily quests.
	 * @param zone zone to be configured with this
	 */
	private void buildSemosTownhallAreaMayor(final StendhalRPZone zone) {
		// We create an NPC
		final SpeakerNPC npc = new SpeakerNPC("Mayor Sakhs") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(13, 3));
				nodes.add(new Node(19, 3));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Welcome citizen! Do you need #help?");
				addJob("I'm the mayor of Semos village.");
				addHelp("You will find a lot of people in Semos that offer you help on different topics.");
				addGoodbye("Have a good day and enjoy your stay!");
			}
		};

		npc.setEntityClass("mayornpc");
		npc.setDescription("The mighty mayor of Semos, Mayor Sakhs, is walking infront of you. He seems to be nervous...");
		npc.setPosition(13, 3);
		npc.setCollisionAction(CollisionAction.STOP);
		npc.initHP(100);
		zone.add(npc);
	}
}
