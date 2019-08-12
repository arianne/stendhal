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
package games.stendhal.server.maps.ados.city;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.CollisionAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;

/**
 * Creates the NPCs and portals in Ados City.
 *
 * @author hendrik
 */
public class KidsNPCs implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildKids(zone);
	}

	private void buildKids(final StendhalRPZone zone) {
		final String[] names = { "Jens", "George", "Anna" };
		final String[] classes = { "kid3npc", "kid4npc", "kid5npc" };
		final String[] descriptions = {"You see Jens. He seems to be a bit bored.", "You see George who loves playing roughly.", "You see Anna. She is a sweet girl who searches for toys."};
		final Node[] start = new Node[] {new Node(45, 31), new Node(46, 34), new Node(46, 37)};
		for (int i = 0; i < 3; i++) {
			final SpeakerNPC npc = new SpeakerNPC(names[i]) {
				@Override
				protected void createPath() {
					final List<Node> nodes = new LinkedList<Node>();
					nodes.add(new Node(46, 30));
					nodes.add(new Node(45, 30));
					nodes.add(new Node(45, 29));
					nodes.add(new Node(40, 29));
					nodes.add(new Node(40, 32));
					nodes.add(new Node(34, 32));
					nodes.add(new Node(34, 36));
					nodes.add(new Node(39, 36));
					nodes.add(new Node(39, 41));
					nodes.add(new Node(40, 41));
					nodes.add(new Node(40, 39));
					nodes.add(new Node(45, 39));
					nodes.add(new Node(45, 43));
					nodes.add(new Node(51, 43));
					nodes.add(new Node(51, 37));
					nodes.add(new Node(46, 37));
					setPath(new FixedPath(nodes, true));
				}

				@Override
				protected void createDialog() {
					// Anna is special because she has a quest
					if (!getName().equals("Anna")) {
						add(ConversationStates.IDLE,
					        ConversationPhrases.GREETING_MESSAGES,
					        new GreetingMatchesNameCondition(getName()), true,
					        ConversationStates.IDLE,
					        "Mummy said, we are not allowed to talk to strangers. Bye.",
					        null);
					}

					addGoodbye("Bye bye!");
				}
			};

			npc.setEntityClass(classes[i]);
			npc.setPosition(start[i].getX(), start[i].getY());
			npc.setDescription(descriptions[i]);
			npc.setDirection(Direction.DOWN);
			npc.setCollisionAction(CollisionAction.REROUTE);
			npc.initHP(100);
			zone.add(npc);
		}
	}
}
