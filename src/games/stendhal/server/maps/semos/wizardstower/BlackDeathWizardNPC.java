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
 * Ravashack, the death wizard of the Wizards Tower
 *
 * see games.stendhal.server.maps.quests.WizardRavashackPlainQuest
 */
public class BlackDeathWizardNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildRavashack(zone);
	}

	private void buildRavashack(final StendhalRPZone zone) {
		final SpeakerNPC ravashack = new SpeakerNPC("Ravashack") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(5, 18));
				nodes.add(new Node(7, 18));
				nodes.add(new Node(7, 15));
				nodes.add(new Node(7, 20));
				nodes.add(new Node(12, 20));
				nodes.add(new Node(12, 21));
				nodes.add(new Node(9, 21));
				nodes.add(new Node(9, 25));
				nodes.add(new Node(11, 25));
				nodes.add(new Node(9, 25));
				nodes.add(new Node(9, 26));
				nodes.add(new Node(9, 21));
				nodes.add(new Node(2, 21));
				nodes.add(new Node(2, 25));
				nodes.add(new Node(4, 25));
				nodes.add(new Node(4, 27));
				nodes.add(new Node(6, 27));
				nodes.add(new Node(4, 27));
				nodes.add(new Node(4, 28));
				nodes.add(new Node(2, 28));
				nodes.add(new Node(2, 18));
				nodes.add(new Node(7, 18));
				nodes.add(new Node(7, 15));
				nodes.add(new Node(7, 18));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Greetings mortal! What is it this time?");
				addHelp("Excuse me mortal, I am very busy, working to establish the necromancers part at the wizards circle.");
				addJob("I am Ravashack. I represent the necromancers of #Wraithforge at the wizards circle.");
				addOffer("Excuse me mortal, I am very busy, working to establish the necromancers part at the wizards circle.");
				addQuest("The magic in this world has just begun and I am very busy to establish the necromancers of #Wraithforge at the wizards circle. I will tell you in time, when I have a quest for you.");
				addReply("Wraithforge", "In the centre of the fields of glory lies Wraithforge, the school of dark magic.");
				addGoodbye("So long, mortal!");

			} //remaining behaviour defined in maps.quests.WizardRavashackPlainQuest
		};

		ravashack.setDescription("You see Ravashack, the mighty and mystical Necromancer.");
		ravashack.setEntityClass("largeblackwizardnpc");
		ravashack.setPosition(5, 17);
		ravashack.initHP(100);
		zone.add(ravashack);
	}
}
