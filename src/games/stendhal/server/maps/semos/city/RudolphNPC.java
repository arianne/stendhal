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
package games.stendhal.server.maps.semos.city;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.CollisionAction;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.SpeakerNPC;
/**
 * ZoneConfigurator configuring Rudolph the Red-Nosed Reindeer who clops around Semos city during Christmas season
 */
public class RudolphNPC implements ZoneConfigurator {


	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		final SpeakerNPC npc = new SpeakerNPC("Rudolph") {

			@Override
			protected void createPath() {
				final List<Node> path = new LinkedList<Node>();
				path.add(new Node(2, 3));
				path.add(new Node(2, 14));
				path.add(new Node(36, 14));
				path.add(new Node(36, 46));
				path.add(new Node(51, 46));
				path.add(new Node(51, 48));
				path.add(new Node(62, 48));
				path.add(new Node(62, 55));
				path.add(new Node(51, 55));
				path.add(new Node(51, 58));
				path.add(new Node(32, 58));
				path.add(new Node(32, 53));
				path.add(new Node(18, 53));
				path.add(new Node(18, 43));
				path.add(new Node(20, 43));
				path.add(new Node(20, 26));
				path.add(new Node(26, 26));
				path.add(new Node(26, 14));
				path.add(new Node(21, 14));
				path.add(new Node(21, 3));
				setPath(new FixedPath(path, true));
			}

			@Override
			public void createDialog() {
				addGreeting("Hi, my jolly friend. What a wonderful time of year this is!");
				addHelp("Oh, my, I can't help you, sorry. It's not like I can influence Santa at all.");
				addJob("I pull Santa's sleigh on Christmas night. It gives me such pleasure to flash my nose so that Santa can see where he is going.");
				addGoodbye("It was such a pleasure to meet you.");

				// remaining behaviour defined in games.stendhal.server.maps.quests.GoodiesForRudolph

			}

			@Override
			protected void onGoodbye(RPEntity player) {
				setDirection(Direction.DOWN);
			}

		};
		npc.setPosition(2, 3);
		npc.setDirection(Direction.DOWN);
		npc.setDescription("You see Rudolph the Red-Nosed Reindeer. His nose is so big, bright and flashy.");
		npc.setBaseSpeed(1);
		npc.setEntityClass("rudolphnpc");
		npc.setCollisionAction(CollisionAction.REVERSE);
		zone.add(npc);
	}

}
