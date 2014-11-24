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
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * QUEST: Bows for Ouchit
 * 
 * PARTICIPANTS:
 * <ul>
 * <li> Ouchit, ranged items seller</li>
 * <li> Karl, farmer</li>
 * </ul>
 * 
 * STEPS:
 * <ul>
 * <li> Ouchit asks for wood for his bows and arrows. </li>
 * <li> Puchit asks you to fetch horse hair from Karl also.</li>
 * <li> Return and you get some equipment as reward.<li>
 * </ul>
 * 
 * REWARD:
 * <ul>
 * <li> 1 XP<li>
 * <li> Scale armor</li>
 * <li> Chain legs</li>
 * <li> Karma: 14<li>
 * </ul>
 * 
 * REPETITIONS:
 * <ul>
 * <li> None.</li>
 * </ul>
 */

public class BowsForOuchit extends AbstractQuest {

	public static final String QUEST_SLOT = "bows_ouchit";

	public void prepareQuestStep() {

		/*
		 * get a reference to the Ouchit NPC
		 */
		SpeakerNPC npc = npcs.get("Ouchit");

		/*
		 * Add a reply on the trigger phrase "quest" to Ouchit
		 */
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUESTION_1,
				"Are you here to help me a bit?",
				null);

		/*
		 * Player is interested in helping, so explain the quest.
		 */
		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED,
				"Good! I sell bows and arrows. It would be great if you could " +
				"bring me 10 pieces of #wood. Can you bring me the wood?",
				null);

		/*
		 * Player refused to help - end the conversation.
		 */
		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.NO_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.IDLE,
				"Oh ok, bye.",
				null);

		/*
		 * Player agreed to get wood, so tell them what they'll need to say
		 */
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Nice :-) Come back when you have them and say #wood.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "wood", 2.0));

		/*
		 * Player asks about wood.
		 */
		npc.add(ConversationStates.QUEST_OFFERED,
				"wood",
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED,
				"Wood is a great item with many purposes. Of course you will " +
				"find some pieces in a forest. Will you bring me 10 pieces?",
				null);

		/*
		 * Player refused to help - end the conversation.
		 */
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.IDLE,
				"Ok, you can come back later if you want. Bye for now.",
				null);
	}

	public void bringWoodStep() {

		/*
		 * get a reference to the Ouchit NPC
		 */
		SpeakerNPC npc = npcs.get("Ouchit");
		
		/*
		 * Player asks about quest, remind what they're doing
		 */
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT,"wood"),
				ConversationStates.ATTENDING,
				"I'm waiting for you to bring me 10 pieces of #wood.",
				null);
		
		/*
		 * Player asks about wood, but hasn't collected any - remind them.
		 */
		npc.add(ConversationStates.ATTENDING,
				"wood",
				new AndCondition(new QuestInStateCondition(QUEST_SLOT,"wood"),
								 new NotCondition (new PlayerHasItemWithHimCondition("wood",10))),
				ConversationStates.ATTENDING,
				"Wood is a great item with many purposes. Of course you will " +
				"find some pieces in a forest. Please remember to come back when you " +
				"have ten pieces for me, and say #wood.",
				null);

		/*
		 * Player asks about wood, and has collected some - take it and
ask for horse hair.
		 */
		npc.add(ConversationStates.ATTENDING,
				"wood",
				new AndCondition(new QuestInStateCondition(QUEST_SLOT,"wood"),
								new PlayerHasItemWithHimCondition("wood",10)),
				ConversationStates.ATTENDING,
				"Great, now I can make new arrows. But for the bows I need " +
				"bowstrings. Please go to #Karl. I know he has horses and if " +
				"you tell him my name he will give you #'horse hairs' from a horsetail.",
				new MultipleActions(new SetQuestAndModifyKarmaAction(QUEST_SLOT, "hair", 2.0), new DropItemAction("wood", 10)));

		/*
		 * For simplicity, respond to 'Karl' at any time.
		 */
		npc.addReply("Karl", "Karl is a farmer, east of Semos. He has many pets on his farm.");
	}

	public void getHairStep() {

		/*
		 * get a reference to the Karl NPC
		 */
		SpeakerNPC npc = npcs.get("Karl");

		npc.add(ConversationStates.ATTENDING,
				"Ouchit",
				new AndCondition(new QuestInStateCondition(QUEST_SLOT,"hair"),
								new NotCondition (new PlayerHasItemWithHimCondition("horse hair",1))),
				ConversationStates.ATTENDING,
				"Hello, hello! Ouchit needs more horse hairs from my horses? " +
				"No problem, here you are. Send Ouchit greetings from me.",
				new EquipItemAction("horse hair"));

	}

	public void bringHairStep() {

		/*
		 * get a reference to the Ouchit NPC
		 */
		SpeakerNPC npc = npcs.get("Ouchit");

		/*
		 * Player asks about quest, remind what they're doing
		 */
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT,"hair"),
				ConversationStates.ATTENDING,
				"I'm waiting for you to bring me some #'horse hairs'.",
				null);
		
		/*
		 * Player asks about horse hair, but hasn't collected any - remind them.
		 */
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("hair", "horse", "horse hairs"),
				new AndCondition(new QuestInStateCondition(QUEST_SLOT,"hair"),
								new NotCondition (new PlayerHasItemWithHimCondition("horse hair"))),
				ConversationStates.ATTENDING,
				"Horse hairs can be used as a bowstring. Please fetch me some from #Karl.",
				null);

		/*
		 * These actions are part of the reward
		 */
		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new DropItemAction("horse hair"));
		reward.add(new EquipItemAction("scale armor", 1, true));
		reward.add(new EquipItemAction("chain legs", 1, true));
		reward.add(new IncreaseXPAction(100));
		reward.add(new SetQuestAndModifyKarmaAction(QUEST_SLOT, "done", 10.0));
		
		/*
		 * Player asks about horse hair, and has collected some - take it
and ask for horse hair.
		 */
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("hair", "horse", "horse hairs"),
				new AndCondition(new QuestInStateCondition(QUEST_SLOT,"hair"),
								new PlayerHasItemWithHimCondition("horse hair")),
				ConversationStates.ATTENDING,
				"Yay, you got the horse hairs. Thanks a lot. Karl is really nice. Here, " +
				"take this for your work. Someone left it here and I don't need those things.",
				new MultipleActions(reward));
		
		/*
		 * Player asks about quest, and it is finished
		 */
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Thanks for your help. If I can #offer you anything just ask.",
				null);
		
	}

	@Override
	public void addToWorld() {
		prepareQuestStep();
		bringWoodStep();
		getHairStep();
		bringHairStep();
		fillQuestInfo(
				"Bows for Ouchit",
				"Ouchit is running out of bows and arrows to sell!",
				false);
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		final String questState = player.getQuest(QUEST_SLOT);
		res.add("Ouchit asked me for help to replenish his stocks of bows and arrows.");
		if (player.isQuestInState(QUEST_SLOT, "wood", "hair", "done")) {
			res.add("First I must fetch Ouchit 10 pieces of wood.");
		}
		if(player.isEquipped("wood", 10) && "wood".equals(questState)) {
			res.add("I've got the wood to take to Ouchit.");
		}
		if(player.isQuestInState(QUEST_SLOT, "hair", "done")) {
			res.add("Next I need to get some horse hairs, which Ouchit uses as bowstrings. I'm told the farmer Karl will help me.");
		}
		if(player.isEquipped("horse hair") && "hair".equals(questState) || isCompleted(player)) {
			res.add("Karl was kind and gave me some horse hairs.");
		}
		if (isCompleted(player)) {
			res.add("Ouchit gave me some new equipment as thanks for helping him.");
		}
		return res;
	}
	
	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public String getName() {
		return "BowsForOuchit";
	}
	
	@Override
	public int getMinLevel() {
		return 0;
	}
	
	@Override
	public String getRegion() {
		return Region.SEMOS_CITY;
	}

	@Override
	public String getNPCName() {
		return "Ouchit";
	}
}