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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.Rand;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.KilledCondition;
import games.stendhal.server.entity.npc.condition.LevelGreaterThanCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.npc.condition.TriggerInListCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: The Obsidian Knife.
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Alrak, a dwarf who abandoned the mountain dwarves to live in Kobold City</li>
 * <li>Ceryl, the librarian in Semos</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>Alrak is hungry and asks for 100 pieces of ham, cheese or meat</li>
 * <li>Then, Alrak is bored and asks for a book</li>
 * <li>Get the book from Ceryl, and remember the name of who it is for</li>
 * <li>Bring the book to Alrak - he reads it for 3 days</li>
 * <li>After 3 days Alrak has learned how to make a knife from obsidian</li>
 * <li>Provided you have high enough level, you can continue</li>
 * <li>Get obsidian for the blade and a fish for the fish bone handle</li>
 * <li>Alrak makes the knife for you</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>Obsidian Knife</li>
 * <li>11500 XP</li>
 * <li> lots of karma (total 85 + (5 | -5))
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>None</li>
 * </ul>
 */
public class ObsidianKnife extends AbstractQuest {

	// Please note, if you want to code a quest where you're asked to collect a number of some randomly picked item, like alrak asks you to initially,
	// please use StartRecordingRandomItemCollectionAction, SayRequiredItemAction, PlayerHasRecordedItemWithHimCondition and DropRecordedItemAction
    // This quest was written before they were available and you should not use it as a template.

	private static final int MINUTES_IN_DAYS = 24 * 60;

	private static final int REQUIRED_FOOD = 100;

	// Required level to move from the finished reading stage to
	// the offering knife stage
	private static final int REQUIRED_LEVEL = 50;

	private static final List<String> FOOD_LIST = Arrays.asList("ham", "meat", "cheese");

	private static final int REQUIRED_DAYS = 3;

	private static final int REQUIRED_MINUTES = 10;

	private static final String QUEST_SLOT = "obsidian_knife";

	private static final String FISH = "cod";

	private static final String NAME = "Alrak";

