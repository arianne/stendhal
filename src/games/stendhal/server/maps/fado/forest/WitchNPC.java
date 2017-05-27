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
package games.stendhal.server.maps.fado.forest;

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Creates Imorgen, a young witch who lives in Fado Forest.
 *
 * @author Vanessa Julius
 */
public class WitchNPC implements ZoneConfigurator {

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
		final SpeakerNPC npc = new SpeakerNPC("Imorgen") {

			@Override
			protected void createPath() {
				setPath(null);

			}

			@Override
			protected void onGoodbye(RPEntity player) {
				setDirection(Direction.DOWN);
			}

			@Override
			protected void createDialog() {
				addGreeting("Hello wanderer!");
				addJob("I'm a witch and practice each day, I hope to become a powerful one soon.");
				addQuest("My grandma inside is quite concerned... She is ill and afraid and I need to help her. I just need to figure out #how...");
				addReply("how", "The question really is what may help... Maybe I'll ask her later about it, I first have to finish my new #recipe...");
				addReply("recipe", "Oh I doubt that you can eat or drink what I prepare at the moment... But I know two really nice women who can make some tasty #soup for you.");
				addReply("soup", "Florence Boullabaisse and Mother Helena are awesome soup cooks. You'll find them on the Ados market and in the Fado tavern.");
				addHelp("Aldrin sells some honey, you would love to eat it with bread.");
				addOffer("I'm sorry but I can't sell you anything.");
				addGoodbye("Bye bye and take care around here!");
			}
		};

		npc.setEntityClass("youngwitchnpc");
		npc.setPosition(59, 29);
		npc.initHP(100);
		npc.setDescription("You see Imorgen. She is a young witch whose mystical aura can be felt over a long distance.");
		zone.add(npc);
	}
}
