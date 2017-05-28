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
package games.stendhal.server.maps.ados.tavern;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;

/**
 * Ados Tavern (Inside / Level 0).
 *
 * @author kymara
 */
public class BarmanNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildTavern(zone);
	}

	private void buildTavern(final StendhalRPZone zone) {
		final SpeakerNPC barman = new SpeakerNPC("Dale") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(27, 2));
				nodes.add(new Node(23, 2));
				nodes.add(new Node(23, 5));
				nodes.add(new Node(27, 5));
				nodes.add(new Node(27, 8));
				nodes.add(new Node(24, 8));
				nodes.add(new Node(24, 12));
                nodes.add(new Node(28, 12));
                nodes.add(new Node(28, 6));
                nodes.add(new Node(23, 6));
                nodes.add(new Node(23, 2));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hey, good looking ...");
				addJob("I keep the ladies happy. How you doin'?");
				addQuest("Just sit back, relax and enjoy the show.");
				addHelp("This is the room for the ladies. Guys hang out in the other bar, Coralia serves the gents.");
				final Map<String, Integer> offerings = new HashMap<String, Integer>();
				offerings.put("wine", 20);
				offerings.put("pina colada", 100);
				offerings.put("chocolate bar", 100);
				new SellerAdder().addSeller(this, new SellerBehaviour(offerings));
				addGoodbye("See you around, sweetcheeks.");
			}
		};

		barman.setEntityClass("barman3npc");
		barman.setPosition(27, 2);
		barman.initHP(100);
		barman.setDescription("You see a hot looking guy. His name is Dale and he describes himself as an expert on women...");
		zone.add(barman);
	}
}
