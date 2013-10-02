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
 * Provides Eheneumniranin, the sickle wielding NPC.
 * A Halfelf who lost his memory and now works in the grain fields at the farm
 * He will possibly offer a quest to help him find his past.
 *
 * @author omero
 */
public class SickleingHalfelfNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
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
				addJob("To gather the sheaves of grain with my #sickle before taking them to the mill I must... How did I get here?... If I only could remember...");
				addHelp("Ha! Asking for such only reveals how unworthy and undeserving one is... Who am I?... A fog obfuscates my thoughts...");
				addOffer("Oh?! Given I had anything valuable I would offer it willingly for a glimpse of truth...");
				addReply("sickle","A usefull farming tool indeed, like a scythe also is. To some blacksmith you should ask to if he offers any such sharp utensil.");
				addGoodbye("In bocca al lupo...");
			}
	
		};

		npc.setEntityClass("sickleinghalfelfnpc");
		npc.setPosition(76,97);
		npc.initHP(100);
		npc.setCollisionAction(CollisionAction.REVERSE); // So does not block straw carts
		npc.setDescription("You see Eheneumniranin, the Half Elf... He has lost his memory and always looks confused.");
		zone.add(npc);
	}
}
