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

//import java.util.LinkedList;
//import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
//import games.stendhal.server.core.pathfinder.FixedPath;
//import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.impl.MonologueBehaviour;

/**
 * Provides Ermenegilda, a Healer for Deniran
 * 
 * @author omero
 * 
 */
public class DeniranOldWitchHealerNPC implements ZoneConfigurator {
	
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		final String[] mumbles = {
			"A pinch of bleu!",
			"A touch of fringe!",
			"A glimpse of strange..."
		};
		new MonologueBehaviour(buildNPC(zone), mumbles, 1);
	}

	private SpeakerNPC buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Ermenegilda") {
			
			@Override
			public void createDialog() {
				addGreeting("Hello");
				addJob("I can heal you");
				addHelp("I can heal you");
				addOffer("I can heal you");
				addGoodbye("Goodbye");
			}
		};

		/**
		@Override
		protected void createPath() {
			final List<Node> nodes = new LinkedList<Node>();
			nodes.add(new Node(18, 116));
			nodes.add(new Node(18, 119));
			setPath(new FixedPath(nodes, true));
		}
		*/

		// Finalize Ermenegilda
		npc.setEntityClass("oldwitchnpc");
		npc.setDescription("You see Ermenegilda... Maybe");
		npc.setPosition(18,105);
		npc.initHP(100);
		zone.add(npc);
		return npc;	
	}
}
