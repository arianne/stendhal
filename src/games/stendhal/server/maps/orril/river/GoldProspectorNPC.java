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
package games.stendhal.server.maps.orril.river;

import java.util.Arrays;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * Configure Orril River South Campfire (Outside/Level 0).
 */
public class GoldProspectorNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildGoldSourceArea(zone);
	}

	private void buildGoldSourceArea(final StendhalRPZone zone) {

		final SpeakerNPC bill = new SpeakerNPC("Bill") {

			@Override
			protected void createDialog() {
				addGreeting("Howdy partner!");
				addJob("Once I was a very successful gold prospector, but with the age came the backache, so I'm a pensioner now. However I can still give advice to rookies!");
				add(ConversationStates.ATTENDING, ConversationPhrases.HELP_MESSAGES, null,
				        ConversationStates.INFORMATION_1,
				        "I can tell you the secrets of prospecting for gold, if you are interested. Are you?", null);

				add(
				        ConversationStates.INFORMATION_1,
				        ConversationPhrases.YES_MESSAGES,
				        null,
				        ConversationStates.ATTENDING,
				        "First you need a #gold #pan to separate the gold from the mud. Then you have to search for the right spot in the water. The flat water in this area is very rich of gold resources. Just right-click and select Prospect on the light blue water where you see gold glittering. But don't give up too early, you need a lot of luck and patience.",
				        null);

				add(ConversationStates.INFORMATION_1, ConversationPhrases.NO_MESSAGES, null,
				        ConversationStates.ATTENDING,
				        "Oh, it doesn't matter, the less people know about the prospect secrets the better!", null);

				add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES, null,
				        ConversationStates.ATTENDING,
				        "I don't have a task for you, I'm just here to help new prospectors.", null);

				add(ConversationStates.ATTENDING, Arrays.asList("gold", "pan", "gold pan"), null,
				        ConversationStates.ATTENDING,
				        "I don't have a gold pan, but maybe you could ask a blacksmith to sell you one.", null);

				addGoodbye("Seeya, get yer spurs on!");
			}

			@Override
			protected void onGoodbye(RPEntity player) {
				setDirection(Direction.DOWN);
			}

		};

		bill.setEntityClass("oldcowboynpc");
		bill.setPosition(105, 58);
		bill.setDirection(Direction.DOWN);
		bill.initHP(100);
		bill.setDescription("Bill is a retired gold prospector. Now he is waiting for followers in this business.");
		zone.add(bill);
	}
}
