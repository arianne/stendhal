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
 * Builds a npc in Ados (name:Fritz) who is an old fisherman on the market
 * 
 * @author storyteller (idea) and Vanessa Julius (implemented)
 *
 */
public class OldFishermanNPC implements ZoneConfigurator {

	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Fritz") {
		    
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(67, 79));
				nodes.add(new Node(62, 79));
                nodes.add(new Node(62, 77));
                nodes.add(new Node(61, 77));  
                nodes.add(new Node(61, 76));
                nodes.add(new Node(58, 76)); 
                nodes.add(new Node(58, 80)); 
                nodes.add(new Node(61, 80));
                nodes.add(new Node(61, 84));
                nodes.add(new Node(71, 84));
                nodes.add(new Node(71, 78));
                nodes.add(new Node(72, 78));
                nodes.add(new Node(72, 75));
                nodes.add(new Node(69, 75));
                nodes.add(new Node(69, 79));
               	setPath(new FixedPath(nodes, true));

			}

			@Override
			protected void createDialog() {
				addGreeting("Ahoy!");
				addHelp("Harr my friend, my whole life long I was sitting in a boat, trying to catch some fish... The feeling you have when fishies hang on your rod is awesome! Did you ever try to catch some by yourself? You can ask my friend Santiago for help, he lives in a fisherhut somewhere in the city!");
				
				addQuest("Ahoy Matey! I don't have any tasks for you but others do!"); 
				addJob("I am a fisherman. For many, many years I went fishing every day. There have been dangerous storms I had to get over and some reeeeeally big fish which almost drown my ship, but of course I always was able to catch them! Hehe! I hope that I will be able to deliver some fish to the market one time...");
				addOffer("My fishing places on the sea are catched by some ugly storm I can't drive out at the moment. Sorry matey, I can't offer you anything at the moment...");
				addGoodbye("See you, matey! And be careful there on this slippy ground!");
				
			}
		};

		npc.setDescription("You see Fritz. No, he doesn't stink! He just didn't wash his clothes for ages after being on the sea for a long time.");
		npc.setEntityClass("oldfishermannpc");
		npc.setPosition(67, 79);
		npc.setDirection(Direction.RIGHT);
		npc.initHP(100);
		zone.add(npc);
	}
}
