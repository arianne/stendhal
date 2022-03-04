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
package games.stendhal.server.maps.ados.tunnel;

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * In recreation room of Blordrough's habitat in -1_ados_outside_w.
 */
public class CrulaminNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildCrulamin(zone);
	}

	private void buildCrulamin(final StendhalRPZone zone) {
		final SpeakerNPC Crulamin = new SpeakerNPC("Crulamin") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			public void createDialog() {
				addGreeting("I am busy here.  Please leave me alone for now.  Maybe you come back and visit me another day?");
				addOffer("I am just a person who wants to play chess.");
				addJob("I am studying very hard to become a Chess Expert.");
				addHelp("Maybe you can show me what you are capable of some day.  Until that day, please, just go away.");
				addGoodbye("Hmmmm.  What if i move this here, and then that there...");
				// all other behaviour is defined in the quest.
			}
		};

		Crulamin.setDescription("You see Crulamin, a warrior who has given up everything but playing chess.  His hopes are to become 'The Best' ");
		Crulamin.setEntityClass("../monsters/human/armored_leader");
		Crulamin.setPosition(73,93);
		Crulamin.initHP(100);
		zone.add(Crulamin);
	}
}
