/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
 * <li>Tad thanks you and asks you to take the flask to Ilisa
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
	private static final String QUEST_SLOT = "introduce_players";


	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (player.hasQuest("TadFirstChat")) {
			res.add("I have met Tad in Semos Hostel.");
		}
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		final String questState = player.getQuest(QUEST_SLOT);
		if (player.isQuestInState(QUEST_SLOT, "start", "ilisa", "corpse&herbs",
				"potion", "done")) {
			res.add("He asked me to buy a flask from Margaret in Semos Tavern.");
		}
		if ((questState.equals("start") && player.isEquipped("flask"))
				|| player.isQuestInState(QUEST_SLOT, "ilisa", "corpse&herbs",
						"potion", "done")) {
			res.add("I got a flask and will bring it to Tad soon.");
		}
		if (player.isQuestInState(QUEST_SLOT, "ilisa", "corpse&herbs",
				"potion", "done")) {
			res.add("Tad asked me to take the flask to Ilisa at Semos Temple.");
		}
		if (player.isQuestInState(QUEST_SLOT, "corpse&herbs", "potion", "done")) {
			res.add("Ilisa asked me to get a herb called Arandula which I can find north of Semos, near the tree grove.");
		}
		if ((questState.equals("corpse&herbs") && player.isEquipped("arandula"))
				|| player.isQuestInState(QUEST_SLOT, "potion", "done")) {
			res.add("I found some Arandula herbs and will bring them to Ilisa.");
		}
		if (player.isQuestInState(QUEST_SLOT, "potion", "done")) {
			res.add("Ilisa created a powerful potion to help Tad. She asked me to tell him that it is ready.");
		}
		if (questState.equals("done")) {
			res.add("Tad thanked me.");
		}
		return res;
	}

	private void step_1() {
		final SpeakerNPC npc = npcs.get("Tad");
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING, "I'm alright now, thanks.", null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED, 
				"I'm not feeling well... I need to get a bottle of medicine made. Can you fetch me an empty #flask?",
				null);

		/** In case Quest has already been completed */
		npc.add(ConversationStates.ATTENDING, "flask",
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"You've already helped me out! I'm feeling much better now.",
				null);

		/** If quest is not started yet, start it. */
		npc.add(ConversationStates.QUEST_OFFERED, "flask",
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED,
				"You could probably get a flask from #Margaret.", null);

		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.ATTENDING, 
				"Great! Please go as quickly as you can. *sneeze*",
				new SetQuestAction(QUEST_SLOT, "start"));

		npc.add(ConversationStates.QUEST_OFFERED, ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.ATTENDING,
				"Oh, please won't you change your mind? *sneeze*", null);

		npc.add(ConversationStates.QUEST_OFFERED,
				"margaret", null,
				ConversationStates.QUEST_OFFERED,
				"Margaret is the maid in the inn just down the street. So, will you help?",
				null);

		/** Remind player about the quest */
		npc.add(ConversationStates.ATTENDING,
				"flask",
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "start"), new NotCondition(new PlayerHasItemWithHimCondition("flask"))),
				ConversationStates.ATTENDING,
				"*cough* Oh dear... I really need this medicine! Please hurry back with the #flask from #Margaret.",
				null);

        /** Remind player about the quest */
        npc.add(ConversationStates.ATTENDING,
                ConversationPhrases.QUEST_MESSAGES,
                new QuestInStateCondition(QUEST_SLOT, "start"),
                ConversationStates.ATTENDING,
                "*cough* Oh dear... I really need this medicine! Please hurry back with the #flask from #Margaret.",
                null);

		npc.add(ConversationStates.ATTENDING, "margaret", null,
				ConversationStates.ATTENDING,
				"Margaret is the maid in the inn just down the street.", null);
	}

	private void step_2() {
		/** Just buy the stuff from Margaret. It isn't a quest */
	}

	private void step_3() {
		final SpeakerNPC npc = npcs.get("Tad");

		final List<ChatAction> processStep = new LinkedList<ChatAction>();
		processStep.add(new EquipItemAction("money", 10));
		processStep.add(new IncreaseXPAction(10));
		processStep.add(new SetQuestAction(QUEST_SLOT, "ilisa"));
		
		// starting the conversation the first time after getting a flask.
		// note Ilisa is spelled with a small i here because I
		// and l cannot be told apart in game
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, "start"),
						new PlayerHasItemWithHimCondition("flask")),
				ConversationStates.ATTENDING, 
				"Ok, you got the flask! Here take this money to cover your expense. Now, I need you to take it to #ilisa... she'll know what to do next.",
				new MultipleActions(processStep));

		// player said hi with flask on ground then picked it up and said flask
		npc.add(ConversationStates.ATTENDING, "flask",
                new AndCondition(new QuestInStateCondition(QUEST_SLOT, "start"), new PlayerHasItemWithHimCondition("flask")),
                ConversationStates.ATTENDING,
                "Ok, you got the flask! Here take this money to cover your expense. Now, I need you to take it to #ilisa... she'll know what to do next.",
                new MultipleActions(processStep));


		// remind the player to take the flask to ilisa.
		// note Ilisa is spelled with a small i here because I
		// and l cannot be told apart in game
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, "ilisa"),
						new PlayerHasItemWithHimCondition("flask")),
				ConversationStates.ATTENDING, 
				"Ok, you got the flask! Now, I need you to take it to #ilisa... she'll know what to do next.",
				null);

		// another reminder incase player says task again
        npc.add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES,
                new QuestInStateCondition(QUEST_SLOT, "ilisa"),
                ConversationStates.ATTENDING,
                "I need you to take a flask to #ilisa... she'll know what to do next.",
                null);

		npc.add(ConversationStates.ATTENDING, "ilisa", null,
				ConversationStates.ATTENDING,
				"Ilisa is the summon healer at Semos temple.", null);
	}

	private void step_4() {
		final SpeakerNPC npc = npcs.get("Ilisa");

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, "ilisa"),
						new NotCondition(new PlayerHasItemWithHimCondition("flask"))),
				ConversationStates.ATTENDING, 
				"Medicine for #Tad? Didn't he tell you to bring a flask?", null);

		final List<ChatAction> processStep = new LinkedList<ChatAction>();
		processStep.add(new DropItemAction("flask"));
		processStep.add(new IncreaseXPAction(10));
		processStep.add(new SetQuestAction(QUEST_SLOT, "corpse&herbs"));

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, "ilisa"),
						new PlayerHasItemWithHimCondition("flask")),
				ConversationStates.ATTENDING, 
				"Ah, I see you have that flask. #Tad needs medicine, right? Hmm... I'll need a #herb. Can you help?",
				new MultipleActions(processStep));

		npc.add(
				ConversationStates.ATTENDING,
				Arrays.asList("herb", "arandula", "yes", "ok"),
						new AndCondition(new QuestInStateCondition(QUEST_SLOT, "corpse&herbs"),
						new NotCondition(new PlayerHasItemWithHimCondition("arandula"))),
				ConversationStates.ATTENDING,
				"North of Semos, near the tree grove, grows a herb called arandula. Here is a picture I drew so you know what to look for.",
				new ExamineChatAction("arandula.png", "Ilisa's drawing", "Arandula"));

		npc.add(
				ConversationStates.ATTENDING,
				"tad",
				null,
				ConversationStates.ATTENDING,
				"He needs a very powerful potion to heal himself. He offers a good reward to anyone who will help him.",
				null);
	}

	private void step_5() {
		final SpeakerNPC npc = npcs.get("Ilisa");

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, "corpse&herbs"),
						new NotCondition(new PlayerHasItemWithHimCondition("arandula"))),
				ConversationStates.ATTENDING, 
				"Can you fetch those #herbs for the #medicine?", null);

		final List<ChatAction> processStep = new LinkedList<ChatAction>();
		processStep.add(new DropItemAction("arandula"));
		processStep.add(new IncreaseXPAction(50));
        processStep.add(new IncreaseKarmaAction(4));
		processStep.add(new SetQuestAction(QUEST_SLOT, "potion"));

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, "corpse&herbs"),
						new PlayerHasItemWithHimCondition("arandula")),
				ConversationStates.ATTENDING, 
				"Okay! Thank you. Now I will just mix these... a pinch of this... and a few drops... there! Can you ask #Tad to stop by and collect it? I want to see how he's doing.",
				new MultipleActions(processStep));

		npc.add(ConversationStates.ATTENDING, Arrays.asList("potion",
				"medicine"), null, ConversationStates.ATTENDING,
				"The medicine that #Tad is waiting for.", null);
	}

	private void step_6() {
		SpeakerNPC npc = npcs.get("Tad");

        // another reminder incase player says task again                                                                                                    
        npc.add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES,
                new QuestInStateCondition(QUEST_SLOT, "corpse&herbs"),
                ConversationStates.ATTENDING,
                "*cough* I hope #ilisa hurries with my medicine...",
                null);

		final List<ChatAction> processStep = new LinkedList<ChatAction>();
		processStep.add(new IncreaseXPAction(200));
		processStep.add(new SetQuestAction(QUEST_SLOT, "done"));
		
		// note Ilisa is spelled with a small i here because I
		// and l cannot be told apart in game
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, "potion")),
				ConversationStates.ATTENDING, "Thanks! I will go talk with #ilisa as soon as possible.",
				new MultipleActions(processStep));
	
		/*
		 * if player has not finished this quest, ketteh will remind player about him.
		 * if player has not started, and not finished, ketteh will ask if player has met him.
		 */
		npc = npcs.get("Ketteh Wehoh");

        npc.add(ConversationStates.ATTENDING, 
        		ConversationPhrases.GOODBYE_MESSAGES,
        		new AndCondition(new QuestStartedCondition(QUEST_SLOT),
        					     new QuestNotCompletedCondition(QUEST_SLOT)),
                ConversationStates.IDLE,
                "Goodbye. Don't forget to check on Tad. I hope he's feeling better.",
                null);

        npc.add(ConversationStates.ATTENDING, 
        		ConversationPhrases.GOODBYE_MESSAGES,
        		new QuestNotStartedCondition(QUEST_SLOT),
                ConversationStates.IDLE,
                "Farewell. Have you met Tad, in the hostel? If you get a chance, please check in on him. I heard he was not feeling well. You can find the hostel in Semos village, close to Nishiya.",
                null);

	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		fillQuestInfo(
				"Medicine For Tad",
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
