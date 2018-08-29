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
package games.stendhal.server.maps.ados.market;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.CollisionAction;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Builds a npc in Ados (name:Florence Boullabaisse) who is a fish soup maker on the market
 *
 * @author Krupi (fish soup idea) Vanessa Julius (implemented)
 *
 */
public class FishSoupMakerNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Florence Boullabaisse") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(63, 14));
				nodes.add(new Node(70, 14));
                nodes.add(new Node(70, 10));
                nodes.add(new Node(67, 10));
                nodes.add(new Node(67, 14));
                nodes.add(new Node(64, 14));
                nodes.add(new Node(64, 10));
                nodes.add(new Node(63, 10));
               	setPath(new FixedPath(nodes, true));

			}

			@Override
			protected void createDialog() {
				//addGreeting();
				addHelp("I can cook a really tasty fish soup for you but if you are not into fish, I can suggest a good friend of mine to you, Old Mother Helena in Fado. She makes the best vegetable soup in the whole of Faiumoni!");

				addQuest("I don't have any quests for you, but I can offer you some fresh made fish soup for your travels.");
				addJob("I am a trained cook but specialized into soups. My most favourite soup is a fish soup but I also like normal ones...");
				addOffer("If you are really hungry or need some food for your travels, I can cook a really tasty fish soup for you after a selfmade receipe.");
				addGoodbye("Have a nice stay and day on Ados market!");
				addEmotionReply("cuddle", "cuddle");
			}
		};

		npc.setDescription("You see Florence Boullabaisse. She is an excellent soup chef.");
		npc.setEntityClass("fishsoupmakernpc");
		npc.setPosition(63, 14);
		npc.setCollisionAction(CollisionAction.STOP);
		npc.setDirection(Direction.RIGHT);
		npc.initHP(100);
		zone.add(npc);
	}
}
