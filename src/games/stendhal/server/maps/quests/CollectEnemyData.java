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
package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.MathHelper;
import games.stendhal.common.Rand;
import games.stendhal.common.constants.SkinColor;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
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
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.TeleporterBehaviour;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.SoundEvent;
import marauroa.common.Pair;


/**
 * QUEST: Collect Enemy Data (collect_enemy_data)
 *
 * PARTICIPANTS:
 * <ul>
 *   <li>Rengard, a wandering adventurer.</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 *   <li>Find Rengard wandering around Faimouni.</li>
 *   <li>He will ask for information on 3 different creatures.</li>
 *   <li>Kill each creature & bring him the requested information.</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 *   <li>Can buy bestiary from Rengard.</li>
 *   <li>karma</li>
 *   <ul>
 *     <li>35.0 for starting quest.</li>
 *     <li>200.0 for completing quest.</li>
 *   </ul>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 *   <li>Not repeatable.</li>
 * </ul>
 */
public class CollectEnemyData extends AbstractQuest {

	private static final Logger logger = Logger.getLogger(CollectEnemyData.class);

	private static final String QUEST_NAME = "Collect Enemy Data";
	private static final String QUEST_SLOT = QUEST_NAME.toLowerCase().replace(" ", "_");

	private SpeakerNPC npc;

	private TeleporterBehaviour teleporterBehaviour;

	public static final String[] zonesWhitelist = {
			"0_semos_mountain_n2_w", "0_ados_mountain_n2_w2", "0_deniran_forest_n2_e",
			"-7_deniran_atlantis_mtn_n_e2", "0_orril_mountain_n_w2"
	};

	private static final int bestiaryPrice = 500000;

	private static final Map<String, Pair<String, String>> questionOptions = new HashMap<String, Pair<String, String>>() {{
		put("level", new Pair<String, String>("What is the level of", null));
		put("hp", new Pair<String, String>("How much HP does", "have"));
		put("def", new Pair<String, String>("What is the defense level of", null));
		put("atk", new Pair<String, String>("What is the attack level of", null));
	}};

