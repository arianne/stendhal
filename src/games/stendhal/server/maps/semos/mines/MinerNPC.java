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
package games.stendhal.server.maps.semos.mines;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.ExamineChatAction;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Builds a NPC in Semos Mine (name:Barbarus) who is a miner and informs players about his job
 * 
 * @author storyteller and Vanessa Julius 
 *
 */
public class MinerNPC implements ZoneConfigurator {

	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Barbarus") {
		    
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(70, 115));
				nodes.add(new Node(69, 117));
                nodes.add(new Node(69, 120));
                nodes.add(new Node(72, 120));  
                nodes.add(new Node(76, 123));
                nodes.add(new Node(80, 121)); 
                nodes.add(new Node(83, 119)); 
                nodes.add(new Node(83, 115));
                nodes.add(new Node(79, 115));
                nodes.add(new Node(79, 113));
                nodes.add(new Node(74, 113));
                nodes.add(new Node(74, 112));
                nodes.add(new Node(70, 112));
               	setPath(new FixedPath(nodes, true));

			}

			@Override
			protected void createDialog() {
				addGreeting("Good luck!");
				addHelp("Always remember your way! Otherwise you may get lost in these tunnels which run deep into the mountain! And... before I forget: There seems to be something dangerous in the #mine... I hear very strange #sounds from time to time which come from somewhere down...");
				addReply("mine","This mine is a huge system of tunnels, dug into the mountain a long time ago. Nobody knows all ways through the tunnels any more, except the dwarves maybe, harhar... *cough*");
				addReply("sounds","The sounds are very weird... Sometimes they sound like someone is shouting from far away... just like commanding soldiers or so... I also heard steps in the shadows a few times... that's really scary...");
				addOffer("I can sell you a useful tool for getting some coal with. Most of my friends who used to work with me left me some time ago, so you can buy some #picks which they leave here.");
				addReply("picks", "You need a pick for getting some coal from places on the walls in Semos Mine.");
				final Map<String, Integer> offerings = new HashMap<String, Integer>();
                offerings.put("pick", 450);
                new SellerAdder().addSeller(this, new SellerBehaviour(offerings), false);
				addQuest("Sorry but as you can see, I'm covered in dust and still didn't finish my work up yet. I can't think about any quests for you, but you can help me to gain some coal."); 
				addJob("I am a miner. Working in a mine is quite hard work. If you get deep and deeper into the earth it gets very warm and even more dusty. And you can hardly see anything in this low light...");
				addOffer("I am very sorry, I think I cannot offer you anything... I'd like to give you some of my drink and food but there isn't much left... I still need to work some hours so I need it for myself, sorry...But I can show you a hand drawn #map of the Mine if you want.");
				addReply("map","There you have a hand-drawn map of myself. But take care, not everything will be right to 100%!",
						new ExamineChatAction("map-semos-mine.png", "Semos Mine", "Rough map of Semos Mine"));
				addGoodbye("Nice to meet you. Good luck!");
				
			}
		};

		npc.setDescription("You see Barbarus. He looks dirty and sweats quite much. His face and arms are nearly black because they are covered with dust.");
		npc.setEntityClass("minernpc");
		npc.setPosition(70, 115);
		npc.setDirection(Direction.RIGHT);
		npc.initHP(100);
		zone.add(npc);
	}
}
