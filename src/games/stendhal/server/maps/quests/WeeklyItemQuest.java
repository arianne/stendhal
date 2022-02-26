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

		// levels 0-50

		// armor (easy difficulty)
		addEasy("shadow armor", 1);
		addEasy("stone armor", 1);

		// boots (easy difficulty)
		addEasy("golden boots", 1);
		addEasy("steel boots", 1);
		addEasy("stone boots", 1);

		// cloak (easy difficulty)
		addEasy("blue striped cloak", 1);

		// club (easy difficulty)
		addEasy("skull staff", 1);
		addEasy("ugmash", 1);

		// drink (easy difficulty)
		addEasy("fish soup", 3);
		addEasy("mega potion", 5);

		// special (easy difficulty)
		addEasy("lucky charm", 1);

		// shield (easy difficulty)
		addEasy("green dragon shield", 1);
		addEasy("shadow shield", 1);

		// sword (easy difficulty)
		addEasy("dark dagger", 1);
		addEasy("demon sword", 1);


		// levels 51-150

		// armor (medium difficulty)
		addMed("barbarian armor", 1);
		addMed("dwarvish armor", 1);
		addMed("golden armor", 1);
		addMed("magic plate armor", 1);

		// axe (medium difficulty)
		addMed("golden twoside axe", 1);

		// boots (medium difficulty)
		addMed("chaos boots", 1);
		addMed("mainio boots", 1);
		addMed("shadow boots", 1);

		// cloak (medium difficulty)
		addMed("blue dragon cloak", 1);
		addMed("chaos cloak", 1);
		addMed("golden cloak", 1);
		addMed("red dragon cloak", 1);
		addMed("shadow cloak", 1);

		// helmet (medium difficulty)
		addMed("golden helmet", 1);
		addMed("horned golden helmet", 1);
		addMed("mainio helmet", 1);
		addMed("shadow helmet", 1);

		// jewellery (medium difficulty)
		addMed("diamond", 1);

		// legs (medium difficulty)
		addMed("chaos legs", 1);
		addMed("dwarvish legs", 1);
		addMed("golden legs", 1);
		addMed("mainio legs", 1);
		addMed("shadow legs", 1);

		// misc (medium difficulty)
		addMed("giant heart", 5);
		addMed("venom gland", 1);

		// resource (medium difficulty)
		addMed("mithril bar", 1);
		addMed("mithril nugget", 1);
		addMed("silk gland", 7);

		// ring (medium difficulty)
		addMed("medicinal ring", 1);

		// special (medium difficulty)
		addMed("mythical egg", 1);

		// shield (medium difficulty)
		addMed("chaos shield", 1);
		addMed("golden shield", 1);
		addMed("magic plate shield", 1);
		addMed("mainio shield", 1);

		// sword (medium difficulty)
		addMed("assassin dagger", 1);
		addMed("buster", 1);
		addMed("chaos sword", 1);
		addMed("drow sword", 1);
		addMed("fire sword", 1);
		addMed("great sword", 1);
		addMed("ice sword", 1);
		addMed("night dagger", 1);

		// tool (medium difficulty)
		addMed("scroll eraser", 1);


		// levels 151+

		// armor (hard difficulty)
		addHard("chaos armor", 1);
		addHard("ice armor", 1);
		addHard("mainio armor", 1);
		addHard("xeno armor", 1);

		// axe (hard difficulty)
		addHard("magic twoside axe", 1);

		// boots (hard difficulty)
		addHard("xeno boots", 1);

		// cloak (hard difficulty)
		addHard("magic cloak", 1);
		addHard("mainio cloak", 1);
		addHard("xeno cloak", 1);

		// helmet (hard difficulty)
		addHard("chaos helmet", 1);

		// jewellery (hard difficulty)
		addHard("obsidian", 1);

		// legs (hard difficulty)
		addHard("xeno legs", 1);

		// misc (hard difficulty)
		addHard("unicorn horn", 5);

		// shield (hard difficulty)
		addHard("xeno shield", 1);

		// sword (hard difficulty)
		addHard("hell dagger", 1);
		addHard("immortal sword", 1);
		addHard("xeno sword", 1);


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
