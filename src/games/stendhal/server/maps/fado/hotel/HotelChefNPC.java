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
package games.stendhal.server.maps.fado.hotel;

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
 * Builds a NPC in a house on Ados market (name:Stefan) who is the daughter of fisherman Fritz
 * 
 * @author Vanessa Julius 
 *
 */
public class HotelChefNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Stefan") {
		    
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(92, 9));
				nodes.add(new Node(98, 9));
	            nodes.add(new Node(98, 2));
	            nodes.add(new Node(93, 2));  
	            nodes.add(new Node(93, 4));
	            nodes.add(new Node(91, 4)); 
	            nodes.add(new Node(91, 3)); 
	            nodes.add(new Node(90, 3));
	            nodes.add(new Node(90, 11));
	            nodes.add(new Node(98, 11));
	            nodes.add(new Node(98, 9));
	           	setPath(new FixedPath(nodes, true));		
			}

			@Override
			protected void createDialog() {
				addGreeting("Welcome in the Fado hotel kitchen, stranger!");
				addHelp("I'm really #stressed up here with my work. Sorry, but I can't give you any advice at the moment.");
				addReply("stressed", "It's high season at the moment! We get lots of reservations which means more #guests and more work for everyone.");
				addReply("guest", "Most of them visit Fado for #getting #married. I can understand their choice of Fado. It's a beautiful city.");
				addReply("getting married", "Didn't you know, that Fado is the most known wedding town in whole Faiumoni? You have to visit our church, it's so lovely!");
				addQuest("I'm so busy at the moment thinking about what I can do to get some help #somewhere..."); 
				addReply("somewhere", "Yes, somewhere... I doubt that the problem can be solved in my kitchen alone... It's tiny!");
				addJob("Some weeks ago, I got the job offer to become the hotel chef here. What I didn't know: I'm the only #cook at the moment!");
				addReply("cook", "Being a cook is awesome! I love all kind of food and experiment around with different dishes is just fun for me.");
				addOffer("The kitchen isn't open at the moment and before it can be opened, I have to think about a solution for my #problem in here...");
				addReply("problem", "Being alone in a hotel kitchen can't work at all!");
				addGoodbye("Goodbye! Have a nice stay in Fado!");
			}

		};

		npc.setDescription("You see Stefan, the young chef of the Fado hotel.");
		npc.setEntityClass("hotelchefnpc");
		npc.setPosition(92, 9);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		zone.add(npc);
	}
}
