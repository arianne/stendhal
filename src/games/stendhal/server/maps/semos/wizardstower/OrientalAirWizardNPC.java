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
 * Jaer, the air wizard of the Wizards Tower
 *
 * see games.stendhal.server.maps.quests.WizardJaerPlainQuest
 */
public class OrientalAirWizardNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildJaer(zone);
	}

	private void buildJaer(final StendhalRPZone zone) {
		final SpeakerNPC jaer = new SpeakerNPC("Jaer") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(40, 44));
				nodes.add(new Node(37, 44));
				nodes.add(new Node(37, 42));
				nodes.add(new Node(33, 42));
				nodes.add(new Node(33, 37));
				nodes.add(new Node(31, 37));
				nodes.add(new Node(31, 36));
				nodes.add(new Node(33, 36));
				nodes.add(new Node(33, 33));
				nodes.add(new Node(32, 33));
				nodes.add(new Node(33, 33));
				nodes.add(new Node(33, 36));
				nodes.add(new Node(31, 36));
				nodes.add(new Node(31, 37));
				nodes.add(new Node(31, 35));
				nodes.add(new Node(33, 35));
				nodes.add(new Node(33, 33));
				nodes.add(new Node(32, 33));
				nodes.add(new Node(33, 33));
				nodes.add(new Node(33, 42));
				nodes.add(new Node(31, 42));
				nodes.add(new Node(39, 42));
				nodes.add(new Node(39, 41));
				nodes.add(new Node(40, 41));
				nodes.add(new Node(40, 34));
				nodes.add(new Node(38, 34));
				nodes.add(new Node(38, 33));
				nodes.add(new Node(40, 33));
				nodes.add(new Node(40, 41));
				nodes.add(new Node(41, 41));
				nodes.add(new Node(41, 43));
				nodes.add(new Node(40, 43));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("I greet you!");
				addHelp("Please excuse me, I am very busy founding the illusionists part at the wizards circle.");
				addJob("I am Jaer. I represent the illusionists of #Cloudburst at the wizards circle.");
				addOffer("Please excuse me, I am very busy founding the illusionists part at the wizards circle.");
				addQuest("The magic in this world has just begun and I am very busy founding the illusionists of #Cloudburst at the wizards circle. I will tell you in time, when I have a quest for you.");
				addReply("Cloudburst", "Cloudburst, the school of illusions, levitates between the clouds in the sky.");
				addGoodbye("Goodbye!");

			} //remaining behaviour defined in maps.quests.WizardJaerPlainQuest
		};

		jaer.setDescription("You see Jaer, the master of illusion.");
		jaer.setEntityClass("orientalwizardnpc");
		jaer.setPosition(40, 43);
		jaer.initHP(100);
		zone.add(jaer);
	}
}
