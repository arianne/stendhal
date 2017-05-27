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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.ExamineChatAction;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;

/**
 * Builds a NPC in Semos Mine (name:Barbarus) who is a miner and informs players about his job
 *
 * @author storyteller and Vanessa Julius
 *
 */
public class MinerNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Barbarus") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(57, 78));
				nodes.add(new Node(55, 78));
                nodes.add(new Node(55, 80));
                nodes.add(new Node(53, 80));
                nodes.add(new Node(53, 82));
                nodes.add(new Node(55, 82));
                nodes.add(new Node(55, 84));
                nodes.add(new Node(59, 84));
                nodes.add(new Node(59, 78));
                nodes.add(new Node(58, 78));
                nodes.add(new Node(57, 78));
               	setPath(new FixedPath(nodes, true));

			}

			@Override
			protected void createDialog() {
				addGreeting("Good luck!");
				addReply("good luck", "Good luck! I hope you'll leave this mine healthy!");
				addReply("glück auf", "Glüüück Auf, Glück Auf...! *sing");
				addHelp("Always remember your way! Otherwise you may get lost in these tunnels which run deep into the mountain! And... before I forget: There seems to be something dangerous in the #mine... I hear very strange #sounds from time to time which come from somewhere down...");
				addReply("mine","This mine is a huge system of tunnels, dug into the mountain a long time ago. Nobody knows all ways through the tunnels any more, except the dwarves maybe, harhar... *cough*");
				addReply("sounds","The sounds are very weird... Sometimes they sound like someone is shouting from far away... just like commanding soldiers or so... I also heard steps in the shadows a few times... that's really scary...");
				addOffer("I can sell you a useful tool for getting some coal with. Most of my friends who used to work with me left me some time ago, so you can buy some #picks which they left here. I'd also like to give you some of my drink and food but there isn't much left... I still need to work some hours so I need it for myself, sorry... But I can show you a hand drawn #map of the mine if you want.");
				addReply("picks", "You need a pick for getting some coal from places on the walls in Semos Mine.");
				final Map<String, Integer> offerings = new HashMap<String, Integer>();
                offerings.put("pick", 400);
                new SellerAdder().addSeller(this, new SellerBehaviour(offerings), false);
				addQuest("Sorry but as you can see, I'm covered with dust and still didn't finish my work up yet. I can't think about any quests for you, but you can help me to gain some coal.");
				addJob("I am a miner. Working in a mine is quite hard work. If you get deep and deeper into the earth it gets very warm and even more dusty. And you can hardly see anything in this low light...");
				addReply("map","This is a map of the Semos Mine which I have drawn by myself some time ago. It may help you to find the way. But take care, not everything is exactly right!",
						new ExamineChatAction("map-semos-mine.png", "Semos Mine", "Rough map of Semos Mine"));
				addGoodbye("Nice to meet you. Good luck!");

			}
		};

		npc.setDescription("You see Barbarus. He looks dirty and sweats quite much. His face and arms are nearly black because they are covered with dust.");
		npc.setEntityClass("minernpc");
		npc.setPosition(57, 78);
		npc.setDirection(Direction.RIGHT);
		npc.initHP(100);
		zone.add(npc);
	}
}
