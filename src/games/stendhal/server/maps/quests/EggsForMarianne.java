/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2019 - Stendhal                      *
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
import java.util.LinkedList;
import java.util.List;

import games.stendhal.common.Rand;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: EggsForMarianne
 *
 * PARTICIPANTS:
 * <ul>
 * <li> Marianne, a little girl looking for eggs</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> Marianne asks you for eggs for her pancakes</li>
 * <li> You collect a dozen of eggs from chickens</li>
 * <li> You give a dozen of eggs to Marianne.</li>
 * <li> Marianne gives you some flowers in return.<li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li> some pansy or daisies</li>
 * <li> 100 XP</li>
 * <li> Karma: 50</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li> Unlimited, at least 60 minutes have to elapse before repeating</li>
 * </ul>
 */
public class EggsForMarianne extends AbstractQuest {

	//a dozen of eggs
	private static final int REQUIRED_EGGS = 12;

	//60 minutes before quest can be repeated
	private static final int REQUIRED_MINUTES = 60;

	private static final String QUEST_SLOT = "eggs_for_marianne";

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public boolean isCompleted(final Player player) {
		return player.hasQuest(QUEST_SLOT) && !"start".equals(player.getQuest(QUEST_SLOT)) && !"rejected".equals(player.getQuest(QUEST_SLOT));
	}

	@Override
	public boolean isRepeatable(final Player player) {
		return new AndCondition(
				new QuestNotInStateCondition(QUEST_SLOT, "start"),
				new QuestStartedCondition(QUEST_SLOT),
				new TimePassedCondition(QUEST_SLOT,REQUIRED_MINUTES)).fire(player, null, null);
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("I have met Marianne");
		final String questState = player.getQuest(QUEST_SLOT);
		if ("rejected".equals(questState)) {
			res.add("I do not want to help Marianne");
			return res;
		}
		res.add("I do want to help Marianne");
		if (player.isEquipped("egg", REQUIRED_EGGS) || isCompleted(player)) {
			res.add("I have found some eggs for Marianne");
		}
		if (isCompleted(player)) {
			res.add("I have given Marianne the eggs. " +
		            "She gave me some flowers in return. " +
					"I also gained some xp!");
		}
		if(isRepeatable(player)){
			res.add("Marianne needs more eggs again.");
		}
		return res;
	}

	private void prepareRequestingStep() {
		final SpeakerNPC npc = npcs.get("Marianne");

		// player returns with the promised eggs
		npc.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(
					new GreetingMatchesNameCondition(npc.getName()),
					new QuestInStateCondition(QUEST_SLOT, "start"),
					new PlayerHasItemWithHimCondition("egg", REQUIRED_EGGS)),
			ConversationStates.QUEST_ITEM_BROUGHT,
			"Hi again! You've got several eggs I see; " +
			"Do you have as many eggs as I asked earlier?",
			null);

		//player returns without promised eggs
		npc.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestInStateCondition(QUEST_SLOT, "start"),
					new NotCondition(
							new PlayerHasItemWithHimCondition("egg", REQUIRED_EGGS))),
			ConversationStates.ATTENDING,
			"You're back already? " +
			"You promised to collect a dozen of eggs for me ... " +
			"But it does not seem that you carry enough eggs with you!",
			null);

		// first chat of player with Marianne
		npc.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestNotStartedCondition(QUEST_SLOT)),
			ConversationStates.ATTENDING, "Hello you ... " +
					                      "I need a little #favor if you please, you look so kind ... ",
			null);

		// player who is rejected or 'done' but waiting to start again, returns
		npc.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestNotInStateCondition(QUEST_SLOT, "start"),
					new QuestStartedCondition(QUEST_SLOT)),
			ConversationStates.ATTENDING,
			"Hello again, you!",
			null);

		// if they ask for quest while on it, remind them
		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestInStateCondition(QUEST_SLOT, "start"),
			ConversationStates.ATTENDING,
			"You already promised me to bring me some eggs, do you not remember?",
			null);

		// first time player asks/ player had rejected
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED,
				"I need a dozen of eggs. " +
				"My mom asked me to collect eggs and she is going to make me pancakes! " +
				"I'm afraid getting close to those chickens! " +
				"Could you please get eggs for me? " +
				"Remember .. I need a dozen of eggs ...",
				null);

		// player returns - enough time has passed
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(
						new QuestNotInStateCondition(QUEST_SLOT, "start"),
						new QuestStartedCondition(QUEST_SLOT),
						new TimePassedCondition(QUEST_SLOT,REQUIRED_MINUTES)),
				ConversationStates.QUEST_OFFERED,
				"My mom needs eggs again! " +
				"Could you please get more eggs for me? "+
				"A dozen of eggs will do ...",
				null);

		// player returns - enough time has passed
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestNotInStateCondition(QUEST_SLOT, "start"),
				new QuestStartedCondition(QUEST_SLOT),
				new NotCondition(new TimePassedCondition(QUEST_SLOT,REQUIRED_MINUTES))),
				ConversationStates.ATTENDING,
				null,
				new SayTimeRemainingAction(QUEST_SLOT,REQUIRED_MINUTES,
						"Thanks! " +
						"I think the eggs you already brought me " +
						"will be enough for another while ..."));

		// player is willing to help
		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Okay. " +
			"You can find eggs hunting chickens... I am so afraid of getting near those pesky chickens! " +
			"Please come back when you found enough eggs for me!",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "start", 2));

		// player is not willing to help
		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Oh dear, what am I going to do with all these flowers? " +
			"Perhaps I'll just leave them around some graves...",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));
	}

	private void prepareBringingStep() {
		final SpeakerNPC npc = npcs.get("Marianne");
		// player has eggs and tells Marianne, yes, it is for her

		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new DropItemAction("egg", REQUIRED_EGGS));
		reward.add(new IncreaseXPAction(50));
		reward.add(new SetQuestToTimeStampAction(QUEST_SLOT));
		reward.add(new IncreaseKarmaAction(50));
		reward.add(new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
				String rewardClass;
				if (Rand.throwCoin() == 1) {
					rewardClass = "pansy";
				} else {
					rewardClass = "daisies";
				}
				npc.say("Thank you! Here, take some " + rewardClass + "!");
				final StackableItem reward = (StackableItem) SingletonRepository.getEntityManager().getItem(rewardClass);
				reward.setQuantity(REQUIRED_EGGS);
				player.equipOrPutOnGround(reward);
				player.notifyWorldAboutChanges();
			}
		});

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.YES_MESSAGES,
			new PlayerHasItemWithHimCondition("egg", REQUIRED_EGGS),
			ConversationStates.ATTENDING, null,
			new MultipleActions(reward));

		//player said the eggs was for her but has dropped it from his bag or hands
		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.YES_MESSAGES,
			new NotCondition(new PlayerHasItemWithHimCondition("egg", REQUIRED_EGGS)),
			ConversationStates.ATTENDING,
			"Hey! Where did you put the eggs?",
			null);

		// player had eggs but said it is not for Marianne
		npc.add(
			ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Oh... well, I hope you find some quickly;" +
			"I'm getting hungry!",
			null);
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Eggs for Marianne",
				"Marianne's mom is going to make some pancakes and she needs some eggs.",
				true);
		prepareRequestingStep();
		prepareBringingStep();
	}

	@Override
	public String getName() {
		return "EggsForMarianne";
	}

	@Override
	public int getMinLevel() {
		return 0;
	}

	@Override
	public String getNPCName() {
		return "Marianne";
	}

	@Override
	public String getRegion() {
		return Region.DENIRAN;
	}
}
