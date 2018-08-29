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
package games.stendhal.server.maps.amazon.hut;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.CollisionAction;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Builds the jailed Barbarian in Prison Hut on amazon island.
 *
 * @author Teiv
 */
public class JailedBarbNPC implements ZoneConfigurator {
	//
	// ZoneConfigurator
	//

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC JailedBarbNPC = new SpeakerNPC("Lorenz") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(11, 12));
				nodes.add(new Node(11, 10));
				nodes.add(new Node(9, 10));
				nodes.add(new Node(9, 6));
				nodes.add(new Node(11, 6));
				nodes.add(new Node(11, 4));
				nodes.add(new Node(4, 4));
				nodes.add(new Node(4, 6));
				nodes.add(new Node(6, 6));
				nodes.add(new Node(6, 10));
				nodes.add(new Node(4, 10));
				nodes.add(new Node(4, 12));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
			    addGreeting("Flowers, flowers. All over here these ugly flowers!");
				addJob("I belong to the #Guard of the hidden King! Oops, too much information for you!");
				addReply("guard", "Uhm as I said, I didn't say anything to you!");
				addHelp("Kill as much of these ugly Amazonesses as you can, they tried to make me go insane with these ugly flowers all over here.");
				addOffer("Nothing to offer you!");
				addGoodbye("Bye bye, and cut down some of these ugly flowers!");
			}
		};

		JailedBarbNPC.setEntityClass("jailedbarbariannpc");
		JailedBarbNPC.setPosition(11, 12);
		JailedBarbNPC.setCollisionAction(CollisionAction.STOP);
		JailedBarbNPC.initHP(100);
		JailedBarbNPC.setDescription("You see the jailed Barbarian Lorenz. What did he do to the Amazonesses?");
		zone.add(JailedBarbNPC);
	}
}
