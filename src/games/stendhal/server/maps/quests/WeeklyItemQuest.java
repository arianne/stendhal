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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.MathHelper;
import games.stendhal.common.Rand;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropRecordedItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPDependentOnLevelAction;
import games.stendhal.server.entity.npc.action.IncrementQuestAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayRequiredItemAction;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.action.StartRecordingRandomItemCollectionAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.LevelGreaterThanCondition;
import games.stendhal.server.entity.npc.condition.LevelLessThanCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasRecordedItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Weekly Item Fetch Quest.
 * <p>
 * PARTICIPANTS:
 * <ul><li> Hazel, Museum Curator of Kirdneh
 * <li> some items
 * </ul>
 * STEPS:<ul>
 * <li> talk to Museum Curator to get a quest to fetch a rare item
 * <li> bring the item to the Museum Curator
 * <li> if you cannot bring it in 6 weeks she offers you the chance to fetch
 *
 * another instead </ul>
 *
 * REWARD:
 * <ul><li> xp
 * <li> between 100 and 600 money
 * <li> can buy kirdneh house if other eligibilities met
 * <li> 10 Karma
 * </ul>
 * REPETITIONS:
 * <ul><li> once a week</ul>
 */
public class WeeklyItemQuest extends AbstractQuest {

	/** the logger instance */
	private static final Logger logger = Logger.getLogger(WeeklyItemQuest.class);

	private static WeeklyItemQuest instance;

	private static final String QUEST_SLOT = "weekly_item";

	/** How long until the player can give up and start another quest */
	private static final int expireDelay = MathHelper.MINUTES_IN_ONE_WEEK * 6;

	/** How often the quest may be repeated */
	private static final int delay = MathHelper.MINUTES_IN_ONE_WEEK;

	/**
	 * All items which are hard enough to find but not tooo hard and not in Daily quest. If you want to do
	 * it better, go ahead. *
	 */
	private static final Map<String, Integer> items_easy = new HashMap<String, Integer>();
	private static final Map<String, Integer> items_med = new HashMap<String, Integer>();
	private static final Map<String, Integer> items_hard = new HashMap<String, Integer>();

	private static final int LEVEL_MED = 51;
	private static final int LEVEL_HARD = 151;


	/**
	 * Get the static instance.
	 *
	 * @return
	 * 		WeeklyItemQuest
	 */
	public static WeeklyItemQuest getInstance() {
		if (instance == null) {
			instance = new WeeklyItemQuest();
		}

		return instance;
	}

