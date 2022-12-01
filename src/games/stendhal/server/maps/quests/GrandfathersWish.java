/***************************************************************************
 *                    Copyright © 2003-2022 - Arianne                      *
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
import java.util.List;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DecreaseKarmaAction;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EnableFeatureAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.NPCEmoteAction;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.LevelLessThanCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasInfostringItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.server.maps.quests.grandfatherswish.MylingSpawner;


/**
 * Quest to increase number of bag slots.
 *
 * NPCs:
 * - Elias Breland
 * - Niall Breland
 * - Marianne
 * - Father Calenus
 *
 * Required items:
 * - rope ladder
 * - holy water
 *
 * Reward:
 * - 5000 XP
 * - 500 karma
 * - 3 more bag slots
 */
public class GrandfathersWish extends AbstractQuest {

	public static final String QUEST_SLOT = "grandfatherswish";
	private static final int min_level = 100;

	private final SpeakerNPC elias = npcs.get("Elias Breland");

	private static MylingSpawner spawner;


	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public String getName() {
		return "GrandfathersWish";
	}

	@Override
	public String getRegion() {
		return Region.DENIRAN;
	}

	@Override
	public String getNPCName() {
		return elias.getName();
	}

	@Override
	public int getMinLevel() {
		return min_level;
	}

