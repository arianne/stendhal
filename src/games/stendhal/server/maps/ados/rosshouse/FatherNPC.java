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
package games.stendhal.server.maps.ados.rosshouse;

import java.util.Arrays;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
/**
 * <p>Creates a normal version of mr ross in the ross house.
 */
public class FatherNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		createDadNPC(zone);
	}

	public void createDadNPC(final StendhalRPZone zone) {

		final SpeakerNPC npc = new SpeakerNPC("Mr Ross") {
			@Override
			protected void createPath() {
				setPath(null);

			}

			@Override
			protected void createDialog() {
				addGreeting("Hi there.");
				addJob("I'm looking after my daughter Susi.");
				addHelp("If you need help finding any buildings in Ados, the guard Julius will give you a map. He is by the city entrance.");
				addOffer("Sorry I do not have anything to offer you, but there are two places to eat in Ados - the tavern and a bar.");
				addQuest("At the end of October we will be visiting the #Mine #Town #Revival #Weeks");
				addGoodbye("Bye, nice to meet you.");

				// Revival Weeks
				add(
					ConversationStates.ATTENDING,
					Arrays.asList("Semos", "Mine", "Town", "Revival", "Weeks"),
					ConversationStates.ATTENDING,
					"During the Revival Weeks at the end of October we celebrate the old and now mostly dead Mine Town north of Semos City.",
					null);
			}

			/*
			 * (non-Javadoc)
			 * @see games.stendhal.server.entity.npc.SpeakerNPC#onGoodbye(games.stendhal.server.entity.RPEntity)
			 */
			@Override
			protected void onGoodbye(RPEntity player) {
				setDirection(Direction.DOWN);
			}

		};

		npc.setOutfit(1, 34, 7, null, 0, null, 27, null, 0);
		npc.setPosition(12, 7);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		npc.setDescription("You see Susi's dad Mr. Ross. He calmed a bit down after the adventures of his daughter.");
		zone.add(npc);
	}

}
