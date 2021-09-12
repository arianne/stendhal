/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.kalavan.citygardens;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.NPCSetDirection;

/**
 * A fisherman.
 *
 * @author kymara
 */
public class FishermanNPC implements ZoneConfigurator {
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
		final SpeakerNPC npc = new SpeakerNPC("Baelin") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				add(ConversationStates.IDLE,
					ConversationPhrases.GREETING_MESSAGES,
					ConversationStates.IDLE,
					"Nice day for fishing, ain't it? Hua hah!",
					new NPCSetDirection(Direction.RIGHT));
			}
		};
		npc.setDescription("You see a young man fishing.");
		npc.setDirection(Direction.RIGHT);
		npc.setPosition(116, 47);
		Outfit outfit = new Outfit("body=0,dress=13,head=0,mouth=0,eyes=0,mask=0,hair=6,hat=11,detail=0");
		npc.setOutfit(outfit);
		npc.setOutfitColor("dress", 0xffe1b01e);
		npc.setOutfitColor("hat", 0x00ffe01e);

		npc.initHP(100);
		zone.add(npc);
	}
}
