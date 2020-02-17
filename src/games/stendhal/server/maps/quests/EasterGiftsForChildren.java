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
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Easter gifts for children
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Caroline who is working in her tavern in Ados city</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>Caroline wants to make children around Faiumoni happy with gifting easter baskets for them.</li>
 * <li>Players have to bring Caroline sweets like chocolate bars and chocolate eggs, as well as some fruit.</li>
 * <li>Children around Faiumoni will be happy with Carolines baskets.</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>100 XP</li>
 * <li>5 Ados city scrolls</li>
 * <li>2 home scrolls</li>
 * <li>Karma: 50</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>None</li>
 * </ul>
 */
public class EasterGiftsForChildren extends AbstractQuest {

	private static final String QUEST_SLOT = "easter_gifts_[year]";



	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("I talked to Caroline in Ados. She is working in her tavern there.");
		final String questState = player.getQuest(QUEST_SLOT);
		if ("rejected".equals(questState)) {
			res.add("She asked me to bring her some sweets but I rejected her request.");
		}
		if (player.isQuestInState(QUEST_SLOT, "start", "done")) {
			res.add("I promised to bring Caroline some sweets for children around Faiumoni as an Easter gift.");
		}
		if ("start".equals(questState) && player.isEquipped("chocolate bar", 5)  && player.isEquipped("small easter egg", 1) && player.isEquipped("apple", 5)  && player.isEquipped("cherry", 5) || "done".equals(questState)) {
			res.add("I got all the sweets and will take them to Caroline.");
		}
		if ("done".equals(questState)) {
			res.add("I took the sweets to Caroline. She gave me some nice Easter gifts for my travels as a real hero. :)");
		}
		return res;
	}

	private void prepareRequestingStep() {
		final SpeakerNPC npc = npcs.get("Caroline");

		npc.add(
			ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestNotCompletedCondition(QUEST_SLOT),
			ConversationStates.QUEST_OFFERED,
			"I need some help with packing Easter baskets for children around Faiumoni. I know that the bunny will meet them, but they are so lovely that I want to make them happy, too. Do you think you can help me?",
			null);

		npc.add(
			ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"Thank you very much for the sweets! I already gave all Easter baskets away to children around Faiumoni and they were really happy! :) Unfortunately I don't have any other task for you at the moment. Have wonderful Easter holidays!",
			null);

		// Player asks for quests after it is already started
		npc.add(
			ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestActiveCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"Did you forget? I already asked you to fetch me some #sweets",
			null);

		// player is willing to help
		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"I need some #sweets for my Easter baskets. If you get 5 #chocolate #bars, a #small #easter #egg, 5 #apples and 5 #cherries, I'll give you a nice Easter reward.",
			new SetQuestAction(QUEST_SLOT, "start"));

		// player is not willing to help
		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES, null,
			ConversationStates.ATTENDING,
			"Oh what a pity! Poor children will not receive wonderful baskets then. Maybe I'll find someone else and ask him or her for help.",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));

		// player wants to know what sweets she is referring to
		npc.add(
			ConversationStates.ATTENDING,
			Arrays.asList("sweets"),
			null,
			ConversationStates.ATTENDING,
			"There are lots of chocolate sweets around but I would also like to fill my basket with fruits as well.", null);

		// player wants to know where he can get this sweets from
		npc.add(
				ConversationStates.ATTENDING,
				Arrays.asList("chocolate bar", "chocolate bars", "chocolate"),
				null,
				ConversationStates.ATTENDING,
				"Chocolate bars are sold in taverns and I've heard that some evil children carry them, too. If you find some, remember that Elizabeth in Kirdneh loves chocolate, too. :)", null);

		npc.add(
				ConversationStates.ATTENDING,
				Arrays.asList("apple", "apples"),
				null,
				ConversationStates.ATTENDING,
				"Apples are found at the farm to the east of the city. They are really healthy and you can bake an awesome apple pie from these. You can also get one by Martha in Kalavan City gardens.", null);

		npc.add(
				ConversationStates.ATTENDING,
				Arrays.asList("cherry", "cherries"),
				null,
				ConversationStates.ATTENDING,
				"Old Mother Helena in Fado sells the most beautifully red cherries. They are really tasty! I hope you tasted the lovely cherry pie already, made by Gertha in Kalavan City gardens.", null);

		npc.add(
				ConversationStates.ATTENDING,
				Arrays.asList("small easter egg", "chocolate egg"),
				null,
				ConversationStates.ATTENDING,
				"Small easter eggs are a speciality of our Easter bunny friend who hops around during the Easter days. Maybe you will meet him on his way. :)", null);
	}

	private void prepareBringingStep() {
		final SpeakerNPC npc = npcs.get("Caroline");

		// player returns while quest is still active
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
				new QuestInStateCondition(QUEST_SLOT, "start"),
				new AndCondition(
					new PlayerHasItemWithHimCondition("chocolate bar", 5),
					new PlayerHasItemWithHimCondition("small easter egg",1),
					new PlayerHasItemWithHimCondition("apple", 5),
					new PlayerHasItemWithHimCondition("cherry", 5))),
			ConversationStates.QUEST_ITEM_BROUGHT,
			"Oh nice! I see you have delicious sweets with you. Are they for the Easter baskets which I'm currently preparing?",
			null);

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
				new QuestInStateCondition(QUEST_SLOT, "start"),
				new NotCondition(new AndCondition(
					new PlayerHasItemWithHimCondition("chocolate bar", 5),
					new PlayerHasItemWithHimCondition("small easter egg",1),
					new PlayerHasItemWithHimCondition("apple", 5),
					new PlayerHasItemWithHimCondition("cherry", 5)))),
			ConversationStates.ATTENDING,
			"Oh no. There are still some sweets missing which I need for my Easter baskets. Hope you can find them, soon...",
			null);

		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new DropItemAction("chocolate bar", 5));
		reward.add(new DropItemAction("small easter egg", 1));
		reward.add(new DropItemAction("apple", 5));
		reward.add(new DropItemAction("cherry",5));
		reward.add(new EquipItemAction("ados city scroll", 5));
		reward.add(new EquipItemAction("home scroll", 2));
		reward.add(new IncreaseXPAction(100));
		reward.add(new SetQuestAction(QUEST_SLOT, "done"));
		reward.add(new IncreaseKarmaAction(50));



		npc.add(
			ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.YES_MESSAGES,
			// make sure the player isn't cheating by putting the sweets
			// away and then saying "yes"

			new AndCondition(
					new PlayerHasItemWithHimCondition("chocolate bar", 5),
					new PlayerHasItemWithHimCondition("small easter egg", 1),
					new PlayerHasItemWithHimCondition("apple", 5),
					new PlayerHasItemWithHimCondition("cherry", 5)),

			ConversationStates.ATTENDING, "How great! Now I can fill these baskets for the children! They will be so happy! Thank you very much for your help and Happy Easter! Please take these scrolls for your effort. :)",
			new MultipleActions(reward));


		npc.add(
			ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"I hope you'll find some sweets for me before the Easter days passed and children will be sad.",
			null);
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Easter Gifts for Children",
				"Caroline, the nice tavern owner in Ados city, wants to make some children happy during Easter holidays.",
				false);
		prepareRequestingStep();
		prepareBringingStep();
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public String getName() {
		return "EasterGiftsForChildren";
	}

	@Override
	public int getMinLevel() {
		return 0;
	}

	@Override
	public String getRegion() {
		return Region.ADOS_CITY;
	}

	@Override
	public String getNPCName() {
		return "Caroline";
	}
}
