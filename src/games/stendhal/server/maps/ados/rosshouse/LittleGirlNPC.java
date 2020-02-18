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
package games.stendhal.server.maps.ados.rosshouse;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.common.constants.SkinColor;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * <p>Creates a normal version of Susi in the ross house.
 */
public class LittleGirlNPC implements ZoneConfigurator {

	public final static String SUSI_OUTFIT = "body=2,dress=32,head=0,eyes=0,hair=4";

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		createGirlNPC(zone);
	}

	public void createGirlNPC(final StendhalRPZone zone) {

		final SpeakerNPC npc = new SpeakerNPC("Susi") {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(3, 7));
				nodes.add(new Node(5, 7));
				nodes.add(new Node(5, 3));
				nodes.add(new Node(5, 8));
				nodes.add(new Node(10, 8));
				nodes.add(new Node(10, 12));
				nodes.add(new Node(12, 12));
				nodes.add(new Node(9, 12));
				nodes.add(new Node(9, 11));
				nodes.add(new Node(7, 11));
				nodes.add(new Node(7, 7));
				setPath(new FixedPath(nodes, true));

			}

			@Override
			protected void createDialog() {
				// TODO: Add different greetings depending on whether Susi's is a friend of the player or not
				addGreeting("Hello. Daddy must have left the house door open again. He's always doing that.");
				addJob("I am just a little girl.");
				addGoodbye("Have fun!");

				addQuest("I might see you some time at the #Mine #Town #Revival #Weeks.");

				// Revival Weeks
				add(
					ConversationStates.ATTENDING,
					Arrays.asList("Semos", "Mine", "Town", "Revival", "Weeks"),
					ConversationStates.ATTENDING,
					"During the Revival Weeks at the end of October we celebrate the old and now mostly dead dead Mine Town north of Semos City.",
					null);

				// help
				addHelp("Have fun.");
			}
		};

		npc.setOutfit(SUSI_OUTFIT);
		npc.setOutfitColor("skin", SkinColor.LIGHT);
		npc.setPosition(3, 7);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		npc.setDescription("You see Susi. Did you ever hear the story about her being lost for some time?");
		zone.add(npc);
	}

}
