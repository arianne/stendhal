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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.Rand;
import games.stendhal.common.constants.SkinColor;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayTextAction;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.TeleporterBehaviour;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.SoundEvent;


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

	private static final int bestiaryPrice = 500000;

	private String questionOption = null;


	private void initNPC() {
		npc = new SpeakerNPC("Rengard");
		npc.setOutfit("body=0,head=0,eyes=16,hair=39,dress=999,mask=4,hat=10");
		npc.setOutfitColor("skin", SkinColor.DARK);
		npc.setOutfitColor("hat", 0xff0000);
		npc.setDescription("You see a seasoned adventurer with a smile on his face and a sparkle in his eye.");

		npc.addGreeting("Hello fellow adventurer.");
		npc.addGoodbye("May you have luck on your future adventures.");
		npc.addJob("Job? Hah! I am a free spirit. I travel the world, seeking to increase my own knowledge and experience.");

		final String helpReply = "If you seek to expand your knowledge as I do, I have a little #task I could use some help with.";

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.OFFER_MESSAGES,
				new NotCondition(new QuestCompletedCondition(QUEST_SLOT)),
				ConversationStates.ATTENDING,
				helpReply,
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.HELP_MESSAGES,
				new NotCondition(new QuestCompletedCondition(QUEST_SLOT)),
				ConversationStates.ATTENDING,
				helpReply,
				null);

		// NPC only visits exterior zones that are not cities & not highly populated
		// TODO: add atlantis zones
		final List<String> zonesBlacklist = Arrays.asList("*kikareukin_cave", "7_kikareukin_clouds",
				"0_semos_village_w", "0_semos_mountain_n2_e2", "0_semos_mountain_n2", "0_semos_mountain_n_w2",
				"0_ados_wall", "0_ados_wall_n", "0_ados_wall_n2", "0_ados_wall_s");
		final List<String> zonesWhitelist = new ArrayList<String>();

		final StendhalRPWorld world = StendhalRPWorld.get();

		for (final String region: world.getRegions()) {
			for (final StendhalRPZone zone: world.getAllZonesFromRegion(region, true, true, true)) {
				final String zoneName = zone.getName();

				// exclude city zones
				if (zoneName.endsWith("_city")) {
					continue;
				}

				boolean blacklisted = false;

				for (String blZone: zonesBlacklist) {
					if (blZone.startsWith("*")) {
						blZone = blZone.replace("*", "");
						if (zoneName.contains(blZone)) {
							blacklisted = true;
							break;
						}
					} else {
						if (zoneName.equals(blZone)) {
							blacklisted = true;
							break;
						}
					}
				}

				if (!blacklisted) {
					zonesWhitelist.add(zone.getName());
				}
			}
		}

		teleporterBehaviour = new TeleporterBehaviour(npc, zonesWhitelist, "", "♫♫♫") {
			@Override
			protected void doRegularBehaviour() {
				super.doRegularBehaviour();

				npc.addEvent(new SoundEvent("npc/whistling-01", SoundLayer.CREATURE_NOISE));
			}
		};

		teleporterBehaviour.setExitsConversation(false);
		teleporterBehaviour.onTurnReached(0); // initialize NPC on random map
	}

	private void initQuestDialogue() {
		final ChatCondition isFinalStepCondition = new ChatCondition() {
			@Override
			public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
				final String state = player.getQuest(QUEST_SLOT);
				if (state == null) {
					return false;
				}

				final String[] steps = state.split(";");
				if (steps.length < 3) {
					return false;
				}

				boolean step1Done = false;
				boolean step2Done = false;
				boolean step3Done = false;

				if (steps[0].contains("=")) {
					step1Done = steps[0].split("=")[1].equals("done");
				}
				if (steps[1].contains("=")) {
					step2Done = steps[1].split("=")[1].equals("done");
				}
				if (steps[2].contains("=")) {
					step3Done = steps[2].split("=")[1].equals("done");
				}

				return step1Done && step2Done && !step3Done;
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
						new NotCondition(new QuestCompletedCondition(QUEST_SLOT))),
				ConversationStates.ATTENDING,
				"You have already agreed to help me collect creature data.",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
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
				setQuestAction());

		// player has to returned to give info
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
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
				new NotCondition(hasKilledCreatureCondition()),
				ConversationStates.ATTENDING,
				"Don't lie to me. You haven't even killed one yet.",
				null);

		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				hasKilledCreatureCondition(),
				ConversationStates.QUESTION_2,
				null,
				askAboutCreatureAction());

		npc.add(ConversationStates.QUESTION_2,
				"",
				new NotCondition(answeredCorrectlyCondition()),
				ConversationStates.IDLE,
				"Hmmm, that doesn't seem accurate. Perhaps you could double check.",
				null);

		npc.add(ConversationStates.QUESTION_2,
				"",
				new AndCondition(
						answeredCorrectlyCondition(),
						new NotCondition(isFinalStepCondition)),
				ConversationStates.IDLE,
				null,
				new MultipleActions(
						completeStepAction(),
						setQuestAction()));

		npc.add(ConversationStates.QUESTION_2,
				"",
				new AndCondition(
						answeredCorrectlyCondition(),
						isFinalStepCondition),
				ConversationStates.ATTENDING,
				null,
				new MultipleActions(
						completeStepAction(),
						rewardPlayerAction()));
	}

	private void initShop() {
		final Map<String, Integer> prices = new LinkedHashMap<String, Integer>() {{
			put("bestiary", bestiaryPrice);
		}};

		final SellerBehaviour behaviour = new SellerBehaviour(prices) {
			@Override
			public ChatCondition getTransactionCondition() {
				return new QuestCompletedCondition(QUEST_SLOT);
			}

			@Override
			public ChatAction getRejectedTransactionAction() {
				return new SayTextAction("I need your help first.");
			}

			@Override
			public boolean transactAgreedDeal(ItemParserResult res, final EventRaiser seller, final Player player) {
				if (super.transactAgreedDeal(res, seller, player)) {
					seller.say("I have written your name down in it, just in case you lose it.");

					return true;
				}

				return false;
			}

			@Override
			public Item getAskedItem(final String askedItem, final Player player) {
				final Item item = super.getAskedItem(askedItem, player);

				// set infostring with player name to prevent others from using it
				item.setInfoString(player.getName());
				return item;
			}
		};
		new SellerAdder().addSeller(npc, behaviour, false);


		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.OFFER_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"I can sell you a #bestiary.",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.HELP_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"If you own a #bestiary, you may be able to find a psychic that can give you more insight into the creatures you have encountered.",
				null);

		npc.add(ConversationStates.ATTENDING,
				"bestiary",
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"A bestiary allows you to keep track of the enemies you have defeated.",
				null);
	}

	private ChatAction setQuestAction() {
		return new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				if (player.getQuest(QUEST_SLOT) == null) {
					final String selected = selectCreature(player);
					final int killCount = player.getSoloKill(selected) + player.getSharedKill(selected);
					player.setQuest(QUEST_SLOT, selected + "=" + killCount + ";null;null");
					player.addKarma(35.0);

					npc.say("Great! I have compiled much info on creatures I have come across. But I am still missing three. First, I need some info on "
							+ Grammar.singular(selected) + ".");
				} else {
					final String selected = selectCreature(player);
					final int killCount = player.getSoloKill(selected) + player.getSharedKill(selected);
					player.setQuest(QUEST_SLOT, getCurrentStep(player), selected + "=" + killCount);

					npc.say("Thank you! I am going to write this down. Now I need information on " + Grammar.singular(selected) + ".");
				}
			}
		};
	}

	private ChatAction askAboutCreatureAction() {
		return new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				final List<String> options = Arrays.asList("level", "hp");
				final int lot = Rand.randUniform(0, options.size() - 1);

				questionOption = options.get(lot);
				final String currentCreature = getCurrentCreature(player);

				if (questionOption.equals("level")) {

				} if (questionOption.equals("hp")) {
					npc.say("How much HP does " + Grammar.singular(currentCreature) + " have?");
				} else {
					npc.say("What is the level of " + Grammar.singular(currentCreature) + "?");
				}
			}
		};
	}

	private List<String> getStates(final Player player) {
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
		for (final String value: getStates(player)) {
			if (value.contains("=")) {
				final String[] tmp = value.split("=");
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

	private int getCurrentStep(final Player player) {
		int step = 0;
		final String state = player.getQuest(QUEST_SLOT);
		if (state == null || !state.contains(";")) {
			return step;
		}

		final String[] states = state.split(";");

		for (final String slot: states) {
			if (!slot.contains("=")) {
				break;
			}
			if (!slot.split("=")[1].equals("done")) {
				break;
			}

			step++;
		}

		return step;
	}

	private String getCurrentCreature(final Player player) {
		final String state = player.getQuest(QUEST_SLOT, getCurrentStep(player));

		if (state != null && state.contains("=")) {
			return state.split("=")[0];
		}

		return state;
	}

	private ChatCondition hasKilledCreatureCondition() {
		return new ChatCondition() {
			@Override
			public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
				final String[] state = player.getQuest(QUEST_SLOT).split(";");
				if (state == null) {
					return false;
				}

				final int currentStep = getCurrentStep(player);
				final String creature = state[currentStep].split("=")[0];
				final int recordedKills = Integer.parseInt(state[currentStep].split("=")[1]);

				return (player.getSoloKill(creature) + player.getSharedKill(creature)) > recordedKills;
			}
		};
	}

	private ChatCondition answeredCorrectlyCondition() {
		return new ChatCondition() {
			@Override
			public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
				final String fromQuestSlot = getCurrentCreature(player);
				final Creature creature = SingletonRepository.getEntityManager().getCreature(fromQuestSlot);

				if (creature == null) {
					logger.error("Invalid creature name in quest slot: " + fromQuestSlot);
					return false;
				}

				final String answer = sentence.getTrimmedText();

				if (questionOption.equals("hp")) {
					return answer.equals(Integer.toString(creature.getBaseHP()));
				}

				return answer.equals(Integer.toString(creature.getLevel()));
			}
		};
	}

	private ChatAction completeStepAction() {
		return new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				final int currentStep = getCurrentStep(player);
				final String creatureName = player.getQuest(QUEST_SLOT, currentStep).split("=")[0];
				player.setQuest(QUEST_SLOT, currentStep, creatureName + "=done");
			}
		};
	}

	private ChatAction rewardPlayerAction() {
		return new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				npc.say("Thanks so much for you help. Now I have all the information I need to complete my #bestiary."
						+ " If you would like one of your own, I can sell you one.");
				player.addKarma(200.0);
				player.setQuest(QUEST_SLOT, "done");
			}
		};
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}

		res.add("I have found " + npc.getName() + ", a wandering adventurer.");

		return res;
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public void addToWorld() {
		initNPC();
		initQuestDialogue();
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
}
