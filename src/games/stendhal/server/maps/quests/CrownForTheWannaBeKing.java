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
package games.stendhal.server.maps.quests;

import games.stendhal.common.Grammar;
import games.stendhal.common.NotificationType;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.CollectRequestedItemsAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayTextAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.ItemCollection;

import java.util.List;
import java.util.Map;

/**
 * QUEST: CrownForTheWannaBeKing
 *
 * PARTICIPANTS: 
 * <ul>
 * <li> Ivan Abe, the wannabe king who lives in Sedah</li>
 * <li> Kendra Mattori, priestess living in Magic City</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> Ivan Abe wants you to bring him 2 carbuncles, 2 diamonds, 4 emeralds, 2 gold bars, 1 obsidian, and 3 sapphires for his crown which he
 *      believes will help him to become the new king.</li>
 * <li> Kendra Mattori gives the reward after player brought all required items.</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li> 10,000 XP</li>
 * <li> Player's ATK XP is increased by 0.1% of his/her XP.</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li> None.</li>
 * </ul>
 */
public class CrownForTheWannaBeKing extends AbstractQuest {

	/**
	 * required items for the quest.
	 */
	protected static final String NEEDED_ITEMS = "gold bar=2;emerald=4;sapphire=3;carbuncle=2;diamond=2;obsidian=1";

	/**
	 * Name of the main NPC for this quest.
	 */
	private static final String NPC_NAME = "Ivan Abe";

	/**
	 * Name of the NPC giving the reward.
	 */
	private static final String REWARD_NPC_NAME = "Kendra Mattori";

	/**
	 * Name of the slot used for this quest.
	 */
	private static final String QUEST_SLOT = "crown_for_the_wannabe_king";

	/**
	 * how much ATK XP is given as the reward: formula is player's XP *
	 * ATK_BONUS_RATE ie. 0.001 = 0.1% of the player's XP
	 */
	private static final double ATK_REWARD_RATE = 0.001;

	/**
	 * how much XP is given as the reward.
	 */
	private static final int XP_REWARD = 10000;

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	
	/**
	 * initialize the introduction and start of the quest.
	 */
	private void step_1() {
		final SpeakerNPC npc = npcs.get(NPC_NAME);
		npc.addOffer("I don't sell anything!");
		npc.addGoodbye();
		npc.addJob("My current job is unimportant, I will be the king of Kalavan!");

		/* player says hi before starting the quest */
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Greetings. Be quick with your matters, I have a lot of work to do."
					+ " And next time clean your boots, you are lucky that I'm not the king...yet!",
				null);

		npc.addQuest("Hmm you could be useful for my #plan...");
		npc.addReply("plan",
					"Soon I will dethrone the king of Kalavan and become the new king! Right now I need myself a new #crown.");

		/* player says crown */
		npc.add(ConversationStates.ATTENDING,
				"crown",
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED,
				"Yes, I need jewels and gold for my new crown. Will you help me?",
				null);