	private static void buildItemsMap() {

		/* Comments depict drop scoring (See: https://stendhalgame.org/wiki/StendhalItemsDropScoring)
		 * followed by lowest level creature that drops.
		 *
		 * Nothing below a certain score that is not obtainable by purchase
		 * or other repeatable means should be added.
		 *
		 * Current most rare not obtainable by other means: 0.097 (magic plate armor)
		 *
		 * Difficulty:
		 * - easy:   levels 0-50
		 * - medium: levels 51-150
		 * - hard:   levels 151+
		 */

		// armor (easy)
		addEasy("shadow armor", 1); // 450.59, 35 (purchasable)
		addEasy("stone armor", 1); // 53.63, 41
		// armor (medium)
		addMed("barbarian armor", 1); // 400.95, 37
		addMed("dwarvish armor", 1); // 163.15, 15
		addMed("golden armor", 1); // 536.32, 30
		addMed("magic plate armor", 1); // 0.097, 90 (maybe too rare, remove?)
		// armor (hard)
		addHard("chaos armor", 1); // 4.45, 70
		addHard("ice armor", 1); // 3.38, 45
		addHard("mainio armor", 1); // 1.33, 250
		addHard("xeno armor", 1); // 2.0, 170

		// axe (medium)
		addMed("golden twoside axe", 1); // 19.21, 60

		// boots (easy)
		addEasy("golden boots", 1); // 89.09, 25
		addEasy("steel boots", 1); // 315.17, 15
		addEasy("stone boots", 1); // 374.07, 32
		// boots (medium)
		addMed("chaos boots", 1); // 347.15, 62
		addMed("mainio boots", 1); // 6.99, 28
		addMed("shadow boots", 1); // 248.78, 41 (purchasable)
		// boots (hard)
		addHard("xeno boots", 1); // 3.33, 170

		// cloak (easy)
		addEasy("blue striped cloak", 1); // 39.38, 26 (purchasable)
		// cloak (medium)
		addMed("blue dragon cloak", 1); // 42.19, 70
		addMed("chaos cloak", 1); // 200.67, 74
		addMed("golden cloak", 1); // 71.0, 70
		addMed("magic cloak", 1); // 20.76, 114
		addMed("red dragon cloak", 1); // 13.86, 125
		addMed("shadow cloak", 1); // 93.14, 61 (purchasable)
		// cloak (hard)
		addHard("mainio cloak", 1); // 5.16, 44
		addHard("xeno cloak", 1); // 2.0, 170

		// club (easy)
		addEasy("skull staff", 1); // 81.22, 15
		addEasy("ugmash", 1); // 86.57, 16

		// drink (easy)
		addEasy("fish soup", 3); // n/a, n/a (producable)
		addEasy("mega potion", 5); // 1357.34, 51 (purchasable)

		// helmet (medium)
		addMed("golden helmet", 1); // 80.9, 30
		addMed("horned golden helmet", 1); // 25.98, 58
		addMed("mainio helmet", 1); // 3.47, 37
		addMed("shadow helmet", 1); // 27.26, 52 (purchasable)
		// helmet (hard)
		addHard("chaos helmet", 1); // 44.16, 75

		// jewellery (medium)
		addMed("diamond", 1); // 20.13, 48
		// jewellery (hard)
		addHard("obsidian", 1); // 4.17, 200

		// legs (medium)
		addMed("chaos legs", 1); // 123.2, 85
		addMed("dwarvish legs", 1); // 6.13, 67
		addMed("golden legs", 1); // 53.80, 25
		addMed("mainio legs", 1); // 13.97, 20
		addMed("shadow legs", 1); // 49.19, 55
		// legs (hard)
		addHard("xeno legs", 1); // 2.0, 170

		// misc (medium)
		addMed("giant heart", 5); // 2409.75, 110
		addMed("venom gland", 1); // 55.56, 120
		// misc (hard)
		addHard("unicorn horn", 5); // 398.33, 250

		// resource (medium)
		addMed("mithril bar", 1); // 0.0006, 90 (producable)
		addMed("mithril nugget", 1); // 0.16, 90 (harvestable)
		addMed("silk gland", 7); // 522.79, 110

		// ring (easy)
		addEasy("engagement ring", 1); // purchasable
		// ring (medium)
		addMed("medicinal ring", 1); // 78.26, 73

		// special (easy)
		addEasy("lucky charm", 1); // 76.37, 9
		// special (medium)
		addMed("mythical egg", 1); // n/a, n/a (quest reward)

		// shield (easy)
		addEasy("green dragon shield", 1); // 11.3, 50
		addEasy("shadow shield", 1); // 113.19, 36 (purchasable)
		// shield (medium)
		addMed("chaos shield", 1); // 165.18, 75
		addMed("golden shield", 1); // 21.2, 48
		addMed("magic plate shield", 1); // 23.31, 41
		addMed("mainio shield", 1); // 9.33, 102
		// shield (hard)
		addHard("xeno shield", 1); // 1.33, 170

		// sword (easy)
		addEasy("dark dagger", 1); // 6.95, 50 (acquired from well)
		addEasy("demon sword", 1); // 33.73, 42
		// sword (medium)
		addMed("assassin dagger", 1); // 235.12, 40
		addMed("buster", 1); // 2.26, 69
		addMed("chaos sword", 1); // 120.58, 43
		addMed("drow sword", 1); // 8.04, 58
		addMed("fire sword", 1); // 78.51, 50
		addMed("great sword", 1); // 29.99, 52
		addMed("hell dagger", 1); // 1.46, 42 (purchasable)
		addMed("ice sword", 1); // 3.28, 45
		addMed("night dagger", 1); // 30.9, 39
		// sword (hard)
		addHard("immortal sword", 1); // 0.17, 230

		// tool (medium)
		addMed("scroll eraser", 1); // 18.05, 55



		// add "easy" items to "medium" list
		for (final String key: items_easy.keySet()) {
			items_med.put(key, items_easy.get(key));
		}

		// add "medium" items to "hard" list
		for (final String key: items_med.keySet()) {
			items_hard.put(key, items_med.get(key));
		}
	}

	private static void addEasy(final String item, final int quant) {
		if (DailyItemQuest.utilizes(item)) {
			logger.warn("Not adding item already utilized in DailyItemQuest: " + item);
			return;
		}

		items_easy.put(item, quant);
	}

	private static void addMed(final String item, final int quant) {
		if (DailyItemQuest.utilizes(item)) {
			logger.warn("Not adding item already utilized in DailyItemQuest: " + item);
			return;
		}

		items_med.put(item, quant);
	}

	private static void addHard(final String item, final int quant) {
		if (DailyItemQuest.utilizes(item)) {
			logger.warn("Not adding item already utilized in DailyItemQuest: " + item);
			return;
		}

		items_hard.put(item, quant);
	}

