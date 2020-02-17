/***************************************************************************
 *                   (C) Copyright 2003-2015 - Stendhal                    *
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
import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.ExamineChatAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Introduce new players to game <p>PARTICIPANTS:<ul>
 * <li> Tad
 * <li> Margaret
 * <li> Ilisa
 * <li> Ketteh Wehoh
 * </ul>
 *
 * <p>
 * STEPS:<ul>
 * <li> Tad asks you to buy a flask to give it to Margaret.
 * <li> Margaret sells you a flask
 * <li> Tad thanks you and asks you to take the flask to Ilisa
 * <li> Ilisa asks you for a few herbs.
 * <li> Return the created dress potion to Tad.
 * <li> Ketteh Wehoh will reminder player about Tad, if quest is started but not complete.
 * </ul>
 * <p>
 * REWARD:<ul>
 * <li> 270 XP
 * <li> some karma (4)
 * <li> 10 gold coins
 * </ul>
 * <p>
 * REPETITIONS:<ul>
 * <li> None.
 * </ul>
 */
public class MedicineForTad extends AbstractQuest {

	static final String ILISA_TALK_ASK_FOR_FLASK = "Medicine for #Tad? Didn't he tell you to bring a flask?";
	static final String ILISA_TALK_ASK_FOR_HERB = "Ah, I see you have that flask. #Tad needs medicine, right? Hmm... I'll need a #herb. Can you help?";
	static final String ILISA_TALK_DESCRIBE_HERB = "North of Semos, near the tree grove, grows a herb called arandula. Here is a picture I drew so you know what to look for.";
	static final String ILISA_TALK_INTRODUCE_TAD = "He needs a very powerful potion to heal himself. He offers a good reward to anyone who will help him.";
	static final String ILISA_TALK_REMIND_HERB = "Can you fetch those #herbs for the #medicine?";
	static final String ILISA_TALK_PREPARE_MEDICINE = "Okay! Thank you. Now I will just mix these... a pinch of this... and a few drops... there! Can you ask #Tad to stop by and collect it? I want to see how he's doing.";
	static final String ILISA_TALK_EXPLAIN_MEDICINE = "The medicine that #Tad is waiting for.";

	static final String KETTEH_TALK_BYE_INTRODUCES_TAD = "Farewell. Have you met Tad, in the hostel? If you get a chance, please check in on him. I heard he was not feeling well. You can find the hostel in Semos village, close to Nishiya.";
	static final String KETTEH_TALK_BYE_REMINDS_OF_TAD = "Goodbye. Don't forget to check on Tad. I hope he's feeling better.";

	static final String TAD_TALK_GOT_FLASK = "Ok, you got the flask!";
	static final String TAD_TALK_REWARD_MONEY = "Here, take this money to cover your expense.";
	static final String TAD_TALK_FLASK_ILISA = "Now, I need you to take it to #Ilisa... she'll know what to do next.";
	static final String TAD_TALK_REMIND_FLASK_ILISA = "I need you to take a flask to #Ilisa... she'll know what to do next.";
	static final String TAD_TALK_INTRODUCE_ILISA = "Ilisa is the summon healer at Semos temple.";
	static final String TAD_TALK_REMIND_MEDICINE = "*cough* I hope #Ilisa hurries with my medicine...";
	static final String TAD_TALK_COMPLETE_QUEST = "Thanks! I will go talk with #Ilisa as soon as possible.";

	static final String TAD_TALK_ASK_FOR_EMPTY_FLASK = "I'm not feeling well... I need to get a bottle of medicine made. Can you fetch me an empty #flask?";
	static final String TAD_TALK_ALREADY_HELPED_1 = "I'm alright now, thanks.";
	static final String TAD_TALK_ALREADY_HELPED_2 = "You've already helped me out! I'm feeling much better now.";
	static final String TAD_TALK_WAIT_FOR_FLASK = "*cough* Oh dear... I really need this medicine! Please hurry back with the #flask from #Margaret.";
	static final String TAD_TALK_FLASK_MARGARET = "You could probably get a flask from #Margaret.";
	static final String TAD_TALK_INTRODUCE_MARGARET = "Margaret is the maid in the inn just down the street.";
	static final String TAD_TALK_CONFIRM_QUEST = "So, will you help?";
	static final String TAD_TALK_QUEST_REFUSED = "Oh, please won't you change your mind? *sneeze*";
	static final String TAD_TALK_QUEST_ACCEPTED = "Great! Please go as quickly as you can. *sneeze*";

