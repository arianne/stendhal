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
package games.stendhal.server.maps.semos.tavern;

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

public class StichardRallmanNPC implements ZoneConfigurator {
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
		final SpeakerNPC stallman = new SpeakerNPC("Stichard Rallman") {

			@Override
			public void say(final String text) {
				setDirection(Direction.DOWN);
				super.say(text, false);
			}

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("Welcome to Stendhal! True #free software!");
				addJob("I am the #free software evangelizer!");
				addHelp("Help #Stendhal to be even better. Donate your time, tell your friends to play, create maps.");
				addReply("free",
					"''Free software'' is a matter of liberty, not price. To understand the concept, you should think of ''free'' as in ''free speech,'' not as in ''free beer''.");
				addReply("stendhal",
					"Stendhal is completely #free software (client, server, graphics, everything) under #GNU #GPL. You can run, copy, distribute, study, change and improve this software.");
				addReply("gnu", "https://www.gnu.org/");
				addReply("gpl", "https://www.gnu.org/licenses/gpl.html");

				addGoodbye();
			}
		};

		stallman.setEntityClass("richardstallmannpc");
		stallman.setDescription("Stichard Rallman knows everything about free software and licences.");
		stallman.setPosition(24, 19);
		stallman.setDirection(Direction.DOWN);
		stallman.initHP(100);
		zone.add(stallman);
	}
}
