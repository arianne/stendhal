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
package games.stendhal.server.maps.semos.city;

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * An old man (original name: Monogenes) who stands around and gives directions
 * to newcomers. He's the brother of RetireeNPC (original name: Diogenes).
 *
 * @see games.stendhal.server.maps.quests.MeetMonogenes
 * @see games.stendhal.server.maps.quests.HatForMonogenes
 */
public class GreeterNPC implements ZoneConfigurator {

	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Monogenes") {
			@Override
			public void createDialog() {
				addJob("I'm Diogenes' older brother and I don't actually remember what I used to do... I'm retired now.");
				addOffer("I give directions to #buildings in Semos, to help newcomers settle in. When I'm in a bad mood I sometimes give misleading directions to amuse myself... hee hee hee! Of course, sometimes I get my wrong directions wrong and they end up being right after all! Ha ha!");
				// All further behaviour is defined in quest classes.
			}

			@Override
			protected void onGoodbye(RPEntity player) {
				setDirection(Direction.LEFT);
			}



		};
		npc.setPosition(27, 43);
		npc.setEntityClass("oldmannpc");
		npc.setDescription("You see Monogenes. He looks really ancient, perhaps he knows a thing or two...");
		npc.setDirection(Direction.LEFT);
		zone.add(npc);
	}
}
