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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Builds the NPC who provides ornament that whisks the player away to the northpole.
 * Other behaviour defined in maps/quests/Ornament.java
 *
 * @author kymara modified by tigertoes
 */
public class OrnamentNPC implements ZoneConfigurator {
	//
	// ZoneConfigurator
	//

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

	//
	// IL0_GreeterNPC
	//

	private void buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC ballNPC = new SpeakerNPC("XXXXXX") {

			npc.setPosition(8, 36);
			npc.setEntityClass("northpoleelfskater1npc");
			npc.setDescription("You see XXXXX who looks really jolly and happy. You might be able to get an ornament from them.");
			npc.setDirection(Direction.RIGHT);
			zone.add(npc);
			

			@Override
			protected void createDialog[]; {
				addJob("I provide you with an ornament which will magically swoosh you away to the northpole.");
				addHelp("I just supply the ornament to start your quest at the northpole.");
				addQuest("I just have the ornament which you will use to go to the northpole. Then you will have a chance for a few quests there.");
				addOffer("Nothing to sell. Just an ornament to give");
				addGoodbye("Bye.");
			}
		

		ballNPC.setEntityClass("northpoleelfskater1npc");
		ballNPC.setPosition(8, 36);
		ballNPC.initHP(100);
		ballNPC.setDescription("You see XXXXXX. Seems like he is mentally in another world...");
		zone.add(ballNPC);
	}
}
