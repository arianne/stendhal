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
package games.stendhal.server.maps.semos.road;

import java.util.Arrays;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.LevelGreaterThanCondition;
import games.stendhal.server.entity.npc.condition.LevelLessThanCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasShieldEquippedCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.player.Player;

public class BoyGuardianNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param zone
	 *            The zone to be configured.
	 * @param attributes
	 *            Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		buildMineArea(zone);
	}

	private void buildMineArea(final StendhalRPZone zone) {
			final SpeakerNPC npc = new SpeakerNPC("Will") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {

				String greetingBasis = "Hey you! Take care, you are leaving the city now! ";

				// When the players level is below 15 AND (he has a shield equipped OR he completed the "meet_hayunn" quest)
				add(
						ConversationStates.IDLE,
						ConversationPhrases.GREETING_MESSAGES,
						new AndCondition(
								new LevelLessThanCondition(15),
								new OrCondition(
										new PlayerHasShieldEquippedCondition(),
										new QuestCompletedCondition("meet_hayunn")
								)
						),
						ConversationStates.ATTENDING,
						greetingBasis + "Look out for animals who might attack you and other enemies who walk around. Take some food or drinks with you! ",
						null);
				// When the players level is below 15 AND he has NO shield AND he has NOT completed the "meet_hayunn" quest
				add(
						ConversationStates.IDLE,
						ConversationPhrases.GREETING_MESSAGES,
						new AndCondition(
								new LevelLessThanCondition(15),
								new NotCondition(new PlayerHasShieldEquippedCondition()),
								new NotCondition(new QuestCompletedCondition("meet_hayunn"))
						),
						ConversationStates.ATTENDING,
						greetingBasis + "Eaak! You don't even have a shield. You better go right back and talk to Hayunn in the old guard house in Semos Village before you get into danger out here in the wild.",
						null);
				// When the player is above level 15
				add(
						ConversationStates.IDLE,
						ConversationPhrases.GREETING_MESSAGES,
						new LevelGreaterThanCondition(15),
						ConversationStates.ATTENDING,
						greetingBasis + "Oh, I see you are mighty and brave already! Have fun :)",
						null);

				addJob("My job is to watch out for baaad creatures! My parents gave me that special #duty!");
				addReply("duty", "Yes, a really special and important one!");
				addHelp("My daddy always tells me to #sneak around in forests which aren't familiar to me... And he said that I should always take something to #eat #and #drink with me, to be on the safe side!");
				addReply("sneak", "Yes, if you want to be a warrior like I want to be, you have to move quietly!");
				addReply(Arrays.asList("eat","drink","eat and drink"), "Leander, the Semos baker, makes some really tasty sandwiches, mom always buys them there and his bread is yummi, too! I'm not sure about drinks, maybe you can ask #Carmen or #Margaret?");
				addReply("Carmen", "Carmen is a famous healer in Semos city. Maybe you spotted her on your way from the village into the city :)");
				addReply("Margaret", "Margaret works in the tavern but I am not allowed to go there without my parents...");
				addQuest("I'm on a mission :) I watch out for bad bad guys and warn people, to #help them! But I don't need anything from you...");
				addGoodbye("Shhhhh, don't talk too loud! Bye and take care!");
			}

			@Override
			protected void onGoodbye(RPEntity player) {
				setDirection(Direction.DOWN);
			}
		};

		npc.addInitChatMessage(null, new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				if (!player.hasQuest("WillFirstChat")) {
					player.setQuest("WillFirstChat", "done");
					((SpeakerNPC) raiser.getEntity()).listenTo(player, "hi");
				}
			}
		});

		npc.setEntityClass("boyguardnpc");
		npc.setDescription("You see Will. He wants to be a professional city guard when he grows up.");
		npc.setPosition(6, 43);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		npc.setPerceptionRange(4);
		zone.add(npc);
	}
}