	private static Logger logger = Logger.getLogger(ObsidianKnife.class);

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("I have met Alrak the blacksmith in Wofol.");
		final String questState = player.getQuest(QUEST_SLOT);
		if (questState.equals("rejected")) {
			res.add("I do not want to help Alrak.");
			return res;
		}
		res.add("Alrak asked me to bring him some food.");
		if (player.isQuestInState(QUEST_SLOT, "ham", "meat", "cheese")) {
			res.add("I must fetch " + Grammar.quantityplnoun(REQUIRED_FOOD, questState) + ", and say " + questState + " when I return.");
			return res;
		}
		res.add("I took Alrak the food!");
		if (questState.equals("food_brought")) {
			return res;
		}
		res.add("I need to ask in a library about a gem book for Alrak.");
		if (questState.equals("seeking_book")) {
			return res;
		}
		res.add("I got the gem book Alrak wanted.");
		if (questState.equals("got_book")) {
			return res;
		}
		res.add("Alrak is reading the gem book I brought him.");
		if (questState.startsWith("reading")) {
			return res;
		}
		res.add("Alrak said that the book had taught him how to make a knife, it sounded pretty good.");
		if (questState.equals("book_read")) {
			return res;
		}
		res.add("Alrak says if I kill a black dragon and find a cod and an obsidian he will make me the knife.");
		if (questState.equals("knife_offered")
		&& !player.hasKilled("black dragon")) {
			return res;
		}
		res.add("I have killed a black dragon.");
		if (questState.equals("knife_offered")
				&& player.hasKilled("black dragon")) {
			return res;
		}
		res.add("I have got the cod and obsidian.");
		if (questState.equals("knife_offered")
				&& player.isEquipped("obsidian")
				&& player.isEquipped(FISH))  {
			return res;
		}
		res.add("I took the cod and obsidian to Alrak. Now he's forging my knife.");
		if (questState.startsWith("forging")) {
			return res;
		}
		res.add("I have my obsidian knife! It is awesome!");
		if (questState.equals("done")) {
			return res;
		}
		// if things have gone wrong and the quest state didn't match any of the above, debug a bit:
		final List<String> debug = new ArrayList<String>();
		debug.add("Quest state is: " + questState);
		logger.error("History doesn't have a matching quest state for " + questState);
		return debug;
	}

	private void prepareQuestOfferingStep() {
		final SpeakerNPC npc = npcs.get("Alrak");


		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_ITEM_QUESTION,
				"You know, it's hard to get food round here. I don't have any #supplies for next year.",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"I'm inspired to work again! I'm making things for Wrvil now. Thanks for getting me interested in forging again.",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, "food_brought"),
				ConversationStates.QUEST_ITEM_BROUGHT,
				"Now I'm less worried about food I've realised I'm bored. There's a #book I'd love to read.",
				null);

		// any other state than above
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestActiveCondition(QUEST_SLOT), new QuestNotInStateCondition(QUEST_SLOT, "food_brought")),
				ConversationStates.ATTENDING,
				"I'm sure I asked you to do something for me, already.",
				null);


		/*
		 * Player agrees to collect the food asked for. his quest slot gets set
		 * with it so that later when he returns and says the food name, Alrak
		 * can check if that was the food type he was asked to bring.
		 */
		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.ATTENDING, null,
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					final String food = player.getQuest(QUEST_SLOT);
					npc.say("Thank you! I hope it doesn't take too long to collect. Don't forget to say '"
						+ food + "' when you have it.");
					// player.setQuest(QUEST_SLOT, food);
					// set food to null?
				}
			});

		// Player says no. they might get asked to bring a different food next
		// time but they've lost karma.
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.IDLE,
				"I'm not sure how I'll survive next year now. Good bye, cruel soul.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));

		// Player asks what supplies he needs, and a random choice of what he
		// wants is made.
		npc.add(ConversationStates.QUEST_ITEM_QUESTION,
				"supplies",
				null,
				ConversationStates.QUEST_OFFERED,
				null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						final String food = Rand.rand(FOOD_LIST);
						player.setQuest(QUEST_SLOT, food);
						npc.say("If you could get me " + REQUIRED_FOOD
								+ " pieces of " + food
								+ ", I'd be in your debt. Will you help me?");
					}
				});
	}

	private void bringFoodStep() {
		final SpeakerNPC npc = npcs.get("Alrak");

		/** If player has quest and has brought the food, and says so, take it */
		for(final String itemName : FOOD_LIST) {
			final List<ChatAction> reward = new LinkedList<ChatAction>();
			reward.add(new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						if (player.drop(itemName, REQUIRED_FOOD)) {
							npc.say("Great! You brought the " + itemName + "!");
						}
					} });
			reward.add(new IncreaseXPAction(1000));
			reward.add(new IncreaseKarmaAction(35.0));
			reward.add(new SetQuestAction(QUEST_SLOT, "food_brought"));

			npc.add(ConversationStates.ATTENDING,
				itemName,
				new ChatCondition() {
					@Override
					public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
						return player.hasQuest(QUEST_SLOT)
								&& player.getQuest(QUEST_SLOT).equals(itemName)
								&& player.isEquipped(itemName, REQUIRED_FOOD);
					}
				},
				ConversationStates.ATTENDING,
				null,
				new MultipleActions(reward));
		}
	}

	private void requestBookStep() {
		final SpeakerNPC npc = npcs.get("Alrak");

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				"book",
				null,
				ConversationStates.QUEST_ITEM_BROUGHT,
				"It's about gems and minerals. I doubt you'd be interested ... but do you think you could get it somehow?",
				null);

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.IDLE,
				"Shame, I would really like to learn more about precious stones. Ah well, good bye.",
				null);

		npc.add(ConversationStates.QUEST_ITEM_BROUGHT,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Thanks. Try asking at a library for a 'gem book'.",
				new SetQuestAction(QUEST_SLOT, "seeking_book"));
	}

	private void getBookStep() {
		final SpeakerNPC npc = npcs.get("Ceryl");

		npc.add(ConversationStates.ATTENDING,
				"book",
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "seeking_book"), new QuestCompletedCondition("ceryl_book")),
				ConversationStates.ATTENDING,
				"Currently the #gem #book is quite popular...",
				null);

		npc.add(ConversationStates.ATTENDING,
				"gem book",
				new QuestInStateCondition(QUEST_SLOT, "seeking_book"),
				ConversationStates.QUESTION_1,
				"You're in luck! Ognir brought it back just last week. Now, who is it for?",
				null);

		npc.add(ConversationStates.QUESTION_1,
				NAME,
				null,
				ConversationStates.ATTENDING,
				"Ah, the mountain dwarf! Hope he enjoys the gem book.",
				new MultipleActions(new EquipItemAction("blue book", 1, true),
				new SetQuestAction(QUEST_SLOT, "got_book")));

		// allow to say goodbye while Ceryl is listening for the dwarf's name
		npc.add(ConversationStates.QUESTION_1, ConversationPhrases.GOODBYE_MESSAGES,
				null,
				ConversationStates.IDLE,
				"Bye.", null);

		// player says something which isn't the dwarf's name.
		npc.add(ConversationStates.QUESTION_1,
				"",
				new NotCondition(new TriggerInListCondition(NAME.toLowerCase())),
				ConversationStates.QUESTION_1,
				"Hm, you better check who it's really for.",
				null);
	}

	private void bringBookStep() {
		final SpeakerNPC npc = npcs.get("Alrak");
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, "got_book"),
						new PlayerHasItemWithHimCondition("blue book")),
				ConversationStates.IDLE,
				"Great! I think I'll read this for a while. Bye!",
				new MultipleActions(
						new DropItemAction("blue book"),
						new IncreaseXPAction(500),
						new SetQuestAction(QUEST_SLOT, "reading;"),
						new SetQuestToTimeStampAction(QUEST_SLOT, 1)));

		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new OrCondition(new QuestInStateCondition(QUEST_SLOT,"seeking_book"), new QuestInStateCondition(QUEST_SLOT, "got_book")),
						new NotCondition(new PlayerHasItemWithHimCondition("blue book"))),
				ConversationStates.ATTENDING,
				"Hello again. I hope you haven't forgotten about the gem book I wanted.",
				null);
	}

	private void offerKnifeStep() {
		final SpeakerNPC npc = npcs.get("Alrak");
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestStateStartsWithCondition(QUEST_SLOT, "reading;"),
						new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_DAYS * MINUTES_IN_DAYS))),
				ConversationStates.IDLE,
				null,
				new SayTimeRemainingAction(QUEST_SLOT, 1, REQUIRED_DAYS * MINUTES_IN_DAYS, "I haven't finished reading that book. Maybe I'll be done in"));

		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestStateStartsWithCondition(QUEST_SLOT, "reading;"),
						new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_DAYS * MINUTES_IN_DAYS)),
				ConversationStates.QUEST_2_OFFERED,
				"I've finished reading! That was really interesting. I learned how to make a special #knife from #obsidian.",
				new SetQuestAction(QUEST_SLOT, "book_read"));


		npc.add(ConversationStates.QUEST_2_OFFERED,
				"obsidian",
				new LevelGreaterThanCondition(REQUIRED_LEVEL),
				ConversationStates.QUEST_2_OFFERED,
				"That book says that the black gem, obsidian, can be used to make a very sharp cutting edge. Fascinating! If you slay a black dragon to bring it, I'll make a #knife for you.",
				new SetQuestAction(QUEST_SLOT, "knife_offered"));

		npc.add(ConversationStates.QUEST_2_OFFERED,
				"knife",
				new LevelGreaterThanCondition(REQUIRED_LEVEL),
				ConversationStates.QUEST_2_OFFERED,
				"I'll make an obsidian knife if you can slay a black dragon and get the gem which makes the blade. Bring a "
						+ FISH
						+ " so that I can make the bone handle, too.",
				new SetQuestAction(QUEST_SLOT, "knife_offered"));

		npc.add(ConversationStates.QUEST_2_OFFERED,
				Arrays.asList("obsidian", "knife"),
				new NotCondition(new LevelGreaterThanCondition(REQUIRED_LEVEL)),
				ConversationStates.ATTENDING,
				"Well, I don't think you're quite ready for such a dangerous weapon yet. How about you come back when you're above level " + Integer.toString(REQUIRED_LEVEL) + "?",
				null);

		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, "book_read")),
				ConversationStates.QUEST_2_OFFERED,
				"Hi! Perhaps you have come to ask about that #knife again ... ",
				null);

		// player says hi to NPC when equipped with the fish and the gem and
		// he's killed a black dragon
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, "knife_offered"),
						new KilledCondition("black dragon"),
						new PlayerHasItemWithHimCondition("obsidian"),
						new PlayerHasItemWithHimCondition(FISH)),
				ConversationStates.IDLE,
				"You found the gem for the blade and the fish bone to make the handle! I'll start work right away. Come back in "
				+ REQUIRED_MINUTES + " minutes.",
				new MultipleActions(
				new DropItemAction("obsidian"),
				new DropItemAction(FISH),
				new SetQuestAction(QUEST_SLOT, "forging;"),
				new SetQuestToTimeStampAction(QUEST_SLOT, 1)));

		// player says hi to NPC when equipped with the fish and the gem and
		// he's not killed a black dragon
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, "knife_offered"),
						new NotCondition(new KilledCondition("black dragon")),
						new PlayerHasItemWithHimCondition("obsidian"),
						new PlayerHasItemWithHimCondition(FISH)),
				ConversationStates.ATTENDING,
				"Didn't you hear me properly? I told you to go slay a black dragon for the obsidian, not buy it! How do I know this isn't a fake gem? *grumble* I'm not making a special knife for someone who is scared to face a dragon.",
				null);

		// player says hi to NPC when not equipped with the fish and the gem
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(QUEST_SLOT, "knife_offered"),
						new NotCondition(
								new AndCondition(
										new PlayerHasItemWithHimCondition("obsidian"),
										new PlayerHasItemWithHimCondition(FISH)))),
				ConversationStates.ATTENDING,
				"Hello again. Don't forget I offered to make that obsidian knife, if you bring me a "
					+ FISH
					+ " and a piece of obsidian from a black dragon you killed. In the meantime if I can #help you, just say the word.",
				null);

		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestStateStartsWithCondition(QUEST_SLOT, "forging;"),
						new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES))),
				ConversationStates.IDLE,
				null,
				new SayTimeRemainingAction(QUEST_SLOT, 1, REQUIRED_MINUTES, "I haven't finished making the knife. Please check back in"));

		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new IncreaseXPAction(10000));
		reward.add(new IncreaseKarmaAction(40));
		reward.add(new SetQuestAction(QUEST_SLOT, "done"));
		reward.add(new EquipItemAction("obsidian knife", 1, true));

		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestStateStartsWithCondition(QUEST_SLOT, "forging;"),
						new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES)),
				ConversationStates.IDLE,
				"The knife is ready! You know, that was enjoyable. I think I'll start making things again. Thanks!",
				new MultipleActions(reward));
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Obsidian Knife",
				"A skilled dwarf blacksmith in Wo'fol is getting hungry, and bored...",
				false);
		prepareQuestOfferingStep();
		bringFoodStep();
		requestBookStep();
		getBookStep();
		bringBookStep();
		offerKnifeStep();
	}

	@Override
	public String getName() {
		return "ObsidianKnife";
	}

	@Override
	public int getMinLevel() {
		return REQUIRED_LEVEL;
	}

	@Override
	public String getNPCName() {
		return "Alrak";
	}

	@Override
	public String getRegion() {
		return Region.SEMOS_MINES;
	}

}
