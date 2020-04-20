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
package games.stendhal.server.maps.deniran.cityinterior.psychicparlor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.common.constants.SoundID;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.OwnedItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.NPCEmoteAction;
import games.stendhal.server.entity.npc.action.PlaySoundAction;
import games.stendhal.server.entity.npc.action.SayTextAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.SoundEvent;


public class PsychicNPC implements ZoneConfigurator {

	private static SpeakerNPC psychic;

	private Creature requestedEnemy = null;
	private Integer currentFee = null;

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		initNPC(zone);
		initReading();
	}

	private void initNPC(final StendhalRPZone zone) {
		psychic = new SpeakerNPC("Lovena") {
			@Override
			protected void onGoodbye(final RPEntity attending) {
				say("Come back soon " + attending.getName() + ".");
				addEvent(new SoundEvent("npc/goodbye_female-01", SoundLayer.CREATURE_NOISE));
			}
		};
		psychic.setOutfit("body=1,head=0,dress=42,eyes=2,hair=19");

		psychic.setPosition(26, 11);
		psychic.setIdleDirection(Direction.LEFT);

		psychic.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				null,
				new MultipleActions(
						new PlaySoundAction("npc/hello_female-01"),
						new SayTextAction("Welcome to my psychic parlor. How can I help you?")));

		psychic.add(ConversationStates.ANY,
				ConversationPhrases.GOODBYE_MESSAGES,
				null,
				ConversationStates.IDLE,
				"I knew you were about to leave. Goodbye.",
				new PlaySoundAction("npc/goodbye_female-01"));

		psychic.addJob("I am a psychic. Would you like a #reading?");
		final String helpReply = "I can #read your combat past, as long as have #recorded the enemies you have defeated.";
		psychic.addHelp(helpReply);
		psychic.addOffer(helpReply);
		psychic.addQuest("The only task I have for you, is the quest for knowledge. I can offer you a #reading for insight into your past.");
		psychic.addReply(
				Arrays.asList("record", "recorded", "recording"),
				"Some adventurers are wise enough to keep a record of the enemies they come accross in a #bestiary.");
		psychic.addReply(
				"bestiary",
				"If you are unfamiliar, seek out an experienced adventurer that can show you how to keep a log of your foes.");

		zone.add(psychic);
	}

	private void initReading() {
		final EntityManager em = SingletonRepository.getEntityManager();

		final ChatCondition isValidCreatureCondition = new ChatCondition() {
			@Override
			public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
				final String requested = sentence.getTrimmedText();
				if (em.isCreature(requested)) {
					requestedEnemy = em.getCreature(requested);
					return true;
				}

				return false;
			}
		};

		final ChatCondition hasKilledCondition = new ChatCondition() {
			@Override
			public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
				if (requestedEnemy == null) {
					return false;
				}

				return player.hasKilled(requestedEnemy.getName());
			}
		};

		final ChatCondition hasFeeCondition = new ChatCondition() {
			@Override
			public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
				if (requestedEnemy == null) {
					return false;
				}

				if (currentFee == null) {
					calculateFee(player);
				}

				return player.isEquipped("money", currentFee);
			}
		};

		final ChatAction sayFeeAction = new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
				if (requestedEnemy == null) {
					npc.say("Hmmm, my divination skills are cloudy. I cannot determine information on this enemy.");
					npc.setCurrentState(ConversationStates.ATTENDING);
					return;
				}

				if (currentFee == null) {
					calculateFee(player);
				}

				npc.say("Information about " + requestedEnemy.getName() + " will cost " + currentFee + ". Would you like to pay?");
			}
		};

		final ChatAction sayKillsAction = new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				if (requestedEnemy == null) {
					psychic.say("Hmmm, my divination skills are cloudy. I cannot determine information on this enemy.");
					psychic.setCurrentState(ConversationStates.ATTENDING);
					return;
				}

				if (currentFee == null) {
					calculateFee(player);
				}

				player.drop("money", currentFee);
				player.addEvent(new SoundEvent(SoundID.COMMERCE, SoundLayer.CREATURE_NOISE));

				final String enemyName = requestedEnemy.getName();
				final String enemyNamePlural = Grammar.pluralCreature(enemyName);
				final int soloKills = player.getSoloKill(enemyName);
				final int sharedKills = player.getSharedKill(enemyName);
				final int totalKills = soloKills + sharedKills;

				final StringBuilder sb = new StringBuilder(player.getName() + ", you have killed a total of " + totalKills + " ");
				if (totalKills == 1) {
					sb.append(enemyName);
				} else {
					sb.append(enemyNamePlural);
				}
				sb.append(". You have killed ");
				if (soloKills == 0) {
					sb.append("none");
				} else {
					sb.append(soloKills);
				}
				sb.append(" alone and ");
				if (sharedKills == 0) {
					sb.append("none");
				} else {
					sb.append(sharedKills);
				}
				sb.append(" in a group.");

				psychic.say("Thank you.");
				new NPCEmoteAction("puts her hand on the bestiary and stares into crystal ball.", false).fire(null, null, raiser);
				psychic.setCurrentState(ConversationStates.BUSY);
				new PlaySoundAction("npc/mm_hmm_female-01").fire(null, null, raiser);
				psychic.setDirection(Direction.LEFT); // face crystal ball

				// create a pause before the NPC replies
				TurnNotifier.get().notifyInSeconds(6, new TurnListener() {
					@Override
					public void onTurnReached(final int currentTurn) {
						/* FIXME: There is a bit of strange behavior if player walks away before receiving
						 *        response as another player can request reading.
						 */
						if (psychic.inConversationRange()) {
							psychic.setCurrentState(ConversationStates.ATTENDING);
						}
						psychic.say(sb.toString());
					}
				});

				// reset requested enemy & fee
				requestedEnemy = null;
				currentFee = null;
			}
		};


		final List<String> readPhrases = Arrays.asList("read", "reading");

		// player is not carrying a bestiary
		psychic.add(ConversationStates.ATTENDING,
				readPhrases,
				new NotCondition(new PlayerHasItemWithHimCondition("bestiary")),
				ConversationStates.ATTENDING,
				"I cannot read into your past unless you have a #record of the enemies you have faced.",
				null);

		// player does not have a usable bestiary
		psychic.add(ConversationStates.ATTENDING,
				readPhrases,
				cannotUseHeldBestiary(),
				ConversationStates.ATTENDING,
				"It appears that bestiary you are carrying belongs to someone else. I suggest you return it to them.",
				null);

		psychic.add(ConversationStates.ATTENDING,
				readPhrases,
				new OrCondition(
						hasNonOwnedBestiary(),
						ownsBestiary()),
				ConversationStates.QUESTION_1,
				"I see you are carrying a bestiary. Which enemy would you like information about?",
				null);

		psychic.add(ConversationStates.QUESTION_1,
				"",
				new NotCondition(isValidCreatureCondition),
				ConversationStates.ATTENDING,
				"I'm sorry, I'm not familiar with that creature. Is there anything else I can help you with?",
				null);

		psychic.add(ConversationStates.QUESTION_1,
				"",
				new AndCondition(
						isValidCreatureCondition,
						new NotCondition(hasKilledCondition)),
				ConversationStates.ATTENDING,
				"It appears you have not encountered that creature.",
				null);

		psychic.add(ConversationStates.QUESTION_1,
				"",
				new AndCondition(
						isValidCreatureCondition,
						hasKilledCondition),
				ConversationStates.QUESTION_2,
				null,
				sayFeeAction);

		psychic.add(ConversationStates.QUESTION_2,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Okay. Is there anything else I can help you with?",
				null);

		psychic.add(ConversationStates.QUESTION_2,
				ConversationPhrases.YES_MESSAGES,
				new NotCondition(hasFeeCondition),
				ConversationStates.ATTENDING,
				"I'm sorry, you don't have enough money to pay the fee.",
				null);

		psychic.add(ConversationStates.QUESTION_2,
				ConversationPhrases.YES_MESSAGES,
				hasFeeCondition,
				null,
				null,
				sayKillsAction);
	}

	/**
	 * Calculates the fee for receiving a reading based on player & requested
	 * creature levels.
	 *
	 * @param player
	 * 		The player requesting the reading.
	 */
	private void calculateFee(final Player player) {
		currentFee = player.getLevel() * 10 + requestedEnemy.getLevel() * 15;
	}

	/**
	 * Checks if player is carrying a non-owned bestiary.
	 */
	private ChatCondition hasNonOwnedBestiary() {
		return new ChatCondition() {

			@Override
			public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
				for (final Item bestiary: player.getAllEquipped("bestiary")) {
					if (!((OwnedItem) bestiary).hasOwner()) {
						return true;
					}
				}

				return false;
			}
		};
	}

	/**
	 * Checks if player is carrying a bestiary that he/she is the owner of.
	 */
	private ChatCondition ownsBestiary() {
		return new ChatCondition() {

			@Override
			public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
				for (final Item bestiary: player.getAllEquipped("bestiary")) {
					if (((OwnedItem) bestiary).isOwner(player.getName())) {
						return true;
					}
				}

				return false;
			}
		};
	}

	/**
	 * Checks if the player is only carrying bestiary that he/she cannot use.
	 */
	private ChatCondition cannotUseHeldBestiary() {
		return new AndCondition(
				new PlayerHasItemWithHimCondition("bestiary"),
				new NotCondition(new OrCondition(
						hasNonOwnedBestiary(),
						ownsBestiary())));
	}
}
