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
package games.stendhal.server.maps.nalwor.hell;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;

/**
 * Creates the elementals npcs in hell.
 *
 * @author kymara
 */
public class ElementalsNPCs implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPCs(zone);
	}

	private void buildNPCs(final StendhalRPZone zone) {
		final String[] names = {"Savanka", "Xeoilia", "Azira"};
		final String[] descriptions = {"You see Savanka, flying around with her fiery aura.", "You see Xeoilia, calmly walking around.", "You see Azira, flapping around softly."};
		final Node[] start = new Node[] { new Node(115, 6), new Node(122, 12), new Node(117, 10) };
		for (int i = 0; i < 3; i++) {
			final SpeakerNPC npc = new SpeakerNPC(names[i]) {

				@Override
				protected void createPath() {
					final List<Node> nodes = new LinkedList<Node>();
					nodes.add(new Node(115, 6));
                    nodes.add(new Node(119, 6));
					nodes.add(new Node(119, 5));
                    nodes.add(new Node(122, 5));
                    nodes.add(new Node(122, 6));
                    nodes.add(new Node(125, 6));
                    nodes.add(new Node(125, 10));
                    nodes.add(new Node(124, 10));
                    nodes.add(new Node(124, 12));
					nodes.add(new Node(123, 12));
					nodes.add(new Node(123, 15));
                    nodes.add(new Node(124, 15));
					nodes.add(new Node(124, 17));
                    nodes.add(new Node(122, 17));
                    nodes.add(new Node(122, 18));
                    nodes.add(new Node(116, 18));
                    nodes.add(new Node(116, 16));
                    nodes.add(new Node(114, 16));
                    nodes.add(new Node(114, 15));
                    nodes.add(new Node(113, 15));
                    nodes.add(new Node(113, 13));
                    nodes.add(new Node(111, 13));
                    nodes.add(new Node(111, 8));
                    nodes.add(new Node(115, 8));
					setPath(new FixedPath(nodes, true));
				}

				@Override
				protected void createDialog() {
					add(
			     		ConversationStates.IDLE,
						ConversationPhrases.GREETING_MESSAGES,
						new GreetingMatchesNameCondition(getName()), true,
						ConversationStates.IDLE,
						"Speak not to us, the harbingers of Hell!",
						null);

				}
			};
			npc.setEntityClass("fireelementalnpc");
			npc.setPosition(start[i].getX(), start[i].getY());
			npc.setDescription(descriptions[i]);
			npc.setDirection(Direction.DOWN);
			npc.initHP(100);
			zone.add(npc);
		}
	}
}
