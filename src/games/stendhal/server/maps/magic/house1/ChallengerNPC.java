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
package games.stendhal.server.maps.magic.house1;

import java.util.Arrays;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.Direction;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.engine.ZoneAttributes;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.LevelGreaterThanCondition;
import games.stendhal.server.entity.npc.condition.LevelLessThanCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.IRPZone;

/**
 * The Chaos Sorcerer controlling entry to the adventure island
 *
 * @author kymara
 */
public class ChallengerNPC implements ZoneConfigurator  {

	private static final int MINUTES_IN_DAYS = 24 * 60;
	/** how many creatures will be spawned.*/
	private static final int NUMBER_OF_CREATURES = 5;
	/** lowest level allowed to island.*/
	private static final int MIN_LEVEL = 50;
	/** Cost multiplier for getting to island. */
	private static final int COST_FACTOR = 300;
	/** How long to wait before visiting island again. */
	private static final int DAYS_BEFORE_REPEAT = 3;
	/** The name of the quest slot where we store the time last visited. */
	private static final String QUEST_SLOT = "adventure_island";

	private static final Logger logger = Logger.getLogger(ChallengerNPC.class);

	private static final class ChallengeChatAction implements ChatAction {

		@Override
		public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
			int cost = COST_FACTOR * player.getLevel();
			// check the money but take it later
			if (!player.isEquipped("money", cost)) {
				npc.say("You don't have enough money with you, the fee at your level is " + cost + " money.");
				npc.setCurrentState(ConversationStates.ATTENDING);
				return;
			}
			// now set up the new zone
			final StendhalRPZone challengezone = (StendhalRPZone) SingletonRepository
			.getRPWorld().getRPZone("int_adventure_island");
			String zoneName = player.getName() + "_adventure_island";

			final AdventureIsland zone = new AdventureIsland(zoneName, challengezone, player);

			// add a colour to the zone
			ZoneAttributes attr = new ZoneAttributes(zone);
			attr.setBaseName("int_adventure_island");
			attr.put("color_method", "softlight");
			attr.put("color", Integer.toString(0x550088));
			zone.setAttributes(attr);

			SingletonRepository.getRPWorld().addRPZone(zone);

			// timestamp the quest slot so that we know if it should be repeated
			player.setQuest(QUEST_SLOT, Long.toString(System.currentTimeMillis()));

			// move the player
			player.teleport(zone, 4, 4, Direction.DOWN, player);

			// prepare the message to tell the player cost and what happened
			String message;
			int numCreatures = zone.getCreatures();
			if (zone.getCreatures() < AdventureIsland.NUMBER_OF_CREATURES) {
				// if we didn't manage to spawn NUMBER_OF_CREATURES they get a reduction
				cost =  (int) (cost * ((float) numCreatures / (float) NUMBER_OF_CREATURES));
				message = "Haastaja bellows from below: I could only fit " + numCreatures + " creatures on the island for you. You have therefore been charged less, a fee of only " + cost + " money. Good luck.";
				logger.info("Tried too many times to place creatures in adventure island so less than the required number have been spawned");
			} else {
				message = "Haastaja bellows from below: I took the fee of " + cost + " money. Good luck up there.";
			}
		    // take the money
			player.drop("money", cost);
			// talk to the player
			player.sendPrivateText(message);

