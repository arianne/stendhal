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
package games.stendhal.server.maps.kalavan.castle;

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Builds a mad scientist NPC who takes your silk glands makes thread, then gives them to another NPC.
 *
 * @author kymara with modifications by tigertoes
 */
public class MadScientist1NPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Vincento Price") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			    protected void createDialog() {
				addHelp("Ha ha ha ha!");
				addOffer("I am only saying this because I can see you need it, but I will #make #40 spools of #silk thread for you.");
				addQuest("Ha! You need 40 spools of #silk thread, I see! I can #make it. If I feel like it ...");
				addJob("What does it look like?");
				addGoodbye("Ta ta!");
				// remaining behaviour defined in maps.quests.MithrilCloak
	 	     }

		};

		npc.setDescription("You see someone that is somewhat strange. Perhaps you shouldn't bother him?");
		npc.setEntityClass("madscientistnpc");
		npc.setPosition(18, 84);
		npc.initHP(100);
		zone.add(npc);
	}
}
