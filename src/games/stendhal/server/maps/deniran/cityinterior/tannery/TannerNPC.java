/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.deniran.cityinterior.tannery;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.common.MathHelper;
import games.stendhal.common.constants.SkinColor;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rp.DaylightPhase;
import games.stendhal.server.core.rp.StendhalQuestSystem;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.EnableFeatureAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.DaylightCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerLootedNumberOfItemsCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.server.maps.quests.AbstractQuest;
import games.stendhal.server.maps.quests.IQuest;
import games.stendhal.server.util.TimeUtil;

public class TannerNPC implements ZoneConfigurator {

	private static TannerNPC instance;

	private static SpeakerNPC tanner;

	private final static String FEATURE_SLOT = "pouch";
	private final static String QUEST_SLOT = "money_" + FEATURE_SLOT;
	private final static String QUEST_NAME = "Money Pouch";

	// players must have looted at least 1,000,000 money to get the money pouch
	private static final int requiredMoneyLoot = 100000;
	private static final int serviceFee = 50000;
	private static final int TAN_TIME = MathHelper.MINUTES_IN_ONE_DAY;
	// required items to make pouch
	private static final Map<String, Integer> requiredItems = new LinkedHashMap<String, Integer>() {{
		put("leather needle", 1);
		put("leather thread", 2);
		put("pelt", 1);
	}};


	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		prepareNPC(zone);
		prepareDialogue();
		prepareTravelLog();
	}

	private void prepareNPC(final StendhalRPZone zone) {
		tanner = new SpeakerNPC("Skinner Rawhide") {

			/**
			 * Force NPC to face north after speaking with players.
			 */
			@Override
			public void setAttending(final RPEntity rpentity) {
				super.setAttending(rpentity);
				if (rpentity == null) {
					setDirection(Direction.UP);
				}
			}
		};

		tanner.addGoodbye();

		tanner.setPosition(10, 8);
		tanner.setDirection(Direction.UP);
		tanner.setOutfit("body=0,head=0,dress=61,hair=25");
		tanner.setOutfitColor("skin", SkinColor.DARK);

		zone.add(tanner);
	}

	/**
	 * Creates a quest to acquire the money pouch.
	 *
	 * Reward:
	 * - new slot to carry money in
	 * - 100 karma
	 *
	 * Notes:
	 * - Players can only talk to Skinner during daytime.
	 */
	private void prepareDialogue() {
		// conditions to check if it is night or day time
		final ChatCondition nightCondition = new DaylightCondition(DaylightPhase.NIGHT);
		final ChatCondition dayCondition = new NotCondition(nightCondition);
		// required items to begin tanning
		final ChatCondition hasItemsCondition = new ChatCondition() {
			@Override
			public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
				for (final String itemName: requiredItems.keySet()) {
					if (!player.isEquipped(itemName, requiredItems.get(itemName))) {
						return false;
					}
				}

				if (!player.isEquipped("money", serviceFee)) {
					return false;
				}

				return true;
			}
		};
		// player can start quest if they have looted 1,000,000 money & they have not already started or finished the quest
		final ChatCondition canStartQuestCondition = new AndCondition(
				new QuestNotStartedCondition(QUEST_SLOT),
				new QuestNotCompletedCondition(QUEST_SLOT),
				new PlayerLootedNumberOfItemsCondition(requiredMoneyLoot, "money"));
		// condition to check if tanner is making the money pouch
		final ChatCondition isTanningCondition = new AndCondition(
				new QuestActiveCondition(QUEST_SLOT),
				new QuestNotInStateCondition(QUEST_SLOT, "start"));
		// action to give items & start
		final ChatAction startAction = new ChatAction() {
			@Override
			public void fire(Player player, Sentence sentence, EventRaiser npc) {
				for (final String itemName: requiredItems.keySet()) {
					player.drop(itemName, requiredItems.get(itemName));
				}

				player.drop("money", serviceFee);
			}
		};


		// cannot talk to tanner at night
		tanner.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				nightCondition,
				ConversationStates.IDLE,
				"It's late. I need to get to bed. Please come back in the morning.",
				null);

		// player has not met requirement
		tanner.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(
						dayCondition,
						new QuestNotActiveCondition(QUEST_SLOT),
						new QuestNotCompletedCondition(QUEST_SLOT),
						new NotCondition(new PlayerLootedNumberOfItemsCondition(requiredMoneyLoot, "money"))),
				ConversationStates.IDLE,
				"Welcome to Deniran's tannery.",
				null);

		// player has met requirement & can get money pouch
		tanner.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(
						dayCondition,
						canStartQuestCondition),
				ConversationStates.QUESTION_1,
				"I see you are experienced at looting money. I can make a pouch for you to carry your money in."
				+ " But I will need some items. Are you interested?",
				null);

		// player wants money pouch
		tanner.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				new QuestNotInStateCondition(QUEST_SLOT, "start"),
				ConversationStates.ATTENDING,
				sayRequiredItems("Okay. I will need [items]. Also, my fee is " + Integer.toString(serviceFee) + " money."
				+ " Please come back when you have that.", false),
				new SetQuestAction(QUEST_SLOT, "start"));

		// player does not want money pouch
		tanner.add(ConversationStates.QUESTION_1,
				ConversationPhrases.NO_MESSAGES,
				new QuestNotInStateCondition(QUEST_SLOT, "start"),
				ConversationStates.IDLE,
				"Oh? I think it would be encouraged.",
				null);

		// player returns without items
		tanner.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(
						dayCondition,
						new QuestInStateCondition(QUEST_SLOT, "start"),
						new NotCondition(hasItemsCondition)),
				ConversationStates.ATTENDING,
				sayRequiredItems("Bring me [items] and I will make a pouch to carry your money in.", true),
				null);

		// player returns with items
		tanner.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(
						dayCondition,
						new QuestInStateCondition(QUEST_SLOT, "start"),
						hasItemsCondition),
				ConversationStates.QUESTION_1,
				"Ah, you found the items to make the pouch. Would you like me to begin?",
				null);

		tanner.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, "start"),
				ConversationStates.IDLE,
				null,
				new MultipleActions(
						startAction,
						new SetQuestToTimeStampAction(QUEST_SLOT),
						new SayTimeRemainingAction(QUEST_SLOT, TAN_TIME, "Okay, I will begin making your money pouch. Please come back in ")));

		tanner.add(ConversationStates.QUESTION_1,
				ConversationPhrases.NO_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT, "start"),
				ConversationStates.IDLE,
				"Really? Okay then. See me again if you change your mind.",
				null);

		// player returns before money pouch is finished
		tanner.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(
						dayCondition,
						isTanningCondition,
						new NotCondition(new TimePassedCondition(QUEST_SLOT, TAN_TIME))),
				ConversationStates.IDLE,
				null,
				new SayTimeRemainingAction(QUEST_SLOT, TAN_TIME, "I'm sorry, your money pouch is not ready yet. Please come back in "));

		// money pouch is finished
		tanner.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(
						dayCondition,
						isTanningCondition,
						new TimePassedCondition(QUEST_SLOT, TAN_TIME)),
				ConversationStates.IDLE,
				"You came back just in time. Your money pouch is ready. Try it out. I know you will like it.",
				new MultipleActions(
						new EnableFeatureAction("pouch"),
						new IncreaseKarmaAction(100.0),
						new SetQuestAction(QUEST_SLOT, "done")));

		// player speaks to tanner after completing quest
		tanner.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(
						dayCondition,
						new QuestCompletedCondition(QUEST_SLOT)),
				ConversationStates.IDLE,
				"I knew you would enjoy the pouch.",
				null);


		// keyword responses

		final Map<String, String> responses = new HashMap<String, String>() {{
			put("leather needle", "I'm sure I had one around here somewhere.");
			put("leather thread", "Leather thread can be made by cutting up a #pelt. You will need a #'rotary cutter'.");
			put("pelt", "Sometimes you can get pelts off of animals that drop them.");
			put("rotary cutter", "I seem to have misplaced mine. Perhaps you could borrow one from somebody else. They are even used for slicing pizza"
					+ ", so ask around in places that make pizza if you can't find one anywhere else.");
		}};

		for (final String res: responses.keySet()) {
			tanner.add(ConversationStates.ATTENDING,
					res,
					new QuestInStateCondition(QUEST_SLOT, "start"),
					ConversationStates.ATTENDING,
					responses.get(res),
					null);
		}
	}

	/**
	 * Creates a quest entry in the player's travel log.
	 */
	private void prepareTravelLog() {
		final IQuest quest = new AbstractQuest() {
			@Override
			public List<String> getHistory(final Player player) {
				final List<String> res = new LinkedList<String>();
				final String questState = player.getQuest(QUEST_SLOT);

				if (questState == null) {
					return res;
				}

				final String tannerName = tanner.getName();

				res.add(tannerName + " will make a money pouch for me if I bring him some materials.");

				if (questState.equals("start")) {
					for (final String itemName: requiredItems.keySet()) {
						final int quantity = requiredItems.get(itemName);
						if (player.isEquipped(itemName, quantity)) {
							res.add("I have the " + itemName + ".");
						} else {
							res.add("I still need to find " + Integer.toString(quantity) + " " + itemName);
						}
					}
					if (player.isEquipped("money", serviceFee)) {
						res.add("I have enough money to pay the service fee.");
					} else {
						res.add("I need more money to pay the service fee.");
					}
				}

				if (!questState.equals("start") && !questState.equals("done")) {
					if (new TimePassedCondition(QUEST_SLOT, TAN_TIME).fire(player, null, null)) {
						res.add(tannerName + " has finished making my money pouch.");
					} else {
						try {
							final long timeRemains = Long.parseLong(questState) + (TAN_TIME * MathHelper.MILLISECONDS_IN_ONE_MINUTE) - System.currentTimeMillis();
							final int secondsRemain = (int) (timeRemains / 1000L);

							res.add(tannerName + " is making my money pouch. He will be done in "
									+ TimeUtil.approxTimeUntil(secondsRemain) + ".");
						} catch (NumberFormatException e) {
							res.add(tannerName + " is making my money pouch.");
						}
					}
				}

				if (questState.equals("done")) {
					res.add("I can now carry money in my pouch." );
				}

				return res;
			}

			@Override
			public String getSlotName() {
				return QUEST_SLOT;
			}

			@Override
			public void addToWorld() {
				fillQuestInfo(
						QUEST_NAME,
						tanner.getName() + " can make a pouch to carry money in.",
						isRepeatable(null));
			}

			@Override
			public String getName() {
				return QUEST_NAME.replace(" ", "");
			}

			@Override
			public String getRegion() {
				return Region.DENIRAN;
			}

			@Override
			public String getNPCName() {
				return tanner.getName();
			}
		};

		StendhalQuestSystem.get().loadQuest(quest);
	}

	public String sayRequiredItems(final String msg, final boolean includeFee, final boolean highlight) {
		final StringBuilder sb = new StringBuilder();

		final Map<String, Integer> tempList = new LinkedHashMap<String, Integer>(requiredItems);
		if (includeFee) {
			tempList.put("money", serviceFee);
		}

		int idx = 0;
		for (final String key: tempList.keySet()) {
			if (idx == tempList.size() - 1) {
				sb.append("and ");
			}

			// highlight keywords
			String itemName = key;
			if (!itemName.equals("money") && highlight) {
				itemName = "#'" + itemName + "'";
			}

			sb.append(Integer.toString(tempList.get(key)) + " " + itemName);

			if (idx < tempList.size() - 1) {
				sb.append(", ");
			}

			idx++;
		}

		return msg.replace("[items]", sb.toString());
	}

	public String sayRequiredItems(final String msg, final boolean includeFee) {
		return sayRequiredItems(msg, includeFee, true);
	}


	// some helper methods for tests

	public static TannerNPC getInstance() {
		if (instance == null) {
			instance = new TannerNPC();
		}

		return instance;
	}

	public SpeakerNPC getNPC() {
		return tanner;
	}

	public String getQuestSlot() {
		return QUEST_SLOT;
	}

	public String getFeatureSlot() {
		return FEATURE_SLOT;
	}

	public Integer getRequiredMoneyLoot() {
		return requiredMoneyLoot;
	}

	public Integer getServiceFee() {
		return serviceFee;
	}

	public Map<String, Integer> getRequiredItems() {
		return requiredItems;
	}

	public Integer getWaitTime() {
		return TAN_TIME;
	}
}