	@Override
	public List<String> getHistory(final Player player) {
		final String[] states = player.getQuest(QUEST_SLOT).split(";");
		final String quest_state = states[0];
		String find_myling = null;
		String holy_water = null;
		String cure_myling = null;
		for (final String st: states) {
			if (st.startsWith("find_myling:")) {
				find_myling = st.split(":")[1];
			}
			if (st.startsWith("holy_water:")) {
				holy_water = st.split(":")[1];
			}
			if (st.startsWith("cure_myling:")) {
				cure_myling = st.split(":")[1];
			}
		}

		final List<String> res = new ArrayList<>();
		res.add(elias.getName() + " wishes to know what has become of his"
			+ " estranged grandson.");

		if (quest_state.equals("rejected")) {
			res.add("I have no time for senile old men.");
		} else {
			res.add("I have agreed to investigate.");
			if (find_myling != null) {
				res.add("Marianne mentioned that Niall wanted to"
					+ " explore the graveyard in the Semos area.");
				if (find_myling.equals("done")) {
					res.add("Niall has been turned into a myling. Elias will be"
						+ " devestated. But I must tell him.");
				}
			}
			if (holy_water != null) {
				res.add("There may be hope yet. I must find a priest and ask"
					+ " about holy water to help change Niall back to normal.");
				if (!holy_water.equals("start")) {
					res.add("I found Father Calenus.");
					if (holy_water.equals("bring_items")) {
						res.add("He asked me to gather some items. He needs a flask"
							+ " of water and some charcoal.");
					} else if (holy_water.equals("blessing")) {
						res.add("He is blessing the holy water and will give it to"
							+ " me when it is ready.");
					} else if (holy_water.equals("done")) {
						res.add("He gave me a bottle of blessed holy water. Now I"
							+ " must use it on Niall.");
					}
				}
			}
			if (cure_myling != null && cure_myling.equals("done")) {
				res.add("I used the holy water. Niall is healed! Now I should"
					+ " bring him back to his grandfather.");
			}
			if (quest_state.equals("done")) {
				res.add("Elias and his grandson have been"
					+ " reunited.");
			}
		}

		return res;
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
			"A Grandfather's Wish",
			elias.getName() + " is grieved over the loss of his grandson.",
			false
		);
		prepareRequestStep();
		prepareMarianneStep();
		prepareFindPriestStep();
		prepareHolyWaterStep();
		prepareCompleteStep();
		prepareMylingSpawner();
	}

	private void prepareRequestStep() {
		// requests quest but does not meet minimum level requirement
		elias.add(
			ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new AndCondition(
				new QuestNotStartedCondition(QUEST_SLOT),
				new LevelLessThanCondition(min_level)),
			ConversationStates.ATTENDING,
			"My grandson disappeared over a year ago. But I need help from a"
				+ " more experienced adventurer.",
			null);

		// requests quest
		elias.add(
			ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new AndCondition(
				new QuestNotStartedCondition(QUEST_SLOT),
				new NotCondition(new LevelLessThanCondition(min_level))),
			ConversationStates.QUEST_OFFERED,
			"My grandson disappeared over a year ago. I fear the worst and"
				+ " have nearly given up all hope. What I would give to just"
				+ " know what happened to him! If you learn anything will"
				+ " you bring me the news?",
			null);

			// already accepted quest
			elias.add(
				ConversationStates.ANY,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Thank you for accepting my plea for help. Please tell me if"
					+ " you hear any news about what has become of my grandson."
					+ " He used to play with a little girl named #Marianne.",
				null);

			// already completed quest
			elias.add(
				ConversationStates.ANY,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Thank you for returning my grandson to me. I am overfilled"
					+ " with joy!",
				null);

			// rejects quest
			elias.add(
				ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Alas! What has become of my grandson!?",
				new MultipleActions(
					new SetQuestAction(QUEST_SLOT, "rejected;;;"),
					new DecreaseKarmaAction(15)));

			// accepts quest
			elias.add(
				ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				ConversationStates.ATTENDING,
				"Oh thank you! My grandson's name is #Niall. You could talk"
					+ " to #Marianne. They used to play together.",
				new MultipleActions(
					new SetQuestAction(QUEST_SLOT, "investigate;;;"),
					new IncreaseKarmaAction(15)));

			// ask about Niall
			elias.add(
				ConversationStates.ANY,
				Arrays.asList("Niall", "grandson"),
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Niall is my grandson. I am so distraught over his"
					+ " disappearance. Ask the girl #Marianne. They often played"
					+ " together.",
				null);

			// ask about Marianne
			elias.add(
				ConversationStates.ANY,
				"Marianne",
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Marianne lives here in Deniran. Ask her about #Niall.",
				null);
	}

	private void prepareMarianneStep() {
		final SpeakerNPC marianne = npcs.get("Marianne");

		final ChatCondition investigating = new QuestActiveCondition(QUEST_SLOT);

		marianne.add(
			ConversationStates.ATTENDING,
			"Niall",
			investigating,
			ConversationStates.ATTENDING,
			"Oh! My friend Niall! I haven't seen him in a long time. Every"
				+ " time I go to his grandfather's house to #play, he is not"
				+ " home.",
			new NPCEmoteAction("suddenly looks very melancholy."));

		marianne.add(
			ConversationStates.ATTENDING,
			"play",
			investigating,
			ConversationStates.ATTENDING,
			"Not only was he fun to play with, but he was also very helpful."
				+ " He used to help me gather chicken eggs whenever I was too"
				+ " #afraid to do it myself.",
			new NPCEmoteAction("looks even more melancholy."));

		marianne.add(
			ConversationStates.ATTENDING,
			"afraid",
			investigating,
			ConversationStates.ATTENDING,
			"Know what he told me once? He said he wanted to go all the way"
				+ " to Semos to see the #graveyard there. Nuh uh! No way! That"
				+ " sounds more scary than chickens.",
			new MultipleActions(
				new NPCEmoteAction("shivers."),
				new SetQuestAction(QUEST_SLOT, 1, "find_myling:start")));

		marianne.add(
			ConversationStates.ATTENDING,
			Arrays.asList("graveyard", "cemetary"),
			investigating,
			ConversationStates.ATTENDING,
			"I hope he didn't go to that scary graveyard. Who knows what kind"
				+ " of monsters are there.",
			null);

		marianne.add(
			ConversationStates.ATTENDING,
			"Niall",
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"I heard that Niall came home! He sure was gone for a long time."
				+ " I am glad he is home safe.",
			new NPCEmoteAction("lets out a sigh of relief."));
	}

	private void prepareFindPriestStep() {
			final ChatCondition found_myling = new QuestInStateCondition(QUEST_SLOT, 1, "find_myling:done");

			// tells Elias that Niall has been turned into a myling
			elias.add(
				ConversationStates.ANY,
				Arrays.asList("Niall", "myling"),
				found_myling,
				ConversationStates.ATTENDING,
				"Oh no! My dear grandson! If only there were a way to #change"
					+ " him back.",
				null);

			elias.add(
				ConversationStates.ANY,
				"change",
				found_myling,
				ConversationStates.ATTENDING,
				"Wait! I have heard that #'holy water' has special properties"
					+ " when used on the undead. Perhaps a #priest would have"
					+ " have some. Please, go and find a priest.",
				new SetQuestAction(QUEST_SLOT, 2, "holy_water:start"));

			elias.add(
				ConversationStates.ANY,
				Arrays.asList("Niall", "myling", "priest", "holy water"),
				new QuestInStateCondition(QUEST_SLOT, 2, "holy_water:start"),
				ConversationStates.ATTENDING,
				"Please! Find a priest. Maybe one can provide holy water to"
					+ " help my grandson.",
				null);
	}

	private void prepareHolyWaterStep() {
		final SpeakerNPC priest = npcs.get("Father Calenus");

		final int blessTime = 10; // 10 minutes to make holy water

		final ChatCondition stateBringing =
			new QuestInStateCondition(QUEST_SLOT, 2, "holy_water:bring_items");
		final ChatCondition stateBlessing =
			new QuestInStateCondition(QUEST_SLOT, 2, "holy_water:blessing");

		final ChatCondition canRequestHolyWater = new AndCondition(
			new QuestActiveCondition(QUEST_SLOT),
			new NotCondition(new PlayerHasInfostringItemWithHimCondition("ashen holy water", "Niall Breland")),
			new OrCondition(
				new QuestInStateCondition(QUEST_SLOT, 2, "holy_water:start"),
				new QuestInStateCondition(QUEST_SLOT, 2, "holy_water:done"))
		);

		final ChatCondition hasIngredients = new AndCondition(
			new PlayerHasItemWithHimCondition("water"),
			new PlayerHasItemWithHimCondition("charcoal"));

		final ChatAction equipWithHolyWater = new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				final Item holy_water = SingletonRepository.getEntityManager().getItem("ashen holy water");
				holy_water.setDescription("A bottle of ashen holy water to cure Niall.");
				holy_water.setInfoString("Niall Breland");
				holy_water.setBoundTo(player.getName());

				player.equipOrPutOnGround(holy_water);
			}
		};

		priest.add(
			ConversationStates.ATTENDING,
			Arrays.asList("holy water", "myling", "Niall", "Elias"),
			canRequestHolyWater,
			ConversationStates.ATTENDING,
			"Oh my! A young boy has transformed into a myling? I can help,"
				+ " but this will require a special holy water. Bring me a"
				+ " flask of water and some charcoal.",
			new SetQuestAction(QUEST_SLOT, 2, "holy_water:bring_items"));

		priest.add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			stateBringing,
			ConversationStates.QUESTION_1,
			"Have you brought the items I requested?",
			null);

		priest.add(
			ConversationStates.QUESTION_1,
			ConversationPhrases.NO_MESSAGES,
			stateBringing,
			ConversationStates.ATTENDING,
			"Okay, I still need a flask of water and some charcoal.",
			null);

		priest.add(
			ConversationStates.QUESTION_1,
			ConversationPhrases.YES_MESSAGES,
			new AndCondition(
				stateBringing,
				new NotCondition(hasIngredients)),
			ConversationStates.ATTENDING,
			"Hmmm... It doesn't look like you have what I need. I requested"
				+ " a flask of water and some charcoal.",
			null);

		priest.add(
			ConversationStates.QUESTION_1,
			ConversationPhrases.YES_MESSAGES,
			new AndCondition(
				stateBringing,
				hasIngredients),
			ConversationStates.IDLE,
			"Okay. It will take about 10 minutes to bless this water and"
				+ " make it holy.",
			new MultipleActions(
				new DropItemAction("water"),
				new DropItemAction("charcoal"),
				new SetQuestAction(QUEST_SLOT, 2, "holy_water:blessing"),
				new SetQuestToTimeStampAction(QUEST_SLOT, 4)));
			/*
				new SayTimeRemainingAction(QUEST_SLOT, 4, blessTime,
					"Okay. It will take about", "to bless this water and"
						+ " make it holy.")));
			*/

		priest.add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(
				stateBlessing,
				new NotCondition(new TimePassedCondition(QUEST_SLOT, 4,
					blessTime))),
			ConversationStates.ATTENDING,
			null,
			new SayTimeRemainingAction(QUEST_SLOT, 4, blessTime, "The holy"
				+ " water will be ready in"));

		priest.add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(
				stateBlessing,
				new TimePassedCondition(QUEST_SLOT, 4, blessTime)),
			ConversationStates.ATTENDING,
			"Here is the holy water. Use it to cure the boy.",
			new MultipleActions(
				equipWithHolyWater,
				new SetQuestAction(QUEST_SLOT, 2, "holy_water:done"),
				new SetQuestAction(QUEST_SLOT, 3, "cure_myling:start"),
				new SetQuestAction(QUEST_SLOT, 4, null)));
	}

	private void prepareCompleteStep() {
		final SpeakerNPC niall = npcs.get("Niall Breland");

		final ChatCondition canGetReward = new AndCondition(
			new QuestActiveCondition(QUEST_SLOT),
			new QuestInStateCondition(QUEST_SLOT, 3, "cure_myling:done"));

		// Niall has been healed
		elias.add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			canGetReward,
			ConversationStates.ATTENDING,
			"You have returned my grandson to me. I cannot thank you enough."
				+ " I don't have much to offer for your kind service, but"
				+ " please speak to Niall. He is in the basement.",
				null);

		elias.add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"Thank you for returning my grandson to me. He is in the basement"
				+ " if you want to speak to him.",
			null);

		niall.add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			canGetReward,
			ConversationStates.ATTENDING,
			"Thank you. Without your help, I would have never made it back"
				+ " home. This is my backpack. I want you to have it. It will"
				+ " enable you to carry more stuff.",
			new MultipleActions(
				new SetQuestAction(QUEST_SLOT, 0, "done"),
				new SetQuestToTimeStampAction(QUEST_SLOT, 4), // store timestamp of completion
				new IncreaseKarmaAction(500),
				new IncreaseXPAction(5000),
				new EnableFeatureAction("bag", "3 5")));

		niall.add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"Hi again. I'm getting ready to go on another adventure with"
				+ " Marianne. But don't worry, we are staying away from"
				+ " cemeteries.",
			null);
	}

	private void prepareMylingSpawner() {
		final StendhalRPZone wellZone = SingletonRepository.getRPWorld().getZone("-1_myling_well");
		spawner = new MylingSpawner();
		spawner.setPosition(6, 5);
		wellZone.add(spawner);
		spawner.startSpawnTimer();
	}

	public static MylingSpawner getMylingSpawner() {
		return spawner;
	}
}