	// FIXME: QuestActiveCondition doesn't work for this quest because of the overridden isCompleted method
	private final ChatCondition questActiveCondition = new ChatCondition() {
		@Override
		public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
			if (player.getQuest(QUEST_SLOT) != null) {
				return !isCompleted(player);
			}

			return false;
		}
	};

	// FIXME: QuestCompletedCondition doesn't work for this quest because of the overridden isCompleted method
	private final ChatCondition questCompletedCondition = new ChatCondition() {
		@Override
		public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
			return isCompleted(player);
		}
	};


	private void initNPC() {
		npc = new SpeakerNPC("Rengard");
		npc.setOutfit("body=0,head=0,eyes=16,hair=39,dress=999,mask=4,hat=10");
		npc.setOutfitColor("skin", SkinColor.DARK);
		npc.setOutfitColor("hat", 0xff0000);
		npc.setDescription("You see a seasoned adventurer with a smile on his face and a sparkle in his eye.");

		npc.addGreeting("Hello fellow adventurer.");
		npc.addGoodbye("May you have luck on your future adventures.");
		npc.addJob("Job? Hah! I am a free spirit. I travel the world, seeking to increase my own knowledge and experience.");

		final List<String> helpTriggers = new ArrayList<>();
		helpTriggers.addAll(ConversationPhrases.HELP_MESSAGES);
		helpTriggers.addAll(ConversationPhrases.OFFER_MESSAGES);

		npc.add(ConversationStates.ATTENDING,
				helpTriggers,
				//new NotCondition(new QuestCompletedCondition(QUEST_SLOT)),
				new NotCondition(new QuestStartedCondition(QUEST_SLOT)),
				ConversationStates.ATTENDING,
				"If you seek to expand your knowledge as I do, I have a little #task I could use some help with.",
				null);

		npc.add(ConversationStates.ATTENDING,
				helpTriggers,
				questActiveCondition,
				ConversationStates.ATTENDING,
				"Finish helping me gather some enemy info and I will sell you something very useful.",
				null);


		teleporterBehaviour = new TeleporterBehaviour(npc, Arrays.asList(zonesWhitelist), "", "♫♫♫") {
			@Override
			protected void doRegularBehaviour() {
				super.doRegularBehaviour();

				npc.addEvent(new SoundEvent("npc/whistling-01", SoundLayer.CREATURE_NOISE));
			}
		};

		teleporterBehaviour.setTarryDuration(MathHelper.SECONDS_IN_ONE_MINUTE * 15); // spends 15 minutes on a map
		teleporterBehaviour.setExitsConversation(false);
		teleporterBehaviour.setTeleportWarning("I must leave soon.");

		// initialize Rengard with a zone so JUnit test does not fail
		SingletonRepository.getRPWorld().getZone(zonesWhitelist[0]).add(npc);
	}

	private void initQuest() {

		final ChatCondition hasKilledCreatureCondition = new ChatCondition() {
			@Override
			public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
				final String[] state = player.getQuest(QUEST_SLOT).split(";");
				if (state == null) {
					return false;
				}

				final int currentStep = getCurrentStep(player);
				final String creature = getEnemyForStep(player, currentStep);
				final Integer recordedKills = getRecordedKillsForStep(player, currentStep);

				if (creature == null || recordedKills == null) {
					onError(null);
					return false;
				}

				return (player.getSoloKill(creature) + player.getSharedKill(creature)) > recordedKills;
			}
		};

		final ChatCondition answeredCorrectlyCondition = new ChatCondition() {
			@Override
			public boolean fire(final Player player, final Sentence sentence, final Entity raiser) {
				final int currentStep = getCurrentStep(player);
				final String fromQuestSlot = getEnemyForStep(player, currentStep);
				final Creature creature = SingletonRepository.getEntityManager().getCreature(fromQuestSlot);

				if (creature == null) {
					logger.error("Invalid creature name in quest slot: " + fromQuestSlot);
					return false;
				}

				final String answer = sentence.getTrimmedText();
				String correctAnswer = getAnswerForStep(player, creature, currentStep);

				// should not happen
				if (correctAnswer == null) {
					onError(null);
					return false;
				}

				return answer.equals(correctAnswer);
			}
		};

		final ChatCondition isFinalStepCondition = new ChatCondition() {
			@Override
			public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
				return getCurrentStep(player) > 1;
			}
		};

		final ChatAction setQuestAction = new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				final String selected = selectCreature(player);
				final int killCount = player.getSoloKill(selected) + player.getSharedKill(selected);

				if (player.getQuest(QUEST_SLOT) == null) {
					setQuestSlot(player, 0, selected, killCount);
					player.addKarma(35.0);

					npc.say("Great! I have compiled much info on creatures I have come across. But I am still missing three. First, I need some info on "
							+ Grammar.singular(selected) + ".");
				} else {
					setQuestSlot(player, getCurrentStep(player), selected, killCount);

					npc.say("Thank you! I am going to write this down. Now I need information on " + Grammar.singular(selected) + ".");
				}
			}
		};

		final ChatAction completeStepAction = new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				final int currentStep = getCurrentStep(player);
				String[] stepState = player.getQuest(QUEST_SLOT, currentStep).split(",");
				stepState[1] = "done";
				player.setQuest(QUEST_SLOT, currentStep, String.join(",", stepState));
			}
		};


		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED,
				"Would you like to help me collect data on creatures found around the world of Faimouni?",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(
						new QuestStartedCondition(QUEST_SLOT),
						//new NotCondition(new QuestCompletedCondition(QUEST_SLOT)),
						new NotCondition(questCompletedCondition)),
				ConversationStates.ATTENDING,
				"You have already agreed to help me collect creature data.",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				//new QuestCompletedCondition(QUEST_SLOT),
				questCompletedCondition,
				ConversationStates.ATTENDING,
				"Thank you for your help compiling creature information.",
				null);

		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Okay, have it your way.",
				null);

		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.IDLE,
				null,
				setQuestAction);

		// player has to returned to give info
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				//new QuestActiveCondition(QUEST_SLOT), // FIXME: doesn't work for this quest because of the overridden isCompleted method
				questActiveCondition,
				ConversationStates.QUESTION_1,
				"Have you brought information about the creature I requested?",
				null);

		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Okay. What else can I help you with?",
				null);

		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				new NotCondition(hasKilledCreatureCondition),
				ConversationStates.ATTENDING,
				"Don't lie to me. You haven't even killed one since we spoke.",
				null);

		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				hasKilledCreatureCondition,
				ConversationStates.QUESTION_2,
				null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						final String question = getQuestionForStep(player, getCurrentStep(player));
						if (question == null) {
							onError("Could not retrieve question for player: " + player.getName());
							return;
						}

						npc.say(question);
					}
				});

		npc.add(ConversationStates.QUESTION_2,
				"",
				new NotCondition(answeredCorrectlyCondition),
				ConversationStates.IDLE,
				"Hmmm, that doesn't seem accurate. Perhaps you could double check.",
				null);

		npc.add(ConversationStates.QUESTION_2,
				"",
				new AndCondition(
						answeredCorrectlyCondition,
						new NotCondition(isFinalStepCondition)),
				ConversationStates.IDLE,
				null,
				new MultipleActions(
						completeStepAction,
						setQuestAction));

		npc.add(ConversationStates.QUESTION_2,
				"",
				new AndCondition(
						answeredCorrectlyCondition,
						isFinalStepCondition),
				ConversationStates.ATTENDING,
				null,
				new MultipleActions(
						completeStepAction,
						new ChatAction() {
							@Override
							public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
								npc.say("Thanks so much for you help. Now I have all the information I need to complete my #bestiary."
										+ " If you would like one of your own, I can sell you one.");
								player.addKarma(200.0);
							}}));
	}

	private void initShop() {
		final Map<String, Integer> prices = new LinkedHashMap<String, Integer>() {{
			put("bestiary", bestiaryPrice);
		}};

		final SellerBehaviour behaviour = new SellerBehaviour(prices) {
			@Override
			public ChatCondition getTransactionCondition() {
				//return new QuestCompletedCondition(QUEST_SLOT);
				return questCompletedCondition;
			}

			@Override
			public ChatAction getRejectedTransactionAction() {
				return new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						final String response;
						if (questActiveCondition.fire(player, null, null)) {
							response = "I still need you to help me gather information on " + Grammar.a_noun(getEnemyForStep(player, getCurrentStep(player))) + ".";
						} else {
							response = "I need your help with a #task first.";
						}

						npc.say(response);
					}
				};
			}

			@Override
			public boolean transactAgreedDeal(ItemParserResult res, final EventRaiser seller, final Player player) {
				if (super.transactAgreedDeal(res, seller, player)) {
					seller.say("I have written your name down in it, just in case you lose it. Remember, the creatures you track in this"
							+ " bestiary are only for you. So it will not work for anyone else. Anyone who wants to track kills should"
							+ " buy their own.");

					return true;
				}

				return false;
			}

			@Override
			public Item getAskedItem(final String askedItem, final Player player) {
				final Item item = super.getAskedItem(askedItem, player);

				if (item != null && player != null) {
					// set owner to prevent others from using it
					((OwnedItem) item).setOwner(player.getName());
					return item;
				}

				if (player == null) {
					logger.error("Player is null, cannot set owner in bestiary");
				}
				if (item == null) {
					logger.error("Could not create bestiary item");
				}

				return null; // don't give a bestiary without owner
			}
		};
		new SellerAdder().addSeller(npc, behaviour, false);


		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.OFFER_MESSAGES,
				//new QuestCompletedCondition(QUEST_SLOT),
				questCompletedCondition,
				ConversationStates.ATTENDING,
				"I can sell you a #bestiary.",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.HELP_MESSAGES,
				//new QuestCompletedCondition(QUEST_SLOT),
				questCompletedCondition,
				ConversationStates.ATTENDING,
				"If you own a #bestiary, you may be able to find a psychic that can give you more insight into the creatures you have encountered.",
				null);

		npc.add(ConversationStates.ATTENDING,
				"bestiary",
				//new QuestCompletedCondition(QUEST_SLOT),
				questCompletedCondition,
				ConversationStates.ATTENDING,
				"A bestiary allows you to keep track of the enemies you have defeated.",
				null);
	}


	/**
	 * Fills in information in the quest slot for a step.
	 *
	 * @param player
	 * 		Player doing the quest.
	 * @param slotIndex
	 * 		The quest step in question.
	 * @param enemyName
	 * 		The enemy player is tasked to kill.
	 * @param killCount
	 * 		Recorded number of kills retrieved from player.
	 * @param questionKey
	 * 		Key to identify which question should be asked.
	 */
	private void setQuestSlot(final Player player, final int slotIndex, final String enemyName, final int killCount) {
		String slotString = enemyName + "," + killCount + "," + selectQuestionKey();

		if (slotIndex == 0) {
			slotString += ";null;null";
		}

		player.setQuest(QUEST_SLOT, slotIndex, slotString);
	}

	/**
	 * Randomly chooses the identifier for the question that should be asked.
	 *
	 * @return
	 * 		Key to identify which question should be be asked.
	 */
	private String selectQuestionKey() {
		return questionOptions.keySet().toArray(new String[] {})[Rand.randUniform(0, questionOptions.size() - 1)];
	}

	/**
	 * Randomly chooses an enemy based on player level.
	 *
	 * @param player
	 * 		Player doing the quest.
	 * @return
	 * 		Creature name.
	 */
	private String selectCreature(final Player player) {
		int threshold = 10;
		final int playerLevel = player.getLevel();

		final Collection<Creature> allCreatures = SingletonRepository.getEntityManager().getCreatures();
		// should not happen
		if (allCreatures.size() < 3) {
			logger.error("Not enough registered creatures for quest");
			return null;
		}

		final List<String> previous = new ArrayList<>();
		for (final String value: getStepsStates(player)) {
			if (value.contains(",")) {
				final String[] tmp = value.split(",");
				if (tmp[1].equals("done")) {
					previous.add(tmp[0]);
				}
			}
		}

		final List<String> eligible = new ArrayList<>();
		boolean satisfied = false;

		while (!satisfied) {
			for (final Creature creature: allCreatures) {
				// don't include rare & abnormal creatures
				if (creature.isAbnormal()) {
					continue;
				}

				final String creatureName = creature.getName();
				final int creatureLevel = creature.getLevel();

				if (!previous.contains(creatureName) && !eligible.contains(creatureName)
						&& creatureLevel >= playerLevel - threshold
						&& creatureLevel <= playerLevel + threshold) {
					eligible.add(creatureName);
				}
			}

			satisfied = eligible.size() > 0; // need at least 1 creature

			if (!satisfied) {
				// increase level threshold so more creatures can be added
				threshold += 5;
			}
		}

		// pick randomly from eligible creatures
		return eligible.get(Rand.randUniform(0, eligible.size() - 1));
	}

	/**
	 * Retrieves information for each step from quest slot.
	 *
	 * @param player
	 * 		Player doing the quest.
	 * @return
	 * 		List containing quest steps information.
	 */
	private List<String> getStepsStates(final Player player) {
		final String state = player.getQuest(QUEST_SLOT);
		if (state == null || !state.contains(";")) {
			return Arrays.asList("null", "null", "null");
		}

		final List<String> states = new ArrayList<>();
		for (final String st: state.split(";")) {
			states.add(st);
		}

		// in case there were less than 3 slot indexes
		while (states.size() < 3) {
			states.add("null");
		}

		return states;
	}

	/**
	 * Retrieves currently active step.
	 *
	 * @param player
	 * 		Player doing the quest.
	 * @return
	 * 		The step index.
	 */
	public int getCurrentStep(final Player player) {
		int step = 0;

		// max 3 steps
		while (step < 3) {
			if (!isStepDone(player, step)) {
				break;
			}

			step++;
		}

		return step;
	}

	/**
	 * Checks if player has completed a step.
	 *
	 * @param player
	 * 		Player doing the quest.
	 * @param step
	 * 		The current quest step.
	 * @return
	 * 		<code>true</code> if the player has completed the step.
	 */
	public boolean isStepDone(final Player player, final int step) {
		if (player.getQuest(QUEST_SLOT).equals("done")) {
			return true;
		}

		final List<String> states = getStepsStates(player);
		if (states.size() < step + 1) {
			return false;
		}

		final String[] currentState = states.get(step).split(",");
		if (currentState.length > 1) {
			return currentState[1].equals("done");
		}

		return false;
	}

	/**
	 * Retrieves enemy name stored in quest slot that player must kill for step.
	 *
	 * @param player
	 * 		Player doing the quest.
	 * @param step
	 * 		The current quest step.
	 * @return
	 * 		Name of enemy player is tasked to kill for step.
	 */
	public String getEnemyForStep(final Player player, final int step) {
		final String enemy = getStepsStates(player).get(step).split(",")[0];
		if (enemy == null) {
			logger.error("Could not retrieve enemy/creature from quest slot for step " + step);
		} else if(enemy.equals("null") || enemy.equals("")) {
			return null;
		}

		return enemy;
	}

	/**
	 * Retrieves original kill count of enemy before quest was started.
	 *
	 * @param player
	 * 		Player doing the quest.
	 * @param step
	 * 		The current quest step.
	 * @return
	 * 		Recorded kill count stored in quest slot.
	 */
	public Integer getRecordedKillsForStep(final Player player, final int step) {
		Integer kills = null;
		final String[] indexState = getStepsStates(player).get(step).split(",");
		if (indexState.length > 1) {
			try {
				kills = Integer.parseInt(indexState[1]);
			} catch (final NumberFormatException e) {
				logger.error("Error parsing kill count from quest slot for step " + step);
			}
		}

		if (kills == null) {
			logger.error("Could not retrieve kill count from quest slot for step " + step);
		}

		return kills;
	}

	/**
	 * Retrieves the key to identify which question should be asked.
	 *
	 * @param player
	 * 		Player doing the quest.
	 * @param step
	 * 		The current quest step.
	 * @return
	 * 		Key stored in quest slot that identifies which question should be asked.
	 */
	private String getQuestionKeyForStep(final Player player, final int step) {
		final String[] indexState = getStepsStates(player).get(step).split(",");
		if (indexState.length < 3) {
			logger.warn("Question key not found in quest slog");
			return null;
		}

		final String questionKey = indexState[2];
		if (questionKey == null) {
			logger.error("Could not retrieve question key from quest slot for step " + step);
		}

		return indexState[2];
	}

	/**
	 * Retrieves the question that will be asked for the step.
	 *
	 * @param player
	 * 		Player doing the quest.
	 * @param step
	 * 		The current quest step.
	 * @return
	 * 		Question to be asked to player.
	 */
	public String getQuestionForStep(final Player player, final int step) {
		String questionKey = getQuestionKeyForStep(player, getCurrentStep(player));
		if (questionKey == null || !questionOptions.containsKey(questionKey)) {
			// default to "level" in case an appropriate question cannot be found
			logger.warn("Using default \"level\" to retrieve question for step " + step);
			questionKey = "level";
		}

		final Pair<String, String> questionPair = questionOptions.get(questionKey);
		final String prefix = questionPair.first();
		final String suffix = questionPair.second();
		final String currentCreature = getEnemyForStep(player, step);

		if (prefix == null || currentCreature == null) {
			return null;
		}

		String questionString = prefix.trim() + " " + Grammar.a_noun(currentCreature);
		if (suffix != null) {
			questionString += " " + suffix.trim();
		}

		return questionString + "?";
	}

	/**
	 * Retrieves the correct answer for the step.
	 *
	 * @param player
	 * 		Player doing the quest.
	 * @param creature
	 * 		Creature which player was tasked to kill.
	 * @param step
	 * 		The current quest step.
	 * @return
	 * 		The answer to the question asked.
	 */
	public String getAnswerForStep(final Player player, final Creature creature, final int step) {
		String answer = null;

		// FIXME: is there a way to call these methods automatically?
		final String questionKey = getQuestionKeyForStep(player, step);

		if (questionKey.equals("hp")) {
			answer = Integer.toString(creature.getBaseHP());
		} else if (questionKey.equals("level")) {
			answer = Integer.toString(creature.getLevel());
		} else if (questionKey.equals("def")) {
			answer = Integer.toString(creature.getDef());
		} else if (questionKey.equals("atk")) {
			answer = Integer.toString(creature.getAtk());
		} else if (questionKey.equals("ratk")) {
			answer = Integer.toString(creature.getRatk());
		}

		if (answer == null) {
			// default to "level" in case an appropriate answer cannot be found
			logger.warn("Using default \"level\" to retrieve answer for step " + step);
			answer = Integer.toString(creature.getLevel());
		}

		return answer;
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}

		res.add("I have found " + npc.getName() + ", a wandering adventurer.");

		for (int step = 0; step < 3; step++) {
			final String enemy = getEnemyForStep(player, step);
			if (enemy != null) {
				if (!isStepDone(player, step)) {
					res.add("He has asked me to get information on " + Grammar.a_noun(enemy) + ".");
				} else {
					String key = getQuestionKeyForStep(player, step);
					if (!key.equals("level")) {
						key = key.toUpperCase();
					}
					res.add("I reported information on the " + key + " of " + Grammar.a_noun(enemy) + ".");
				}
			}
		}

		if (isCompleted(player)) {
			res.add("I gathered all the information that " + npc.getName() + " requested on some enemies found in Faiumoni. Now I can purchase a bestiary from him.");
		}

		return res;
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public void addToWorld() {
		initNPC();
		initQuest();
		initShop();

		fillQuestInfo(
				QUEST_NAME,
				npc.getName() + " wants help collecting data on creatures found around Faimouni.",
				false);
	}
	@Override
	public boolean removeFromWorld() {
		final StendhalRPZone currentZone = npc.getZone();
		if (currentZone != null) {
			currentZone.remove(npc);
		}

		// remove the turn notifiers left from the TeleporterBehaviour
		SingletonRepository.getTurnNotifier().dontNotify(teleporterBehaviour);
		return true;
	}

	@Override
	public String getName() {
		return QUEST_NAME.replace(" ", "");
	}

	@Override
	public String getNPCName() {
		if (npc == null) {
			return null;
		}

		return npc.getName();
	}

	@Override
	public boolean isCompleted(final Player player) {
		if (player.hasQuest(QUEST_SLOT)) {
			return (isStepDone(player, 0) && isStepDone(player, 1) && isStepDone(player, 2)) || player.getQuest(QUEST_SLOT).equals("done");
		}

		return false;
	}

	/**
	 *
	 * @param msg
	 * 		Message to print in the console.
	 */
	private void onError(final String msg) {
		if (msg != null) {
			logger.error(msg);
		}

		npc.say("Strange. I'm having some memory loss. It appears I cannot help you finish this quest.");
		npc.setCurrentState(ConversationStates.ATTENDING);
	}
}