		/* player says yes */
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.QUESTION_1, null, new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final EventRaiser entity) {
						player.setQuest(QUEST_SLOT, NEEDED_ITEMS);
						player.addKarma(5.0);
						entity.say("I want my crown to be beautiful and shiny. I need "
									+ Grammar.enumerateCollection(getMissingItems(player).toStringListWithHash())
									+ ". Do you have some of those now with you?");
					}
				});

		/* player is not willing to help */
		npc.add(ConversationStates.QUEST_OFFERED, 
				ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.IDLE, 
				"Oh you don't want to help me?! Get lost, you are wasting my precious time!",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));
	}

	/**
	 * Initializes the main part of the quest.
	 */
	private void step_2() {
		final SpeakerNPC npc = npcs.get(NPC_NAME);

		/* player returns while quest is still active */
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new QuestActiveCondition(QUEST_SLOT), new QuestNotInStateCondition(QUEST_SLOT, "reward")),
				ConversationStates.QUESTION_1,
				"Oh it's you again. Did you bring me any #items for my new crown?",
				null);

		/* player asks what exactly is missing (says items) */
		npc.add(ConversationStates.QUESTION_1, "items", null,
				ConversationStates.QUESTION_1, null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final EventRaiser entity) {
						final List<String> needed = getMissingItems(player).toStringListWithHash();
						entity.say("I need "
								+ Grammar.enumerateCollection(needed)
								+ ". Did you bring something?");
					}
				});

		/* player says he has a required item with him (says yes) */
		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.QUESTION_1, "Fine, what did you bring?",
				null);

		ChatAction completeAction = new  MultipleActions(
											new SetQuestAction(QUEST_SLOT, "reward"),
											new SayTextAction("You have served me well, my crown will be the mightiest of them all!"
											+ " Go to see "+ REWARD_NPC_NAME+ " in the Wizard City to get your #reward."),
											new IncreaseXPAction(XP_REWARD)
											);
		/* create the ChatAction used for item triggers */
		final ChatAction itemsChatAction = new CollectRequestedItemsAction(QUEST_SLOT, "Good, do you have anything else?",
																		"You have already brought that!", completeAction,
																		ConversationStates.ATTENDING);
		
		/* add triggers for the item names */
		final ItemCollection items = new ItemCollection();
		items.addFromQuestStateString(NEEDED_ITEMS);
		for (final Map.Entry<String, Integer> item : items.entrySet()) {
			npc.add(ConversationStates.QUESTION_1, item.getKey(), null,
					ConversationStates.QUESTION_1, null, itemsChatAction);
		}

		/* player says he didn't bring any items (says no) */
		npc.add(ConversationStates.ATTENDING, ConversationPhrases.NO_MESSAGES,
				new AndCondition(new QuestActiveCondition(QUEST_SLOT), new QuestNotInStateCondition(QUEST_SLOT, "reward")), 
				ConversationStates.IDLE,
				"Well don't come back before you find something for me!", null);

		/* player says he didn't bring any items to different question */
		npc.add(ConversationStates.QUESTION_1, ConversationPhrases.NO_MESSAGES,
				new AndCondition(new QuestActiveCondition(QUEST_SLOT), new QuestNotInStateCondition(QUEST_SLOT, "reward")), 
				ConversationStates.IDLE,
				"Farewell, come back after you have what I need!", null);


		/* player says reward */
		npc.add(ConversationStates.ATTENDING,
				"reward", null,
				ConversationStates.IDLE, "As I said, find priestess " + REWARD_NPC_NAME
					+ " in a temple at the city of wizards. She will give you your reward. Now go, I'm busy!",
				null);

		/*
		 * player returns after finishing the quest or before collecting the
		 * reward
		 */
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new OrCondition(new QuestCompletedCondition(QUEST_SLOT), new QuestInStateCondition(QUEST_SLOT, "reward")),
				ConversationStates.IDLE,
				"My new crown will be ready soon and I will dethrone the king! Mwahahaha!",
				null);
	}

	/**
	 * initialize the rewarding NPC.
	 */
	private void step_3() {
		final SpeakerNPC npc = npcs.get(REWARD_NPC_NAME);

		npc.add(ConversationStates.ATTENDING, "reward",
				new QuestInStateCondition(QUEST_SLOT, "reward"),
				ConversationStates.ATTENDING, null,
				new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final EventRaiser entity) {
						entity.say("Oh yes, "
									+ NPC_NAME
									+ " told me to reward you well! I hope you enjoy your increased combat abilities!");
						rewardPlayer(player);
						player.setQuest(QUEST_SLOT, "done");
					}
				});
	}

	/**
	 * Returns all items that the given player still has to bring to complete the quest.
	 *
	 * @param player The player doing the quest
	 * @return A list of item names
	 */
	private ItemCollection getMissingItems(final Player player) {
		final ItemCollection missingItems = new ItemCollection();

		missingItems.addFromQuestStateString(player.getQuest(QUEST_SLOT));

		return missingItems;
	}

	/**
	 * Give the player the reward for completing the quest.
	 *
	 * @param player
	 */
	protected void rewardPlayer(final Player player) {
		player.addKarma(10.0);
		player.setATKXP(player.getATKXP() + (int) (player.getXP() * ATK_REWARD_RATE));
		player.incATKXP();
		player.sendPrivateText(NotificationType.POSITIVE, "You gained " + Integer.toString((int) (player.getXP() * ATK_REWARD_RATE)) + " of attack experience points.");
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		step_1();
		step_2();
		step_3();
		fillQuestInfo(
				"Crown for the Wannabe King",
				"Ivan Abe wants to rule Kalavan ... and he needs a crown.",
				false);
	}

	@Override
	public String getName() {
		return "CrownForTheWannaBeKing";
	}
	
	@Override
	public int getMinLevel() {
		return 100;
	}
}
