/***************************************************************************
 *                   (C) Copyright 2003-2013 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.nalwor.river;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PinkCrystalNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(final StendhalRPZone zone) {
		
		// Name
		final String RIDDLE_NAME = "pink_crystal";
		
		// Answers to the riddle
		final List<String> answers = Arrays.asList("love", "amor", "amour", "amity", "compassion");
		
		// Message to show when player begins conversation
		final String riddle = "I care for all things. I cannot be apart. If you share me I'm sure I will be reciprocated. What am I?";
		
		// Message when player leaves conversation
		final String goodbyeMessage = "Farewell, return to me when you have found the answer to my riddle.";
		
		// Item given as reward for answering the riddle
		final String rewardItem = "crystal of love";
		
		// Reward for getting riddle right
		final List<ChatAction> rewardAction = new LinkedList<ChatAction>();
		rewardAction.add(new EquipItemAction(rewardItem, 1, true));
		rewardAction.add(new IncreaseKarmaAction(5.0));
		
		// Amount of time, in minutes, player must wait before retrying the riddle
		final int WAIT_TIME = 24 * 60;
		
		// Create the NPC
		final SpeakerNPC crystal = new SpeakerNPC("Pink Crystal") {

			@Override
			protected void createPath() {
				// NPC doesn't move
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting(riddle);
				addGoodbye(goodbyeMessage);     
			}
		};

		crystal.setEntityClass("transparentnpc");
		crystal.setAlternativeImage("crystalpinknpc");
		crystal.setPosition(99, 53);
		crystal.initHP(100);
		crystal.setDescription("You see a crystal.");
		crystal.setResistance(0);
		
		// Offering a riddle
		crystal.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new NotCondition(new PlayerHasItemWithHimCondition(rewardItem)),
				ConversationStates.ATTENDING,
				riddle,
				null);
		
		// Player already has crystal reward
		crystal.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new PlayerHasItemWithHimCondition(rewardItem),
				ConversationStates.IDLE,
				"I have nothing left to offer you.",
				null);
		
		// Player says "bye"
		crystal.add(ConversationStates.ATTENDING,
				ConversationPhrases.GOODBYE_MESSAGES,
				null,
				ConversationStates.IDLE,
				goodbyeMessage,
				null);
		
		// Player gets the riddle right
		crystal.add(ConversationStates.ATTENDING,
				answers,
				null,
				ConversationStates.IDLE,
				"That is correct. Take this crystal as a reward",
				new MultipleActions(rewardAction));
		
		// Player gets the riddle wrong
		crystal.add(ConversationStates.ATTENDING,
				"",
				null,
				ConversationStates.IDLE,
				"I'm sorry, that is incorrect.",
				null);
		
		
		zone.add(crystal);
	}
}
