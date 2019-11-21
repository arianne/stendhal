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

package games.stendhal.server.maps.ados.forest;

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
 * Provides Eheneumniranin
 *
 * @author omero
 */
public class SickleingHalfelfNPC implements ZoneConfigurator {

	@Override
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Eheneumniranin") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(77, 96));
				nodes.add(new Node(77, 98));
				nodes.add(new Node(81, 98));
				nodes.add(new Node(81, 100));
				nodes.add(new Node(85, 100));
				nodes.add(new Node(85, 107));
				nodes.add(new Node(75, 107));
				nodes.add(new Node(75, 96));

				setPath(new FixedPath(nodes, true));
			}

			@Override
			public void createDialog() {
				addGreeting("Salve straniero...");
				addJob( "To gather the sheaves of grain... With a #sickle... Maybe I should use a #scythe!");
				addReply(
						"sickle",
						"A usefull farming tool indeed."+
						" To some blacksmith one should ask to if he offers any such sharp utensil.");
				addReply(
						"scythe",
						"A usefull farming tool indeed."+
						" To some blacksmith one should ask to if he offers any such sharp utensil.");
				addGoodbye("In bocca al lupo...");
			}
		};

		npc.setEntityClass("sickleinghalfelfnpc");
		npc.setPosition(76,97);
		npc.initHP(100);
		npc.setCollisionAction(CollisionAction.REVERSE); // So does not block straw carts
		npc.setDescription("You see Eheneumniranin");
		zone.add(npc);
	}
}
