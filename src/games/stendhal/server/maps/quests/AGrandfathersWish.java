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
import games.stendhal.server.entity.Entity;
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
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.server.maps.quests.a_grandfathers_wish.MylingSpawner;
import games.stendhal.server.util.TimeUtil;


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
public class AGrandfathersWish extends AbstractQuest {

	public static final String QUEST_SLOT = "a_grandfathers_wish";
	private static final int min_level = 100;

	private final SpeakerNPC elias = npcs.get("Elias Breland");

	private static MylingSpawner spawner;


	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public String getName() {
		return "AGrandfathersWish";
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
	public boolean removeFromWorld() {
		if (spawner != null) {
			spawner.removeActiveMylings();
			SingletonRepository.getRPWorld().remove(spawner.getID());
		}

		// FIXME: NPCs should be reset

		return true;
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
			} else if (st.startsWith("holy_water:")) {
				holy_water = st.split(":")[1];
			} else if (st.startsWith("cure_myling:")) {
				cure_myling = st.split(":")[1];
			}
		}

		final List<String> res = new ArrayList<>();
		res.add(elias.getName() + " wishes to know what has become of his"
			+ " estranged grandson.");

		if (quest_state.equals("rejected")) {
			res.add("I have no time for senile old men.");
		} else {
			res.add("I have agreed to investigate. I should speak with the"
				+ " girl Marianne and ask her about Niall. They used to play"
				+ " together.");
			if (find_myling != null) {
				res.add("Marianne mentioned that Niall wanted to explore the"
					+ " graveyard in Semos mountains.");
				if (find_myling.equals("well_rope")) {
					res.add("I heard a strange noise coming from a well north of"
						+ " the graveyard. I need a rope to decend into it.");
				} else if (find_myling.equals("done")) {
					res.add("I found Niall in a well north of the graveyard. He"
						+ " has been turned into a myling.");
					if (holy_water == null) {
						res.add("Elias will be devestated but I must tell him.");
					}
				}
			}
			if (holy_water != null) {
				res.add("I told Elias about Niall's state. He asked me to find"
					+ " a priest and ask about holy water to help change Niall"
					+ " back to normal.");
				if (!holy_water.equals("find_priest")) {
					res.add("I met Father Calenus.");
					if (holy_water.equals("bring_items")) {
						res.add("He asked me to gather some items to bless holy"
							+ " water. He needs a flask of water and some charcoal.");
					} else if (holy_water.equals("blessing")) {
						res.add("He is blessing the holy water and will give it to"
							+ " me when it is ready.");
					} else if (holy_water.equals("done")) {
						res.add("He gave me a bottle of blessed holy water.");
						if (cure_myling == null || cure_myling.equals("start")) {
							res.add("Now I must use it on Niall.");
						}
					}
				}
			}
			if (cure_myling != null && cure_myling.equals("done")) {
				res.add("I used the holy water to cure Niall.");
				if (!quest_state.equals("done")) {
					res.add("I should visit him at his house to see how he is"
						+ " doing.");
					}
			}
			if (quest_state.equals("done")) {
				res.add("Elias and his grandson have been reunited. Niall gave"
					+ " me his backpack. Now I can carry more items.");
			}
		}

