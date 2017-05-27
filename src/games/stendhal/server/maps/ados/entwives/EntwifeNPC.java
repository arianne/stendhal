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
package games.stendhal.server.maps.ados.entwives;

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * entwife located in 0_ados_mountain_n2_w2.
 */
public class EntwifeNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildentwife(zone);
	}

	private void buildentwife(final StendhalRPZone zone) {
		final SpeakerNPC entwife = new SpeakerNPC("Tendertwig") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("Welcome, fair wanderer.");
				addJob("I guard all I can see. It is a peaceful life.");
				addHelp("There is lots to see and harvest here. Just wander around.");
				addOffer("I have nothing to offer but fresh air and sunshine.");
				addGoodbye("May your travels be pleasant, my fine friend.");
				addQuest("There is something I wish. But I have no time at present to discuss it. Please come back again later.");
			}
		};

		entwife.setEntityClass("transparentnpc");
		entwife.setAlternativeImage("tendertwig");
		entwife.setPosition(25, 35);
		entwife.initHP(100);
		entwife.setDescription("You see an old and wise entwife. She is called Tendertwig and guards the area around.");
		zone.add(entwife);
	}
}
