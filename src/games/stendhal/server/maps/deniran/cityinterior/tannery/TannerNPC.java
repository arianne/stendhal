/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.deniran.cityinterior.tannery;

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.common.constants.SkinColor;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rp.DaylightPhase;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.DaylightCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;

public class TannerNPC implements ZoneConfigurator {

	@Override
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		final SpeakerNPC tanner = new SpeakerNPC("Skinner Rawhide") {

			/**
			 * Force NPC to face north after speaking with players.
			 */
			@Override
			public void setAttending(final RPEntity rpentity) {
				super.setAttending(rpentity);
				if (rpentity == null) {
					setDirection(Direction.UP);
				}
			}
		};

		// dialogue
		tanner.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new NotCondition(new DaylightCondition(DaylightPhase.NIGHT)),
				ConversationStates.IDLE,
				"Welcome to Deniran's tannery.",
				null);

		tanner.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new DaylightCondition(DaylightPhase.NIGHT),
				ConversationStates.IDLE,
				"It's late. I need to get to bed.",
				null);

		tanner.setPosition(10, 8);
		tanner.setDirection(Direction.UP);
		tanner.setOutfit("body=0,head=0,dress=61,hair=25");
		tanner.setOutfitColor("skin", SkinColor.DARK);

		zone.add(tanner);
	}

}