		return res;
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
			"A Grandfather's Wish",
			elias.getName() + " is grieved over the disappearance of his"
				+ " grandson.",
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
				new AndCondition(
					new QuestActiveCondition(QUEST_SLOT),
					new QuestNotInStateCondition(QUEST_SLOT, 3, "cure_myling:done")),
				ConversationStates.ATTENDING,
				"Thank you for accepting my plea for help. Please tell me if"
					+ " you hear any news about what has become of my grandson."
					+ " He used to play with a little girl named #Marianne.",
				null);

			// already cured Niall
			elias.add(
				ConversationStates.ANY,
				ConversationPhrases.QUEST_MESSAGES,
				new OrCondition(
					new QuestCompletedCondition(QUEST_SLOT),
					new QuestInStateCondition(QUEST_SLOT, 3, "cure_myling:done")),
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
				new SetQuestAction(QUEST_SLOT, "investigate"));

			// ask about Niall
			elias.add(
				ConversationStates.ANY,
				Arrays.asList("Niall", "grandson"),
				new AndCondition(
					new QuestActiveCondition(QUEST_SLOT),
					new QuestInStateCondition(QUEST_SLOT, 2, "")),
				ConversationStates.ATTENDING,
				"Niall is my grandson. I am so distraught over his"
					+ " disappearance. Ask the girl #Marianne. They often played"
					+ " together.",
				null);

			// ask about Marianne
			elias.add(
				ConversationStates.ANY,
				"Marianne",
				new AndCondition(
					new QuestActiveCondition(QUEST_SLOT),
					new QuestInStateCondition(QUEST_SLOT, 2, "")),
				ConversationStates.ATTENDING,
				"Marianne lives here in Deniran. Ask her about #Niall.",
				null);
	}

	private void prepareMarianneStep() {
		final SpeakerNPC marianne = npcs.get("Marianne");

		final ChatCondition investigating1 = new AndCondition(
			new QuestActiveCondition(QUEST_SLOT),
			new ChatCondition() {
				@Override
				public boolean fire(final Player player, final Sentence sentence,
						final Entity entity) {
					return player.getQuest(QUEST_SLOT, 1).equals("");
				}
			});
		final ChatCondition investigating2 = new AndCondition(
			new QuestActiveCondition(QUEST_SLOT),
			new ChatCondition() {
				@Override
				public boolean fire(final Player player, final Sentence sentence,
						final Entity entity) {
					return !player.getQuest(QUEST_SLOT, 1).equals("");
				}
			});

		marianne.add(
			ConversationStates.ATTENDING,
			"Niall",
			investigating1,
			ConversationStates.ATTENDING,
			"Oh! My friend Niall! I haven't seen him in a long time. Every"
				+ " time I go to his grandfather's house to #play, he is not"
				+ " home.",
			new NPCEmoteAction("suddenly looks very melancholy.", false));

		marianne.add(
			ConversationStates.ATTENDING,
			"play",
			investigating1,
			ConversationStates.ATTENDING,
			"Not only was he fun to play with, but he was also very helpful."
				+ " He used to help me gather chicken eggs whenever I was too"
				+ " #afraid to do it myself.",
			new NPCEmoteAction("looks even more melancholy.", false));

		marianne.add(
			ConversationStates.ATTENDING,
			"afraid",
			investigating1,
			ConversationStates.ATTENDING,
			"Know what he told me once? He said he wanted to go all the way"
				+ " to Semos mountains to see the #graveyard there. Nuh uh! No"
				+ " way! That sounds more scary than chickens.",
			new MultipleActions(
				new NPCEmoteAction("shivers.", false),
				new SetQuestAction(QUEST_SLOT, 1, "find_myling:start")));

		marianne.add(
			ConversationStates.ATTENDING,
			"Niall",
			investigating2,
			ConversationStates.ATTENDING,
			"Niall said he wanted to go all the way to Semos mountains to see"
				+ " the #graveyard there. Nuh uh! No way! That sounds more"
				+ " scary than chickens.",
			null);

		marianne.add(
			ConversationStates.ATTENDING,
			Arrays.asList("graveyard", "cemetary"),
			investigating2,
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
			new NPCEmoteAction("lets out a sigh of relief.", false));
	}

	private void prepareFindPriestStep() {
		final ChatCondition foundMyling = new AndCondition(
			new QuestActiveCondition(QUEST_SLOT),
			new QuestInStateCondition(QUEST_SLOT, 1, "find_myling:done"),
			new QuestInStateCondition(QUEST_SLOT, 2, ""),
			new QuestNotInStateCondition(QUEST_SLOT, 3, "cure_myling:done"));
		final ChatCondition findPriest =
			new QuestInStateCondition(QUEST_SLOT, 2, "holy_water:find_priest");

		// tells Elias that Niall has been turned into a myling
		elias.add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			foundMyling,
			ConversationStates.ATTENDING,
			"Oh no! My dear grandson! If only there were a way to #change"
				+ " him back.",
			null);

		elias.add(
			ConversationStates.ANY,
			Arrays.asList("Niall", "myling"),
			foundMyling,
			ConversationStates.ATTENDING,
			"Oh no! My dear grandson! If only there were a way to #change"
				+ " him back.",
			null);

		elias.add(
			ConversationStates.ANY,
			"change",
			foundMyling,
			ConversationStates.ATTENDING,
			"Wait! I have heard that #'holy water' has special properties"
				+ " when used on the undead. Perhaps a #priest would have"
				+ " some. Please, go and find a priest.",
			new SetQuestAction(QUEST_SLOT, 2, "holy_water:find_priest"));

		elias.add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			findPriest,
			ConversationStates.ATTENDING,
			"Please! Find a priest. I have heard there is one in Ados that"
				+ " specializes in holy water. Maybe he can provide some to"
				+ " help my grandson.",
			null);

		elias.add(
			ConversationStates.ANY,
			Arrays.asList("Niall", "myling", "priest", "holy water"),
			findPriest,
			ConversationStates.ATTENDING,
			"Please! Find a priest. I have heard there is one in Ados that"
				+ " specializes in holy water. Maybe he can provide some to"
				+ " help my grandson.",
			null);
	}

	public static ChatCondition canRequestHolyWater() {
		return new AndCondition(
			new QuestActiveCondition(QUEST_SLOT),
			new NotCondition(new PlayerHasInfostringItemWithHimCondition("ashen holy water", "Niall Breland")),
			new OrCondition(
				new QuestInStateCondition(QUEST_SLOT, 2, "holy_water:find_priest"),
				new QuestInStateCondition(QUEST_SLOT, 2, "holy_water:done"))
		);
	}

	private void prepareHolyWaterStep() {
		final SpeakerNPC priest = npcs.get("Father Calenus");

		final int blessTime = 60; // 1 hour to make holy water

		final ChatCondition stateBringing =
			new QuestInStateCondition(QUEST_SLOT, 2, "holy_water:bring_items");
		final ChatCondition stateBlessing =
			new QuestInStateCondition(QUEST_SLOT, 2, "holy_water:blessing");

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
			canRequestHolyWater(),
			ConversationStates.ATTENDING,
			"Oh my! A young boy has transformed into a myling? I can help,"
				+ " but this will require a special holy water. Bring me a"
				+ " flask of water and some charcoal.",
			new SetQuestAction(QUEST_SLOT, 2, "holy_water:bring_items"));

		priest.add(
			ConversationStates.ATTENDING,
			Arrays.asList("holy water", "myling", "Niall", "Elias"),
			new QuestInStateCondition(QUEST_SLOT, 2, "holy_water:bring_items"),
			ConversationStates.ATTENDING,
			"I am still waiting for you to bring me a flask of water and some"
				+ " charcoal before I can bless the holy water.",
			null);

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
			"Okay. It will take about "
				+ TimeUtil.approxTimeUntil(blessTime * 60)
				+ " to bless this water and make it holy.",
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
				+ " graveyards.",
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
