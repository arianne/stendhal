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
package games.stendhal.server.maps.ados.market;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Builds a npc in Ados (name:Haunchy Meatoch) who is a grillmaster on the market
 *
 * @author storyteller (idea) and Vanessa Julius (implemented)
 *
 */
public class BBQGrillmasterNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Haunchy Meatoch") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(14, 25));
				nodes.add(new Node(15, 25));
                nodes.add(new Node(12, 25));
                nodes.add(new Node(16, 25));
                nodes.add(new Node(16, 24));
                nodes.add(new Node(16, 25));
                nodes.add(new Node(15, 25));
                nodes.add(new Node(12, 25));
                nodes.add(new Node(17, 25));
                nodes.add(new Node(17, 24));
                nodes.add(new Node(17, 25));
                nodes.add(new Node(13, 25));
                nodes.add(new Node(14, 25));
               	setPath(new FixedPath(nodes, true));

			}

			@Override
			protected void createDialog() {
				addGreeting("Hey! Nice day for a BBQ!");
				addHelp("Unfortunately the steaks aren't ready yet... If you are hungry and can't wait, you could check some offers in the near out like the Blacksheep offers near the fisherhuts in Ados or you can take a ferry to Athor for getting some nice snacks...");
				addJob("I am a grillmaster as you can probably see. I love the smell of fresh grilled meat!");
				addOffer("I hope that my steaks will be ready soon. Please be a bit patient or have some other snacks first.");
				addGoodbye("A nice day to you! Always keep your fire burning!");

			}
		};

		npc.setDescription("You see Haunchy Meatoch. He is surrounded by a nice smell of fresh grilled meat.");
		npc.setEntityClass("bbqgrillmasternpc");
		npc.setPosition(14, 25);
		npc.setDirection(Direction.RIGHT);
		npc.initHP(100);
		zone.add(npc);
	}
}
