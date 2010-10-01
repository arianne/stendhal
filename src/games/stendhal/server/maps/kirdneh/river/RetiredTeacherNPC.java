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
package games.stendhal.server.maps.kirdneh.river;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Builds a npc in the house at Kirdneh River (name:Ortiv Milquetoast) who is a coward retired teacher
 * 
 * @author Vanessa Julius 
 *
 */
public class RetiredTeacherNPC implements ZoneConfigurator {

	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Ortiv Milquetoast") {
		    
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(15, 28));
				nodes.add(new Node(27, 28));
                nodes.add(new Node(27, 19));
                nodes.add(new Node(26, 19));  
                nodes.add(new Node(26, 16));
                nodes.add(new Node(28, 16)); 
                nodes.add(new Node(28, 11)); 
                nodes.add(new Node(24, 11));
                nodes.add(new Node(24, 20));
                nodes.add(new Node(27, 20));
                nodes.add(new Node(27, 26));
                nodes.add(new Node(14, 26));
                nodes.add(new Node(14, 25));
                nodes.add(new Node(13, 25));
                nodes.add(new Node(13, 20));
                nodes.add(new Node(14, 20));
                nodes.add(new Node(14, 14));
                nodes.add(new Node(4, 14));
                nodes.add(new Node(4, 6));
                nodes.add(new Node(10, 6));
                nodes.add(new Node(10, 3));
                nodes.add(new Node(6, 3));
                nodes.add(new Node(6, 6));
                nodes.add(new Node(4, 6));
                nodes.add(new Node(4, 22));
                nodes.add(new Node(13, 22));
                nodes.add(new Node(13, 27));
               	setPath(new FixedPath(nodes, true));

			}

			@Override
			protected void createDialog() {
				addGreeting("Ohh a stranger found my hidden house, welcome!");
				addHelp("Never ever get into trouble with assassins when they are stronger than you! They will come to get you otherwise!");
				addQuest("I currently work on a mixture to keep the rowdy gang downstairs... Maybe you can help me later with getting me some of the ingredients I'll need.");
				addJob("I was a teacher for alchemy once but some of my students turned into ugly bandits and assassins... I don't know what happens in Faiumoni at the moment as though as I just stay in my save house the whole day long...");
				addOffer("Sorry, but I can't offer you anything... I have some mayor problems in my basement at the moment...");
				addGoodbye("Take care of you and please be fast with visiting me again, I'm scared alone!");
				
			}
		};

		npc.setDescription("You see Ortiv Milquetoast. Although he has some kind of teacher aura around him, he seems to be quite scared and nervous.");
		npc.setEntityClass("retiredteachernpc");
		npc.setPosition(15, 28);
		npc.setDirection(Direction.RIGHT);
		npc.initHP(100);
		zone.add(npc);
	}
}
