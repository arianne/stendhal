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
package games.stendhal.server.maps.deniran.cityoutside;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.CollisionAction;
import games.stendhal.server.entity.npc.SpeakerNPC;

public class DeniranMarketSellerGroceryNPC implements ZoneConfigurator {

	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Ambrogio") {

			@Override
			public void createDialog() {
				addGreeting("Hello visitor! " +
							"You need stuff? I sell stuff... " + 
							"Oh, I should set up one of those blackboards where offers are listed");
				addOffer("I sell stuff... Oh, I should set up one of those blackboards where offers are listed");
				addJob("I sell stuff... Oh, I should set up one of those blackboards where offers are listed");
				addHelp("I sell stuff...Oh, I should set up one of those blackboards where offers are listed");
				addGoodbye("So long...");
			}

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(23, 116));
				nodes.add(new Node(29, 116));
				nodes.add(new Node(29, 120));
				nodes.add(new Node(20, 120));
				setPath(new FixedPath(nodes, true));
			}
		};
		
		//npc.setEntityClass("fatsellernpc");
		npc.setEntityClass("deniranmarketsellernpc1grocery");
		npc.setPosition(26, 122);
		npc.setCollisionAction(CollisionAction.REROUTE);
		npc.setDescription("You see a busy marketplace seller...");
		zone.add(npc);
	}
}
