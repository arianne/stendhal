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

import games.stendhal.common.MathHelper;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.IRPZone;

/**
 * QUEST: FishSoupForHughie
 * 
 * PARTICIPANTS:
 * <ul>
 * <li> Anastasia, a worried mother in Ados farmhouse</li>
 * <li> Hughie, her son</li>
 * </ul>
 * 
 * STEPS:
 * <ul>
 * <li> Anastasia asks for some fish soup for her sick boy</li>
 * <li> You collect the fish soup</li>
 * <li> You give the fish soup to Anastasia.</li>
 * <li> Anastasia rewards you.<li>
 * </ul>
 * 
 * REWARD:
 * <ul> 
 * <li> 10 potions</li>
 * <li> xp </li>
 * <li> Karma: 5</li>
 * </ul>
 * 
 * REPETITIONS: 
 * <ul>
 * <li> Unlimited, but 7 days of waiting are required between repetitions</li>
 * </ul>
 */
public class FishSoupForHughie extends AbstractQuest {

	private static final int REQUIRED_MINUTES = 7 * MathHelper.MINUTES_IN_ONE_DAY;

	private static final String QUEST_SLOT = "fishsoup_for_hughie";

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
		return new AndCondition(new QuestNotInStateCondition(QUEST_SLOT, "start"), new QuestStartedCondition(QUEST_SLOT), new TimePassedCondition(QUEST_SLOT,REQUIRED_MINUTES)).fire(player, null, null);
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("Anastasia asked me to bring fish soup for her boy Hughie.");
		final String questState = player.getQuest(QUEST_SLOT);
		if ("rejected".equals(questState)) {
			res.add("I do not want to help Hughie.");
			return res;
		}
		res.add("I do want to help Hughie and Anastasia.");
		if ((player.isEquipped("fish soup")) || isCompleted(player)) {
			res.add("I have fetched the fish soup needed to heal Hughie.");
		}
		if (isCompleted(player)) {
			res.add("Hughie ate his soup and Anastasia gave me potions.");
		}
		if(isRepeatable(player)){
			res.add("Its been a while since I checked on Hughie and Anastasia, I should remember to go see them again.");
		} 
		return res;
	}



	private void prepareRequestingStep() {
		final SpeakerNPC npc = npcs.get("Anastasia");

		// player returns with the promised fish soup
		npc.add(ConversationStates.IDLE, 
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestInStateCondition(QUEST_SLOT, "start"), new PlayerHasItemWithHimCondition("fish soup")),
			ConversationStates.QUEST_ITEM_BROUGHT, 
			"Hi, you've got fish soup, I see, is that for Hughie?",
			null);

		//player returns without promised fish soup
		npc.add(ConversationStates.IDLE, 
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestInStateCondition(QUEST_SLOT, "start"), new NotCondition(new PlayerHasItemWithHimCondition("fish soup"))),
			ConversationStates.ATTENDING, 
			"You're back already? Hughie is getting sicker! Don't forget the fish soup for him, please. I promise to reward you.",
			null);

		// first chat of player with Anastasia
		npc.add(ConversationStates.IDLE, 
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestNotStartedCondition(QUEST_SLOT)),
			ConversationStates.ATTENDING, "Hi, I really could do with a #favor, please.",
			null);

		// player who is rejected or 'done' but waiting to start again, returns
		npc.add(ConversationStates.IDLE, 
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestNotInStateCondition(QUEST_SLOT, "start"),
					new QuestStartedCondition(QUEST_SLOT)),
			ConversationStates.ATTENDING,
			"Hello again.", 
			null);
		
		// if they ask for quest while on it, remind them
		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES, 
			new QuestInStateCondition(QUEST_SLOT, "start"),
			ConversationStates.ATTENDING,
			"You already promised me to bring me some fish soup for Hughie! Please hurry!",
			null);

		// first time player asks/ player had rejected
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED, 
				"My poor boy is sick and the potions I give him aren't working! Please could you fetch him some fish soup?",
				null);
		
		// player returns - enough time has passed
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestNotInStateCondition(QUEST_SLOT, "start"), new QuestStartedCondition(QUEST_SLOT), new TimePassedCondition(QUEST_SLOT,REQUIRED_MINUTES)),
				ConversationStates.QUEST_OFFERED, 
				"My Hughie is getting sick again! Please could you bring another bowl of fish soup? It helped last time.",
				null);

		// player returns - not enough time has passed
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestNotInStateCondition(QUEST_SLOT, "start"), new QuestStartedCondition(QUEST_SLOT), new NotCondition(new TimePassedCondition(QUEST_SLOT,REQUIRED_MINUTES))),
				ConversationStates.ATTENDING, 
				"Hughie is sleeping off his fever now and I'm hopeful he recovers. Thank you so much.",
				null);
		
		// player is willing to help
		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Thank you! You can ask Florence Bouillabaisse to make you fish soup. I think she's in Ados market somewhere.",
			new SetQuestAction(QUEST_SLOT, "start"));

		// player is not willing to help
		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Oh no, please, he's so sick.",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));
	}

	private void prepareBringingStep() {
		final SpeakerNPC npc = npcs.get("Anastasia");
		// player has fish soup and tells Anastasia, yes, it is for her
		
		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new DropItemAction("fish soup"));
		reward.add(new IncreaseXPAction(200));
		reward.add(new SetQuestToTimeStampAction(QUEST_SLOT));
		reward.add(new IncreaseKarmaAction(5));
		reward.add(new EquipItemAction("potion",10));
		reward.add(new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
				final Item soup = SingletonRepository.getEntityManager()
				.getItem("fish soup");
				final IRPZone zone = SingletonRepository.getRPWorld().getZone("int_ados_farm_house_1");
				// place on table 
				soup.setPosition(32, 5);
				// only allow Hughie, our npc, to eat the soup
				soup.setBoundTo("Hughie");
				zone.add(soup);
			}
		});
		
		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.YES_MESSAGES, 
			new PlayerHasItemWithHimCondition("fish soup"),
			ConversationStates.ATTENDING, "Thank you! I will always be in your favour. I will feed it to Hughie when he wakes. Please take these potions, they did nothing for him.",
			new MultipleActions(reward));

		//player said the fish soup was for her but has dropped it from his bag or hands
		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.YES_MESSAGES, 
			new NotCondition(new PlayerHasItemWithHimCondition("fish soup")),
			ConversationStates.ATTENDING, 
			"Oh! Where did you put the fish soup?",
			null);

		// player had fish soup but said it is not for Hughie
		npc.add(
			ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Oh...but my poor boy ... ",
			null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		fillQuestInfo(
				"Fish Soup For Hughie", 
				"Anastasia's son Hughie is sick and needs something to heal him.", 
				true);
		prepareRequestingStep();
		prepareBringingStep();
	}

	@Override
	public String getName() {
		return "Fish Soup For Hughie";
	}
	
	@Override
	public int getMinLevel() {
		return 10;
	}

	@Override
	public String getNPCName() {
		return "Anastasia";
	}
	
	@Override
	public String getRegion() {
		return Region.ADOS_SURROUNDS;
	}
}
