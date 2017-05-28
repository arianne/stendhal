/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

package games.stendhal.server.maps.orril.constantines_villa;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Builds a npc in Constantines Villa (name:Cameron) who is a librarian
 *
 * @author storyteller (idea) and Vanessa Julius (implemented)
 *
 */

public class LibrarianNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Cameron") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(11, 17));
				nodes.add(new Node(11, 7));
                nodes.add(new Node(18, 7));
                nodes.add(new Node(18, 17));
               	setPath(new FixedPath(nodes, true));

			}

			@Override

			//Greeting and goodbye message in quest code TheMissingBooks.java

			protected void createDialog() {
				addHelp("Hmm... I think you are one of Constantines friends, so I can give you a hint if you step #closer...");
				addReply("closer", "That is good now! You should visit a friend of mine, Imorgen somewhere around Fado forest... Her grandma is ill and she might need some #help...");
				addReply("help", "She was worried about her grandma last time I saw her. Maybe she has something in return for your help...");
				addJob("I'm #Constantines librarian! Unfortunately I lost some books which are really precious ones... I hope he'll never learn about it!");
				addReply(Arrays.asList("Constantine", "Constantines"),
		        "He is my boss and the owner of this huge Villa, I haven't seen him for some time in here, seems like he is busy or on vacation...");
				addReply("vacation", "Don't ask me where he is, I'm the last one who gets any information in here... And books are more important for me anyway.");
				addOffer("Sorry, I have nothing to offer for you.");
				addGoodbye("We read...ehm...see us again soon!");

			}
		};

		npc.setDescription("Cameron looks a bit crazy but don't be afraid:  He is just a librarian with a bit of a problem around his most important treasure.");
		npc.setEntityClass("librarianconstantinenpc");
		npc.setPosition(11, 17);
		npc.initHP(100);
		zone.add(npc);
	}
}
