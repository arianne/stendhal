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
package games.stendhal.server.maps.semos.hostel;

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;

/**
 * Creates kids who are sleeping in semos hostel.
 *
 * @author kymara
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
		buildNPCs(zone);
	}

	private void buildNPCs(final StendhalRPZone zone) {
		final String[] names = {"Taylor", "Covester", "Mick", "Richard"};
		final String[] classes = { "kid2npc", "kid1npc", "kid9npc", "kid8npc" };
		final String[] descriptions = {"You see Taylor. She is deeply sleeping.", "You see Covester. He snores quietly.", "You see Mick. He seems to be really tired.", "You see Richard. He had some stressful vacation days."};
		final int[][] start = { {3, 3}, {9, 3}, {15, 3}, {21, 3} };
		for (int i = 0; i < 4; i++) {
			final SpeakerNPC npc = new SpeakerNPC(names[i]) {
				@Override
				protected void createPath() {
					// they sleeping!
					setPath(null);
				}

				@Override
				protected void createDialog() {
					add(
			     		ConversationStates.IDLE,
						ConversationPhrases.GREETING_MESSAGES,
						new GreetingMatchesNameCondition(getName()), true,
						ConversationStates.IDLE,
						"ZZzzzz ... ",
						null);
				}
			};
			npc.setEntityClass(classes[i]);
			npc.setPosition(start[i][0], start[i][1]);
			npc.setDescription(descriptions[i]);
			npc.setDirection(Direction.LEFT);
			npc.initHP(100);
			zone.add(npc);
		}
	}
}
