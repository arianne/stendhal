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
package games.stendhal.server.maps.athor.holiday_area;

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;

public class WifeNPC implements ZoneConfigurator  {

	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Jane") {
			@Override
			public void say(final String text) {
				// She doesn't move around because she's "lying" on her towel.
				say(text, false);
			}

			@Override
			protected void createPath() {
				// doesn't move
				setPath(null);
			}

			@Override
			public void createDialog() {
				addGreeting("Hi!");
				addGoodbye("Bye!");
			}

		};
		npc.setPosition(28, 44);
		npc.setEntityClass("swimmer6npc");
		npc.setDescription ("Jane is on holiday with her husband, John.");
		npc.setDirection(Direction.DOWN);
		zone.add(npc);
	}
}
