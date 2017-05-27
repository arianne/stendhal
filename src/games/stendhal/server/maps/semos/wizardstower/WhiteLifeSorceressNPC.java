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
package games.stendhal.server.maps.semos.wizardstower;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Elana, the life wizard of the Wizards Tower
 *
 * see games.stendhal.server.maps.quests.WizardElanaPlainQuest
 */
public class WhiteLifeSorceressNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildElana(zone);
	}

	private void buildElana(final StendhalRPZone zone) {
		final SpeakerNPC elana = new SpeakerNPC("Elana") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(39, 18));
				nodes.add(new Node(39, 17));
				nodes.add(new Node(35, 17));
				nodes.add(new Node(35, 15));
				nodes.add(new Node(35, 20));
				nodes.add(new Node(33, 20));
				nodes.add(new Node(33, 25));
				nodes.add(new Node(32, 25));
				nodes.add(new Node(32, 27));
				nodes.add(new Node(32, 25));
				nodes.add(new Node(31, 25));
				nodes.add(new Node(32, 25));
				nodes.add(new Node(32, 27));
				nodes.add(new Node(32, 25));
				nodes.add(new Node(33, 25));
				nodes.add(new Node(33, 21));
				nodes.add(new Node(30, 21));
				nodes.add(new Node(40, 21));
				nodes.add(new Node(40, 29));
				nodes.add(new Node(39, 29));
				nodes.add(new Node(39, 27));
				nodes.add(new Node(36, 27));
				nodes.add(new Node(38, 27));
				nodes.add(new Node(38, 25));
				nodes.add(new Node(40, 25));
				nodes.add(new Node(40, 20));
				nodes.add(new Node(39, 20));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hello!");
				addHelp("Please excuse me, I am very busy founding the white mages part at the wizards circle.");
				addJob("I am Elana. I represent the white mages of #Lavitae at the wizards circle.");
				addOffer("Please excuse me, I am very busy founding the white mages part at the wizards circle.");
				addQuest("The magic in this world has just begun and I am very busy founding the white mages of #Lavitae at the wizards circle. I will tell you in time, when I have a quest for you.");
				addReply("Lavitae", "Lavitae is the school of white magic. To be near its beginnings, it lies close to Ados.");
				addGoodbye("Goodbye!");

			} //remaining behaviour defined in maps.quests.WizardElanaPlainQuest
		};

		elana.setDescription("You see Elana, the divine enchantress of Life.");
		elana.setEntityClass("whitesorceressnpc");
		elana.setPosition(40, 18);
		elana.initHP(100);
		zone.add(elana);
	}
}
