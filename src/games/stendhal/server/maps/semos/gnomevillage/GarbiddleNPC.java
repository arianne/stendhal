/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.semos.gnomevillage;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;


/**
 * Inside Gnome Village.
 */
public class GarbiddleNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildgarbiddle(zone);
	}

	private void buildgarbiddle(final StendhalRPZone zone) {
		final SpeakerNPC garbiddle = new SpeakerNPC("Garbiddle") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(46, 125));
				nodes.add(new Node(50, 125));
				setPath(new FixedPath(nodes, true));

			}

			@Override
			protected void createDialog() {
				addGreeting("Welcome to our wonderful village.");
				addJob("I'm here to buy supplies for a rainy day.");
				addHelp("I buy several things. Please read the sign to see what we need.");
				addOffer("Read the sign to see what we need.");
				addQuest("Thanks for asking, but I am fine.");
				addGoodbye("Bye now. So glad you stopped in to visit us.");
			}
		};

		garbiddle.setEntityClass("gnomenpc");
		garbiddle.setPosition(46, 125);
		garbiddle.initHP(100);
		garbiddle.setDescription("You see Garbiddle, the tiny gnome lady. She waits for customers.");
		zone.add(garbiddle);
	}
}
