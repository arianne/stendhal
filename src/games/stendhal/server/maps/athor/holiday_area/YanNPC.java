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

public class YanNPC implements ZoneConfigurator  {

	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Yan") {

			@Override
			protected void createPath() {
				// doesn't move
				setPath(null);
			}

			@Override
			public void createDialog() {
				addGreeting("Hello stranger!");
				addQuest("I don't have a task for you.");
				addJob("Sorry, but on holiday I don't want to talk about work.");
				addHelp("A cocktail bar is open! Look for a hut with a straw roof.");
				addGoodbye("See you later!");
			}

		};
		npc.setPosition(62, 72);
		npc.setEntityClass("swimmer4npc");
		npc.setDescription ("You see Yan. He lays at the beach and enjoys some cocktails.");
		npc.setDirection(Direction.DOWN);
		zone.add(npc);
	}
}
