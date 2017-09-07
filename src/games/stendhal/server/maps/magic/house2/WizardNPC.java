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
package games.stendhal.server.maps.magic.house2;

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Builds a wizard npc, an expert in textiles.
 *
 * @author kymara
 */
public class WizardNPC implements ZoneConfigurator {

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
		final SpeakerNPC npc = new SpeakerNPC("Whiggins") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			    protected void createDialog() {
				addGreeting("Welcome, warmly");
				addHelp("If you need scrolls, Erodel Bmud sells a wide range.");
				addOffer("I don't sell anything here.");
				addJob("I keep this house nice and watch the fairies.");
				addGoodbye("Till next time.");
				// remaining behaviour defined in maps.quests.MithrilCloak
	 	     }

		};

		npc.setDescription("You see Whiggins, looking tranquil and happy.");
		npc.setEntityClass("mithrilforgernpc");
		npc.setPosition(14, 14);
		npc.initHP(100);
		zone.add(npc);
	}
}
