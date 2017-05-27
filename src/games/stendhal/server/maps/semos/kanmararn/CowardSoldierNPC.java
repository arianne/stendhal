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
package games.stendhal.server.maps.semos.kanmararn;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

public class CowardSoldierNPC implements ZoneConfigurator {


   /**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildHideoutArea(zone);
	}

	private void buildHideoutArea(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Henry") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(57, 113));
				nodes.add(new Node(59, 113));
				nodes.add(new Node(59, 115));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Ssshh! Silence or you will attract more #dwarves.");
				addJob("I'm a soldier in the army.");
				addGoodbye("Bye and be careful with all those dwarves around!");
				addHelp("I need help myself. I got separated from my #group. Now I'm all alone.");
				addReply(Arrays.asList("dwarf", "dwarves"),
					"They are everywhere! Their #kingdom must be close.");
				addReply(Arrays.asList("kingdom", "Kanmararn"),
					"Kanmararn, the legendary city of the #dwarves.");
				addReply("group",
					"The General sent five of us to explore this area in search for #treasure.");
				addReply("treasure",
					"A big treasure is rumored to be #somewhere in this dungeon.");
				addReply("somewhere", "If you #help me I might give you a clue.");
			}
			// remaining behaviour is defined in maps.quests.KanmararnSoldiers.
		};

		npc.setEntityClass("youngsoldiernpc");
		npc.setDescription("You see Henry. He is one of the lost soldiers of Semos and hides himself in the dark cave...");
		npc.setPosition(57, 113);
		npc.setBaseHP(100);
		npc.initHP(20);
		npc.setLevel(5);
		zone.add(npc);
	}
}
