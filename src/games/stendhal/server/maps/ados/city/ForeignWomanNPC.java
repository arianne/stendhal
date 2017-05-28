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
 * Builds a npc in Ados (name:Marla) who is there on vacation
 *
 * @author erdnuggel (idea), madmetzger and Vanessa Julius
 *
 */
public class ForeignWomanNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Marla") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(73, 7));
				nodes.add(new Node(73, 1));
                nodes.add(new Node(70, 1));
                nodes.add(new Node(69, 10));
                nodes.add(new Node(69, 12));
                nodes.add(new Node(70, 12));
                nodes.add(new Node(70, 34));
                nodes.add(new Node(75, 33));
                nodes.add(new Node(75, 24));
                nodes.add(new Node(74, 24));
                nodes.add(new Node(74, 9));
                nodes.add(new Node(73, 9));
               	setPath(new FixedPath(nodes, true));

			}

			@Override
			protected void createDialog() {
				addGreeting("Goeiedag");
				addHelp("Did you visit the wise man in the library up yet? His name is Wikipedian and he is popular for his huge knowledge.");
				addQuest("Since I am living here in Ados I dont need any kind of help, but thank you.");
				addJob("No, I am too old for working.");
				addOffer("I can offer only this nice air, it smells of the sea.");
				addGoodbye("Totsiens.");

			}
		};

		npc.setDescription("You see Marla. She made a long journey till reaching her resort.");
		npc.setEntityClass("womanexoticdressnpc");
		npc.setPosition(73, 7);
		npc.setDirection(Direction.RIGHT);
		npc.initHP(100);
		zone.add(npc);
	}
}
