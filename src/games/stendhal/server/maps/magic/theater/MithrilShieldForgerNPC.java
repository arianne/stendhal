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
package games.stendhal.server.maps.magic.theater;

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Configure Baldemar - mithril shield forger.
 *
 * @author kymara
 */
public class MithrilShieldForgerNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildbaldemar(zone);
	}

	private void buildbaldemar(final StendhalRPZone zone) {
		final SpeakerNPC baldemar = new SpeakerNPC("Baldemar") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("Oh, hi. You caught me taking in some of the local culture.");
				addJob("I am a wizard. I have studied long and hard to perfect the art of #mithril forging.");
				addHelp("I can forge a shield for you, the likes of which you have only dreamed.");
				addOffer("I offer you advice. Seek to gather all I require to make a shield for you. You will be eternally grateful.");
				addReply("mithril", "Mithril was once stored by the Mithrilbourgh soldiers in a vault within their keep. Since they moved, I don't know what has become of it.");
				addGoodbye("Farewell. Try some of the licorice Trillium has. It is to die for.");
			} //remaining behaviour defined in quest
		};

		baldemar.setDescription("You see Baldemar, a Mithrilbourgh Wizard well studied in the craft of forging mithril.");
		baldemar.setEntityClass("mithrilforgernpc");
		baldemar.setPosition(4, 6);
		baldemar.initHP(100);
		zone.add(baldemar);
	}
}
