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
package games.stendhal.server.maps.nalwor.assassinhq;

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Inside assassin headquarters classroom area.
 */
public class LilJohnnnnyNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildLilJohnnnny(zone);
	}

	private void buildLilJohnnnny(final StendhalRPZone zone) {
		final SpeakerNPC liljohnnnny = new SpeakerNPC("lil johnnnny") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("I didn't mean to hurt #him.");
				addJob("Job? Nah! Can't think of that now.");
				addHelp("He said my mommy wears army #boots.");
				addOffer("I'm gonna make him sorry he ever said that!");
				addGoodbye("well, if you really have to leave...");
				addQuest("Nah, I got nothin' for you right now. Maybe later.");
				addReply("him","He was making #fun of me.");
				addReply("fun","He said my mommy wears army #boots.");
				addReply("boots","I'm gonna make him sorry he'd ever said that!");
			}
		};

		liljohnnnny.setDescription("You see lil johnnnny, plotting his next conquest.");
		liljohnnnny.setEntityClass("liljohnnnnynpc");
		liljohnnnny.setPosition(23, 2);
		liljohnnnny.initHP(100);
		zone.add(liljohnnnny);
	}
}
