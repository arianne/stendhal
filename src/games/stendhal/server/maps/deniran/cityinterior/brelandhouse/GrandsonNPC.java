/***************************************************************************
 *                    Copyright © 2003-2022 - Arianne                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.deniran.cityinterior.brelandhouse;

import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;


public class GrandsonNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		/* Note:
		 *   Niall is not be available for interaction until
		 *   after healing myling in An Old Man's Wish quest
		 *   so there is no need to create dialogue here.
		 */

		final SpeakerNPC niall = new SpeakerNPC("Niall Breland");
		niall.setOutfit("body=984,head=994,eyes=998,hair=998,dress=964");

		niall.addGoodbye();

		niall.setPosition(9, 7);

		zone.add(niall);
	}
}