	private ChatAction startQuestAction(final String level) {
		// common place to get the start quest actions as we can both starts it and abort and start again

		final Map<String, Integer> items;
		if (level.equals("easy")) {
			items = items_easy;
		} else if (level.equals("med") || level.equals("medium")) {
			items = items_med;
		} else {
			items = items_hard;
		}

		final List<ChatAction> actions = new LinkedList<ChatAction>();
		actions.add(new StartRecordingRandomItemCollectionAction(QUEST_SLOT, 0, items, "I want Kirdneh's museum to be the greatest in the land! Please fetch [item]"
				+ " and say #complete, once you've brought it."));
		actions.add(new SetQuestToTimeStampAction(QUEST_SLOT, 1));

		return new MultipleActions(actions);
	}

	private void getQuest() {
		final SpeakerNPC npc = npcs.get("Hazel");

		final ChatCondition startEasyCondition = new AndCondition(
				new LevelLessThanCondition(LEVEL_MED),
				new OrCondition(
						new QuestNotStartedCondition(QUEST_SLOT),
						new AndCondition(
								new QuestCompletedCondition(QUEST_SLOT),
								new TimePassedCondition(QUEST_SLOT,1,delay))));

		final ChatCondition startMedCondition = new AndCondition(
				new LevelGreaterThanCondition(LEVEL_MED - 1),
				new LevelLessThanCondition(LEVEL_HARD),
				new OrCondition(
						new QuestNotStartedCondition(QUEST_SLOT),
						new AndCondition(
								new QuestCompletedCondition(QUEST_SLOT),
								new TimePassedCondition(QUEST_SLOT,1,delay))));

		final ChatCondition startHardCondition = new AndCondition(
				new LevelGreaterThanCondition(LEVEL_HARD - 1),
				new OrCondition(
						new QuestNotStartedCondition(QUEST_SLOT),
						new AndCondition(
								new QuestCompletedCondition(QUEST_SLOT),
								new TimePassedCondition(QUEST_SLOT,1,delay))));


		npc.add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestActiveCondition(QUEST_SLOT),
								 new NotCondition(new TimePassedCondition(QUEST_SLOT,1,expireDelay))),
				ConversationStates.ATTENDING,
				null,
				new SayRequiredItemAction(QUEST_SLOT,0,"You're already on a quest to bring the museum [item]"
						+ ". Please say #complete if you have it with you."));

		npc.add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestActiveCondition(QUEST_SLOT),
								 new TimePassedCondition(QUEST_SLOT,1,expireDelay)),
				ConversationStates.ATTENDING,
				null,
				new SayRequiredItemAction(QUEST_SLOT,0,"You're already on a quest to bring the museum [item]"
						+ ". Please say #complete if you have it with you. But, perhaps that is now too rare an item. I can give you #another task, or you can return with what I first asked you."));

		npc.add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestCompletedCondition(QUEST_SLOT),
								 new NotCondition(new TimePassedCondition(QUEST_SLOT,1,delay))),
				ConversationStates.ATTENDING,
				null,
				new SayTimeRemainingAction(QUEST_SLOT,1, delay, "The museum can only afford to send you to fetch an item once a week. Please check back in"));

		// for players levels 50 & below
		npc.add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES,
				startEasyCondition,
				ConversationStates.ATTENDING,
				null,
				startQuestAction("easy"));

		// for players levels 51-150
		npc.add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES,
				startMedCondition,
				ConversationStates.ATTENDING,
				null,
				startQuestAction("med"));

		// for players levels 151+
		npc.add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES,
				startHardCondition,
				ConversationStates.ATTENDING,
				null,
				startQuestAction("hard"));
	}

	private void completeQuest() {
		final SpeakerNPC npc = npcs.get("Hazel");

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.FINISH_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"I don't remember giving you any #task yet.",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.FINISH_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"You already completed the last quest I had given to you.",
				null);

		final List<ChatAction> actions = new LinkedList<ChatAction>();
		actions.add(new DropRecordedItemAction(QUEST_SLOT,0));
		actions.add(new SetQuestToTimeStampAction(QUEST_SLOT, 1));
		actions.add(new IncrementQuestAction(QUEST_SLOT,2,1));
		actions.add(new SetQuestAction(QUEST_SLOT, 0, "done"));
		actions.add(new IncreaseXPDependentOnLevelAction(5.0/3.0, 290.0));
		actions.add(new IncreaseKarmaAction(10.0));
		actions.add(new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				int goldamount;
				final StackableItem money = (StackableItem) SingletonRepository.getEntityManager()
								.getItem("money");
				goldamount = 100 * Rand.roll1D6();
				money.setQuantity(goldamount);
				player.equipOrPutOnGround(money);
				raiser.say("Wonderful! Here is " + Integer.toString(goldamount) + " money to cover your expenses.");
			}
		});

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.FINISH_MESSAGES,
				new AndCondition(new QuestActiveCondition(QUEST_SLOT),
								 new PlayerHasRecordedItemWithHimCondition(QUEST_SLOT,0)),
				ConversationStates.ATTENDING,
				null,
				new MultipleActions(actions));

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.FINISH_MESSAGES,
				new AndCondition(new QuestActiveCondition(QUEST_SLOT),
								 new NotCondition(new PlayerHasRecordedItemWithHimCondition(QUEST_SLOT,0))),
				ConversationStates.ATTENDING,
				null,
				new SayRequiredItemAction(QUEST_SLOT,0,"You don't seem to have [item]"
						+ " with you. Please get it and say #complete only then."));

	}

	private void abortQuest() {
		final SpeakerNPC npc = npcs.get("Hazel");

		final ChatCondition startEasyCondition = new AndCondition(
				new LevelLessThanCondition(LEVEL_MED),
				new QuestActiveCondition(QUEST_SLOT),
				new TimePassedCondition(QUEST_SLOT,1,expireDelay));

		final ChatCondition startMedCondition = new AndCondition(
				new LevelGreaterThanCondition(LEVEL_MED - 1),
				new LevelLessThanCondition(LEVEL_HARD),
				new QuestActiveCondition(QUEST_SLOT),
				new TimePassedCondition(QUEST_SLOT,1,expireDelay));

		final ChatCondition startHardCondition = new AndCondition(
				new LevelGreaterThanCondition(LEVEL_HARD - 1),
				new QuestActiveCondition(QUEST_SLOT),
				new TimePassedCondition(QUEST_SLOT,1,expireDelay));


		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.ABORT_MESSAGES,
				startEasyCondition,
				ConversationStates.ATTENDING,
				null,
				startQuestAction("easy"));

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.ABORT_MESSAGES,
				startMedCondition,
				ConversationStates.ATTENDING,
				null,
				startQuestAction("med"));

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.ABORT_MESSAGES,
				startHardCondition,
				ConversationStates.ATTENDING,
				null,
				startQuestAction("hard"));

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.ABORT_MESSAGES,
				new AndCondition(new QuestActiveCondition(QUEST_SLOT),
						 		 new NotCondition(new TimePassedCondition(QUEST_SLOT,1,expireDelay))),
				ConversationStates.ATTENDING,
				"It hasn't been long since you've started your quest, you shouldn't give up so soon.",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.ABORT_MESSAGES,
				new QuestNotActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"I'm afraid I didn't send you on a #quest yet.",
				null);

	}

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
		res.add("I have met Hazel, the curator of Kirdneh museum.");
		final String questState = player.getQuest(QUEST_SLOT);
		if ("rejected".equals(questState)) {
			res.add("I do not want to help Kirdneh museum become the greatest in the land.");
			return res;
		}
		res.add("I want to help Kirdneh museum become the greatest in the land.");
		if (player.hasQuest(QUEST_SLOT) && !player.isQuestCompleted(QUEST_SLOT)) {
			String questItem = player.getRequiredItemName(QUEST_SLOT,0);
			int amount = player.getRequiredItemQuantity(QUEST_SLOT,0);
			if (!player.isEquipped(questItem, amount)) {
				res.add(String.format("I have been asked to find " +Grammar.quantityplnoun(amount, questItem, "a") + " for Kirdneh museum."));
			} else {
				res.add(String.format("I have " + Grammar.quantityplnoun(amount, questItem, "a") + " for Kirdneh museum and should deliver it to Hazel."));
			}
		}
		if (isRepeatable(player)) {
			res.add("I took the valuable item to Hazel and the museum can now afford to send me to find another.");
		} else if (isCompleted(player)) {
			res.add("I took the valuable item to Hazel within the last 7 days.");
		}
		// add to history how often player helped Hazel so far
		final int repetitions = player.getNumberOfRepetitions(getSlotName(), 2);
		if (repetitions > 0) {
			res.add("I've brought exhibits for the museum on "
					+ Grammar.quantityplnoun(repetitions, "occasion") + " so far.");
		}

		return res;
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Kirdneh Museum Needs Help!",
				"Hazel, the curator of the Kirdneh Museum, wants as many rare exhibits as she can afford.",
				true);
		buildItemsMap();

		getQuest();
		completeQuest();
		abortQuest();
	}

	@Override
	public String getName() {
		return "WeeklyItemQuest";
	}

	// the items requested are pretty hard to get, so it's not worth prompting player to go till they are higher level.
	@Override
	public int getMinLevel() {
		return 60;
	}

	@Override
	public boolean isRepeatable(final Player player) {
		return	new AndCondition(new QuestCompletedCondition(QUEST_SLOT),
						 new TimePassedCondition(QUEST_SLOT,1,delay)).fire(player, null, null);
	}

	@Override
	public String getRegion() {
		return Region.KIRDNEH;
	}

	@Override
	public String getNPCName() {
		return "Hazel";
	}
}
