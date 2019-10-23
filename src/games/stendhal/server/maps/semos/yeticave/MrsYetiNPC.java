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
package games.stendhal.server.maps.semos.yeticave;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.CollisionAction;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.QuestCompletedSellerBehaviour;


public class MrsYetiNPC implements ZoneConfigurator {

	public static final int BUYING_PRICE = 10;
 	private static final String QUEST_SLOT = "mrsyeti";

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildYeti(zone);
	}

	private void buildYeti(final StendhalRPZone zone) {
		final SpeakerNPC yetifemale = new SpeakerNPC("Mrs. Yeti") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(102, 19));
				nodes.add(new Node(104, 19));
				nodes.add(new Node(102, 15));
				nodes.add(new Node(102, 11));
				nodes.add(new Node(97, 11));
				nodes.add(new Node(97, 13));
				nodes.add(new Node(86, 13));
				nodes.add(new Node(86, 15));
				nodes.add(new Node(82, 15));
				nodes.add(new Node(82, 17));
				nodes.add(new Node(80, 17));
				nodes.add(new Node(83, 25));
				nodes.add(new Node(83, 31));
				nodes.add(new Node(85, 31));
				nodes.add(new Node(85, 33));
				nodes.add(new Node(88, 33));
				nodes.add(new Node(88, 31));
				nodes.add(new Node(98, 31));
				nodes.add(new Node(98, 23));
				nodes.add(new Node(100, 23));
				nodes.add(new Node(100, 21));
				nodes.add(new Node(102, 21));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {

				final Map<String, Integer> items = new HashMap<String, Integer>();
				items.put("roach", BUYING_PRICE);

				new SellerAdder().addSeller(this, new QuestCompletedSellerBehaviour(QUEST_SLOT, "I have a #task for you before I will sell you anything.", items));
				// for quest see games.stendhal.server.maps.quest.HelpMrsYeti
				addGreeting("Greetings, strange foreigner!");
				addJob("I go out to hunt for food while Mr. Yeti makes his snow sculptures.");
				addHelp("Be careful of the creatures lurking in these caves!");
				addGoodbye();
			}
		};

		yetifemale.setEntityClass("yetifemalenpc");
		yetifemale.setDescription("You see Mrs. Yeti, a white hairy lady with huge feet!");
		yetifemale.setPosition(102, 19);
		yetifemale.setCollisionAction(CollisionAction.STOP);
		yetifemale.initHP(100);
		zone.add(yetifemale);
	}
}
