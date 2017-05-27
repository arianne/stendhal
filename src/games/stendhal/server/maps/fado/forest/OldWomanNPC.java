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
package games.stendhal.server.maps.fado.forest;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Creates Jefs mother Amber in Fado Forest and other areas (she moves in different zones)
 *
 * @author Vanessa Julius
 */
public class OldWomanNPC implements ZoneConfigurator {

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
		final SpeakerNPC npc = new SpeakerNPC("Amber") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(38,8));
				nodes.add(new Node(14,8));
				nodes.add(new Node(14,30));
				nodes.add(new Node(34,47));
				nodes.add(new Node(45,47));
				nodes.add(new Node(45,61));
				nodes.add(new Node(70,61));
				nodes.add(new Node(70,74));
				nodes.add(new Node(52,74));
				nodes.add(new Node(52,70));
				nodes.add(new Node(29,70));
				nodes.add(new Node(29,96));
				nodes.add(new Node(41,96));
				nodes.add(new Node(41,104));
				nodes.add(new Node(53,104));
				nodes.add(new Node(53,110));
				nodes.add(new Node(42, 110));
				nodes.add(new Node(42, 125));
				nodes.add(new Node(53,125));
				nodes.add(new Node(59,125));
				nodes.add(new Node(59,120));
				nodes.add(new Node(73,120));
				nodes.add(new Node(89,120));
				nodes.add(new Node(89,113));
				nodes.add(new Node(107,113));
				nodes.add(new Node(107,99));
				nodes.add(new Node(114,99));
				nodes.add(new Node(114,95));
				nodes.add(new Node(124,95));
				nodes.add(new Node(124,93));
				nodes.add(new Node(127,93));
				nodes.add(new Node(127,94));
				nodes.add(new Node(125,94));
				nodes.add(new Node(125,110));
				nodes.add(new Node(118,110));
				nodes.add(new Node(118,118));
				nodes.add(new Node(110,118));
				nodes.add(new Node(110,121));
				nodes.add(new Node(99,121));
				nodes.add(new Node(99,118));
				nodes.add(new Node(74,118));
				nodes.add(new Node(74,111));
				nodes.add(new Node(47,111));
				nodes.add(new Node(47,99));
				nodes.add(new Node(33,99));
				nodes.add(new Node(33,88));
				nodes.add(new Node(60,88));
				nodes.add(new Node(60,69));
				nodes.add(new Node(45,69));
				nodes.add(new Node(45,49));
				nodes.add(new Node(52,49));
				nodes.add(new Node(52,38));
				nodes.add(new Node(29,38));
				nodes.add(new Node(29,26));
				nodes.add(new Node(14,26));
				nodes.add(new Node(14,22));
				nodes.add(new Node(9,22));
				nodes.add(new Node(9,11));
				nodes.add(new Node(30,11));
				nodes.add(new Node(30,8));
				nodes.add(new Node(37,8));
				nodes.add(new Node(38,8));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Oh someone met me on my way. Hello.");
				addJob("Actually I don't have any job. I used to be a good mom but I #failed somehow.");
				addReply("failed, fail", "I left my poor son alone for such a long time now. I doubt that he will ever talk to me again but there were so many #problems around...");
				addReply("problem", "I fell in love with a man in Kirdneh called Roger Frampton. Unfortunetly we broke up and I didn't know what to do after so I went out of the city and spent some time on my #own here.");
				addReply("own", "The trees and animals around became great companions of me although I had to give up my real family somehow. I miss my son #Jef so much.");
				addReply("Jef", "Such a gentle and nice young boy. I know that he waits for my return but I'm not ready yet.");
				addQuest("I don't have any quest for you at the moment.");
				addHelp("Visit some of my new #friends here. They are so nice and gentle!");
				addReply("friend", "Aldrin makes really tasty honey and his bees are hard workers for keeping the quality safe.");
				addOffer("Sorry but I don't have anything to sell.");
				addGoodbye("Bye and please be gentle to my son.");
			}
		};

		npc.setEntityClass("oldwomannpc");
		npc.setPosition(38, 8);
		npc.initHP(100);
		npc.setDescription("You see an elder woman. She looks a bit disorientated.");
		zone.add(npc);
	}
}