			player.notifyWorldAboutChanges();
		}
	}

	@Override
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Haastaja") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			public void createDialog() {
				addGreeting("And so, the hero has come.");
				addQuest("Pay the #fee and you can #fight my trained magical creatures on a magical #island. There will be " + NUMBER_OF_CREATURES
						+ " in all, at a level to challenge you.");
				addHelp("If you are strong enough and will pay the #fee, you can #fight " + NUMBER_OF_CREATURES
						+ " of my animals on a private adventure #island.");
				addJob("I train magical animals for fighting and offer warriors the chance to #battle against them on a magical #island.");
				addOffer("To be transported to an #island to fight against " + NUMBER_OF_CREATURES + " of my trained creatures, chosen for your level, make the #challenge.");
				addReply("island", "I can summon a magical island for you personally. It is sustained by your life force, so if you leave it, you must return quickly or "
						+ "it will dissipate. You should not try to leave and return more than once. To enter, just pay the #fee.");
				addGoodbye("Bye.");
				// fee depends on the level
				// but there is a min level to play
				add(ConversationStates.ANY,
						"fee",
						new AndCondition(new LevelGreaterThanCondition(MIN_LEVEL - 1),
								new TimePassedCondition(QUEST_SLOT, DAYS_BEFORE_REPEAT * MINUTES_IN_DAYS)),
								ConversationStates.QUEST_OFFERED,
								null,
								new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						npc.say("The fee is your current level, multiplied by " + COST_FACTOR + " and payable in cash. At your level of "
								+ player.getLevel() + " the fee is " + COST_FACTOR * player.getLevel() + " money. Do you want to fight?");
					}
				});

				// player meets conditions, first remind them of the dangers and wait for a 'yes'
				add(ConversationStates.ANY,
						Arrays.asList("challenge", "fight", "battle"),
						new AndCondition(new LevelGreaterThanCondition(MIN_LEVEL - 1),
								new TimePassedCondition(QUEST_SLOT, DAYS_BEFORE_REPEAT * MINUTES_IN_DAYS)),
								ConversationStates.QUEST_OFFERED,
								"I accept your challenge. If you can pay the #fee, I will summon an island with " + NUMBER_OF_CREATURES
								+ " dangerous creatures for you to face. So, are you sure you want to enter the adventure island?",
								null);
				// player returns within DAYS_BEFORE_REPEAT days, and his island has expired
				add(ConversationStates.ANY,
						Arrays.asList("challenge", "fight", "battle", "fee"),
						new AndCondition(
								new NotCondition(new TimePassedCondition(QUEST_SLOT, DAYS_BEFORE_REPEAT * MINUTES_IN_DAYS)),
								new NotCondition(new AdventureZoneExistsCondition())
						),
						ConversationStates.ATTENDING,
						null,
						new SayTimeRemainingAction(QUEST_SLOT, DAYS_BEFORE_REPEAT * MINUTES_IN_DAYS, "Your life force will not support the island so soon after you last visited. You will be ready again in"));

				// player returns within DAYS_BEFORE_REPEAT days, if the zone still exists that he was in before, send him straight up.
				add(ConversationStates.ANY,
						Arrays.asList("challenge", "fight", "battle", "fee"),
						new AndCondition(
								new NotCondition(new TimePassedCondition(QUEST_SLOT, DAYS_BEFORE_REPEAT * MINUTES_IN_DAYS)),
								new AdventureZoneExistsCondition()
						),
						ConversationStates.QUESTION_1,
						"The island which I recently summoned for you, remains for you to visit at no extra cost. Do you wish to return to it?",
						null);

				// player below MIN_LEVEL
				add(ConversationStates.ANY,
						Arrays.asList("challenge", "fight", "battle", "fee"),
						new LevelLessThanCondition(MIN_LEVEL),
						ConversationStates.ATTENDING,
						"You are too weak to fight against " + NUMBER_OF_CREATURES  + " at once. Come back when you are at least Level " + MIN_LEVEL + ".",
						null);
				// all conditions are met and player says yes he wants to fight
				add(ConversationStates.QUEST_OFFERED,
						ConversationPhrases.YES_MESSAGES,
						new LevelGreaterThanCondition(MIN_LEVEL - 1),
						ConversationStates.IDLE,
						null,
						new ChallengeChatAction());
				// player was reminded of dangers and he doesn't want to fight
				add(ConversationStates.QUEST_OFFERED,
						ConversationPhrases.NO_MESSAGES,
						null,
						ConversationStates.ATTENDING,
						"Fair enough.",
						null);

				// player wishes to return to an existing adventure island
				add(ConversationStates.QUESTION_1,
						ConversationPhrases.YES_MESSAGES,
						// check again it does exist
						new AdventureZoneExistsCondition(),
						ConversationStates.IDLE,
						null,
						new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						final String zoneName = player.getName() + "_adventure_island";
						final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone(zoneName);
						player.teleport(zone, 4, 4, Direction.DOWN, null);
						player.notifyWorldAboutChanges();
					}
				});

				// player wished to return to an existing adventure island but it's now gone
				add(ConversationStates.QUESTION_1,
						ConversationPhrases.YES_MESSAGES,
						// check again it does exist
						new NotCondition(new AdventureZoneExistsCondition()),
						ConversationStates.ATTENDING,
						"Sorry, but the island vanished between the offer I just made you and you saying 'yes'. You cannot visit it now.",
						null);


				// player declined to return to an existing adventure island
				add(ConversationStates.QUESTION_1,
						ConversationPhrases.NO_MESSAGES,
						null,
						ConversationStates.ATTENDING,
						"Very well.",
						null);
			}};
			npc.setPosition(14, 4);
			npc.setEntityClass("../monsters/chaos/chaos_sorceror");
			npc.setDirection(Direction.DOWN);
			npc.setDescription("You see Haastaja, the Challenger. He is a mighty Chaos Sorcerer.");
			npc.setLevel(600);
			npc.initHP(75);
			zone.add(npc);
	}

	// Not made as an entity.npc.condition. file because the zone name depends on player here.
	private static final class AdventureZoneExistsCondition implements ChatCondition {
		@Override
		public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
			final String zoneName = player.getName() + "_adventure_island";
			final IRPZone.ID zoneid = new IRPZone.ID(zoneName);
			return SingletonRepository.getRPWorld().hasRPZone(zoneid);
		}
	}
}
