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

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Builds the reaper in hell.
 * Remaining behaviour will be in games.stendhal.server.maps.quests.SolveRiddles
 * @author kymara
 */
public class ReaperNPC implements ZoneConfigurator {
	//
	// ZoneConfigurator
	//

	/**
	 * Configure a zone.
	 *
	 * @param zone
	 *            The zone to be configured.
	 * @param attributes
	 *            Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		SpeakerNPC npc = createNPC("Grim Reaper");
		npc.setPosition(63, 76);
		zone.add(npc);
	}

	static SpeakerNPC createNPC(String name) {
		final SpeakerNPC npc = new SpeakerNPC(name) {

			@Override
			protected void createPath() {
				// doesn't move
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("If you seek to #leave this place you must solve a #riddle");
				addReply("riddle", "I will pose a brain teaser for you, if you wish to #leave. Of course, you can rot in hell, if you so desire ... ");
				// Remaining behaviour is in games.stendhal.server.maps.quests.SolveRiddles
				addJob("I harvest the souls of the living.");
				addHelp("I hold the keys to the gates of hell, should you wish to #leave");
				addOffer("Unless you wish me to take your soul ... ");
				addGoodbye("The old order of things has passed away ... ");
			}
		};
		npc.setEntityClass("grim_reaper_npc");
		npc.initHP(100);
		npc.setDescription("You see the Grim Reaper. His riddles will give you liberty.");
		return npc;
	}
}