	static final String HISTORY_MET_TAD = "I have met Tad in Semos Hostel.";
	static final String HISTORY_QUEST_OFFERED = "He asked me to buy a flask from Margaret in Semos Tavern.";
	static final String HISTORY_GOT_FLASK = "I got a flask and will bring it to Tad soon.";
	static final String HISTORY_TAKE_FLASK_TO_ILISA = "Tad asked me to take the flask to Ilisa at Semos Temple.";
	static final String HISTORY_ILISA_ASKED_FOR_HERB = "Ilisa asked me to get a herb called Arandula which I can find north of Semos, near the tree grove.";
	static final String HISTORY_GOT_HERB = "I found some Arandula herbs and will bring them to Ilisa.";
	static final String HISTORY_POTION_READY = "Ilisa created a powerful potion to help Tad. She asked me to tell him that it is ready.";
	static final String HISTORY_DONE = "Tad thanked me.";

	static final String STATE_START = "start";
	static final String STATE_ILISA = "ilisa";
	static final String STATE_HERB = "corpse&herbs";
	static final String STATE_SHOWN_DRAWING = "shownDrawing";
	static final String STATE_POTION = "potion";
	static final String STATE_DONE = "done";

	private static final String QUEST_SLOT = "introduce_players";

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (player.hasQuest("TadFirstChat")) {
			res.add(HISTORY_MET_TAD);
		}
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		final String questState = player.getQuest(QUEST_SLOT, 0);
		if (player.isQuestInState(QUEST_SLOT, 0, STATE_START, STATE_ILISA, STATE_HERB, STATE_POTION, STATE_DONE)) {
			res.add(HISTORY_QUEST_OFFERED);
		}
		if (questState.equals(STATE_START) && player.isEquipped("flask")
				|| player.isQuestInState(QUEST_SLOT, 0, STATE_ILISA, STATE_HERB, STATE_POTION, STATE_DONE)) {
			res.add(HISTORY_GOT_FLASK);
		}
		if (player.isQuestInState(QUEST_SLOT, 0, STATE_ILISA, STATE_HERB, STATE_POTION, STATE_DONE)) {
			res.add(HISTORY_TAKE_FLASK_TO_ILISA);
		}
		if (player.isQuestInState(QUEST_SLOT, 0, STATE_HERB, STATE_POTION, STATE_DONE)) {
			res.add(HISTORY_ILISA_ASKED_FOR_HERB);
		}
		if (questState.equals(STATE_HERB) && player.isEquipped("arandula")
				|| player.isQuestInState(QUEST_SLOT, 0, STATE_POTION, STATE_DONE)) {
			res.add(HISTORY_GOT_HERB);
		}
		if (player.isQuestInState(QUEST_SLOT, 0, STATE_POTION, STATE_DONE)) {
			res.add(HISTORY_POTION_READY);
		}
		if (questState.equals(STATE_DONE)) {
			res.add(HISTORY_DONE);
		}
		return res;
	}

	private void step_1() {
		final SpeakerNPC npc = npcs.get("Tad");
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				TAD_TALK_ALREADY_HELPED_1,
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED,
				TAD_TALK_ASK_FOR_EMPTY_FLASK,
				null);

		// In case Quest has already been completed
		npc.add(ConversationStates.ATTENDING,
				"flask",
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				TAD_TALK_ALREADY_HELPED_2,
				null);

		// If quest is not started yet, start it.
		npc.add(ConversationStates.QUEST_OFFERED,
				"flask",
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED,
				TAD_TALK_FLASK_MARGARET,
				null);

		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				TAD_TALK_QUEST_ACCEPTED,
				new SetQuestAction(QUEST_SLOT, 0, STATE_START));

		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				TAD_TALK_QUEST_REFUSED,
				null);

		npc.add(ConversationStates.QUEST_OFFERED,
				"margaret",
				null,
				ConversationStates.QUEST_OFFERED,
				TAD_TALK_INTRODUCE_MARGARET + " " + TAD_TALK_CONFIRM_QUEST,
				null);

		// Remind player about the quest
		npc.add(ConversationStates.ATTENDING,
				"flask",
				new AndCondition(
						new QuestInStateCondition(QUEST_SLOT, 0, STATE_START),
						new NotCondition(new PlayerHasItemWithHimCondition("flask"))),
				ConversationStates.ATTENDING,
				TAD_TALK_WAIT_FOR_FLASK,
				null);

        // Remind player about the quest
        npc.add(ConversationStates.ATTENDING,
                ConversationPhrases.QUEST_MESSAGES,
                new QuestInStateCondition(QUEST_SLOT, 0, STATE_START),
                ConversationStates.ATTENDING,
                TAD_TALK_WAIT_FOR_FLASK,
                null);

		npc.add(ConversationStates.ATTENDING,
				"margaret",
				null,
				ConversationStates.ATTENDING,
				TAD_TALK_INTRODUCE_MARGARET,
				null);
	}

	private void step_2() {
		/** Just buy the stuff from Margaret. It isn't a quest */
	}

	private void step_3() {
		final SpeakerNPC npc = npcs.get("Tad");

		final List<ChatAction> processStep = new LinkedList<ChatAction>();
		processStep.add(new EquipItemAction("money", 10));
		processStep.add(new IncreaseXPAction(10));
		processStep.add(new SetQuestAction(QUEST_SLOT, 0, STATE_ILISA));

		// starting the conversation the first time after getting a flask.
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, 0, STATE_START),
						new PlayerHasItemWithHimCondition("flask")),
				ConversationStates.ATTENDING,
				TAD_TALK_GOT_FLASK + " " + TAD_TALK_REWARD_MONEY + " " + TAD_TALK_FLASK_ILISA,
				new MultipleActions(processStep));

		// player said hi with flask on ground then picked it up and said flask
		npc.add(ConversationStates.ATTENDING, "flask",
                new AndCondition(new QuestInStateCondition(QUEST_SLOT, 0, STATE_START), new PlayerHasItemWithHimCondition("flask")),
                ConversationStates.ATTENDING,
                TAD_TALK_GOT_FLASK + " " + TAD_TALK_REWARD_MONEY + " " + TAD_TALK_FLASK_ILISA,
                new MultipleActions(processStep));

		// remind the player to take the flask to Ilisa.
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, 0, STATE_ILISA),
						new PlayerHasItemWithHimCondition("flask")),
				ConversationStates.ATTENDING,
				TAD_TALK_GOT_FLASK + " " + TAD_TALK_FLASK_ILISA,
				null);

		// another reminder in case player says task again
        npc.add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES,
                new QuestInStateCondition(QUEST_SLOT, 0, STATE_ILISA),
                ConversationStates.ATTENDING,
                TAD_TALK_REMIND_FLASK_ILISA,
                null);

		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("ilisa", "iiisa", "llisa"),
				null,
				ConversationStates.ATTENDING,
				TAD_TALK_INTRODUCE_ILISA,
				null);
	}

	private void step_4() {
		final SpeakerNPC npc = npcs.get("Ilisa");

		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(
						new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, 0, STATE_ILISA),
						new NotCondition(new PlayerHasItemWithHimCondition("flask"))),
				ConversationStates.ATTENDING,
				ILISA_TALK_ASK_FOR_FLASK,
				null);

		final List<ChatAction> processStep = new LinkedList<ChatAction>();
		processStep.add(new DropItemAction("flask"));
		processStep.add(new IncreaseXPAction(10));
		processStep.add(new SetQuestAction(QUEST_SLOT, 0, STATE_HERB));

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(
						new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, 0, STATE_ILISA),
						new PlayerHasItemWithHimCondition("flask")),
				ConversationStates.ATTENDING,
				ILISA_TALK_ASK_FOR_HERB,
				new MultipleActions(processStep));

		ChatAction showArandulaDrawing = new ExamineChatAction("arandula.png", "Ilisa's drawing", "Arandula");
		ChatAction flagDrawingWasShown = new SetQuestAction(QUEST_SLOT, 1, STATE_SHOWN_DRAWING);
		npc.add(
				ConversationStates.ATTENDING,
				Arrays.asList("yes", "ok"),
				new AndCondition(
						new QuestInStateCondition(QUEST_SLOT, 0, STATE_HERB),
						new NotCondition(new QuestInStateCondition(QUEST_SLOT, 1, STATE_SHOWN_DRAWING)),
						new NotCondition(new PlayerHasItemWithHimCondition("arandula"))),
				ConversationStates.ATTENDING,
				ILISA_TALK_DESCRIBE_HERB,
				new MultipleActions(showArandulaDrawing, flagDrawingWasShown));

		npc.add(
				ConversationStates.ATTENDING,
				Arrays.asList("herb", "arandula"),
				new QuestStartedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				ILISA_TALK_DESCRIBE_HERB,
				new MultipleActions(showArandulaDrawing, flagDrawingWasShown));

		npc.add(
				ConversationStates.ATTENDING,
				"tad",
				null,
				ConversationStates.ATTENDING,
				ILISA_TALK_INTRODUCE_TAD,
				null);
	}

	private void step_5() {
		final SpeakerNPC npc = npcs.get("Ilisa");

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(
						new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, 0, STATE_HERB),
						new NotCondition(new PlayerHasItemWithHimCondition("arandula"))),
				ConversationStates.ATTENDING,
				ILISA_TALK_REMIND_HERB, null);

		final List<ChatAction> processStep = new LinkedList<ChatAction>();
		processStep.add(new DropItemAction("arandula"));
		processStep.add(new IncreaseXPAction(50));
        processStep.add(new IncreaseKarmaAction(4));
		processStep.add(new SetQuestAction(QUEST_SLOT, 0, STATE_POTION));

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(
						new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, 0, STATE_HERB),
						new PlayerHasItemWithHimCondition("arandula")),
				ConversationStates.ATTENDING,
				ILISA_TALK_PREPARE_MEDICINE,
				new MultipleActions(processStep));

		npc.add(ConversationStates.ATTENDING, Arrays.asList(STATE_POTION,
				"medicine"), null, ConversationStates.ATTENDING,
				ILISA_TALK_EXPLAIN_MEDICINE, null);
	}

	private void step_6() {
		SpeakerNPC npc = npcs.get("Tad");

        // another reminder in case player says task again
        npc.add(ConversationStates.ATTENDING,
        		ConversationPhrases.QUEST_MESSAGES,
                new QuestInStateCondition(QUEST_SLOT, 0, STATE_HERB),
                ConversationStates.ATTENDING,
                TAD_TALK_REMIND_MEDICINE,
                null);

		final List<ChatAction> processStep = new LinkedList<ChatAction>();
		processStep.add(new IncreaseXPAction(200));
		processStep.add(new SetQuestAction(QUEST_SLOT, 0, STATE_DONE));

		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(
						new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, 0, STATE_POTION)),
				ConversationStates.ATTENDING,
				TAD_TALK_COMPLETE_QUEST,
				new MultipleActions(processStep));

		/*
		 * if player has not finished this quest, ketteh will remind player about him.
		 * if player has not started, and not finished, ketteh will ask if player has met him.
		 */
		npc = npcs.get("Ketteh Wehoh");

        npc.add(ConversationStates.ATTENDING,
        		ConversationPhrases.GOODBYE_MESSAGES,
        		new AndCondition(
        				new QuestStartedCondition(QUEST_SLOT),
        				new QuestNotCompletedCondition(QUEST_SLOT)),
                ConversationStates.IDLE,
                KETTEH_TALK_BYE_REMINDS_OF_TAD,
                null);

        npc.add(ConversationStates.ATTENDING,
        		ConversationPhrases.GOODBYE_MESSAGES,
        		new QuestNotStartedCondition(QUEST_SLOT),
                ConversationStates.IDLE,
                KETTEH_TALK_BYE_INTRODUCES_TAD,
                null);

	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Medicine for Tad",
				"Tad, a boy in Semos Hostel, needs help to get his medicine.",
				false);
		step_1();
		step_2();
		step_3();
		step_4();
		step_5();
		step_6();
	}
	@Override
	public String getName() {
		return "MedicineForTad";
	}

	@Override
	public String getRegion() {
		return Region.SEMOS_CITY;
	}
	@Override
	public String getNPCName() {
		return "Tad";
	}
}
