/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2024 - Stendhal                    *
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import games.stendhal.common.constants.SoundID;
import games.stendhal.common.constants.SoundLayer;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rp.HOFScore;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropRecordedItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayRequiredItemAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.action.StartRecordingRandomItemCollectionAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasRecordedItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.npc.shop.ShopType;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.SoundEvent;
import games.stendhal.server.maps.Region;
import games.stendhal.server.util.TimeUtil;


/**
 * QUEST: Ultimate Collector
 * <p>
 * PARTICIPANTS: <ul><li> Balduin  </ul>
 *
 * STEPS:
 * <ul><li> Balduin challenges you to be the ultimate weapons collector
 *     <li> Balduin asks you to complete each quest where you win a rare item
 *     <li> Balduin asks you to bring him one extra rare item from a list
 *</ul>
 *
 * REWARD: <ul>
 * <li> You can sell black items to Balduin
 * <li> 100000 XP
 * <li> 90 karma
 * </ul>
 *
 * REPETITIONS: <ul><li> None. </ul>
 */
public class UltimateCollector extends AbstractQuest {

	/** Quest slot for this quest, the Ultimate Collector */
	private static final String QUEST_SLOT = "ultimate_collector";

	/** Club of Thorns in Kotoch: The Orc Saman is the NPC */
	private static final String CLUB_THORNS_QUEST_SLOT = "club_thorns"; // kotoch

	/** Vampire Sword quest: Hogart is the NPC */
	private static final String VAMPIRE_SWORD_QUEST_SLOT = "vs_quest"; // dwarf blacksmith

	/** Obsidian Knife quest: Alrak is the NPC */
	private static final String OBSIDIAN_KNIFE_QUEST_SLOT = "obsidian_knife"; // dwarf blacksmith

	/** Immortal Sword Quest in Kotoch: Vulcanus is the NPC */
	private static final String IMMORTAL_SWORD_QUEST_SLOT = "immortalsword_quest"; // kotoch

	/** Mithril Cloak quest: Ida is the NPC */
	private static final String MITHRIL_CLOAK_QUEST_SLOT = "mithril_cloak"; // mithril

	/** Mithril Shield quest: Baldemar is the NPC */
	private static final String MITHRIL_SHIELD_QUEST_SLOT = "mithrilshield_quest"; // mithril

	/** Cloak Collector 2nd quest: Josephine is the NPC (Completing 2nd requires 1st) */
	private static final String CLOAKSCOLLECTOR2_QUEST_SLOT = "cloaks_collector_2"; // cloaks

	/** Cloaks For Bario (Freezing Dwarf) quest: Bario is the NPC  */
	private static final String CLOAKS_FOR_BARIO_QUEST_SLOT = "cloaks_for_bario"; // cloaks

	// private static final String HELP_TOMI_QUEST_SLOT = "help_tomi"; don't require

	/** Elvish Armor quest: Lupos is the NPC */
	private static final String ELVISH_ARMOR_QUEST_SLOT = "elvish_armor"; // specific for this one

	/** Kanmararn Soldiers quest: Henry is the NPC  */
	private static final String KANMARARN_QUEST_SLOT = "soldier_henry"; // specific for this one

	/** Weapons Collector 2nd quest: Balduin is the NPC (Completing 2nd requires 1st) */
	private static final String WEAPONSCOLLECTOR2_QUEST_SLOT = "weapons_collector2";


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
		res.add("Balduin asked me for a last special ultimate weapon collector quest.");
		final String questState = player.getQuest(QUEST_SLOT);
		if (questState.equals("rejected")) {
			res.add("I don't want to bring him any more weapons at the moment.");
			return res;
		}
		res.add("I accepted his last ultimate quest and promised to bring him a special and rare weapon.");
		if (!isCompleted(player)) {
			res.add("Balduin has asked me to bring him " + Grammar.a_noun(player.getRequiredItemName(QUEST_SLOT,0)) + ".");
		}
		if (isCompleted(player)) {
			res.add("Yay! I am <em>the</em> ultimate weapon collector now and I can sell black items to Balduin!");
		}
		return res;
	}

	private void checkCollectingQuests() {
		final SpeakerNPC npc = npcs.get("Balduin");


		npc.add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestCompletedCondition(WEAPONSCOLLECTOR2_QUEST_SLOT),
					new QuestNotStartedCondition(QUEST_SLOT)),
			ConversationStates.ATTENDING,
			"Greetings old friend. I have another collecting #challenge for you.",
			null);

		npc.add(ConversationStates.ATTENDING,
			"challenge",
			new AndCondition(new QuestCompletedCondition(WEAPONSCOLLECTOR2_QUEST_SLOT),
					new QuestNotStartedCondition(QUEST_SLOT),
					new OrCondition(new QuestNotCompletedCondition(CLUB_THORNS_QUEST_SLOT),
							new QuestNotCompletedCondition(IMMORTAL_SWORD_QUEST_SLOT))),
			ConversationStates.ATTENDING,
			"There is still a quest in the Kotoch area which you have not completed. Explore thoroughly and you will be on your way to becoming the ultimate collector!",
			null);


		npc.add(ConversationStates.ATTENDING,
			"challenge",
			new AndCondition(new QuestCompletedCondition(WEAPONSCOLLECTOR2_QUEST_SLOT),
					new QuestNotStartedCondition(QUEST_SLOT),
					new OrCondition(new QuestNotCompletedCondition(MITHRIL_CLOAK_QUEST_SLOT),
							new QuestNotCompletedCondition(MITHRIL_SHIELD_QUEST_SLOT))),
			ConversationStates.ATTENDING,
			"You are missing a special mithril item which you can win if you help the right person, you cannot be an ultimate collector without it.",
			null);

		npc.add(ConversationStates.ATTENDING,
			"challenge",
			new AndCondition(new QuestCompletedCondition(WEAPONSCOLLECTOR2_QUEST_SLOT),
					new QuestNotStartedCondition(QUEST_SLOT),
					new OrCondition(new QuestNotCompletedCondition(OBSIDIAN_KNIFE_QUEST_SLOT),
							new QuestNotCompletedCondition(VAMPIRE_SWORD_QUEST_SLOT))),
			ConversationStates.ATTENDING,
			"There is a dwarf blacksmith living alone deep underground who would forge a special weapon for you, you cannot be an ultimate collector without this.",
			null);

		npc.add(ConversationStates.ATTENDING,
			"challenge",
			new AndCondition(new QuestCompletedCondition(WEAPONSCOLLECTOR2_QUEST_SLOT),
					new QuestNotStartedCondition(QUEST_SLOT),
					new OrCondition(new QuestNotCompletedCondition(CLOAKSCOLLECTOR2_QUEST_SLOT),
							new QuestNotCompletedCondition(CLOAKS_FOR_BARIO_QUEST_SLOT))),
			ConversationStates.ATTENDING,
			"A special item will be yours if you collect many cloaks, whether to fulfill another's vanity or keep them warm, it's a task you must complete.",
			null);

		npc.add(ConversationStates.ATTENDING,
			"challenge",
			new AndCondition(new QuestCompletedCondition(WEAPONSCOLLECTOR2_QUEST_SLOT),
					new QuestNotStartedCondition(QUEST_SLOT),
					new QuestNotCompletedCondition(ELVISH_ARMOR_QUEST_SLOT)),
			ConversationStates.ATTENDING,
			"Another collector of items still needs your help. You'd find him in Fado Forest, and until you have completed that favor for him, you cannot be the ultimate collector.",
			null);

		npc.add(ConversationStates.ATTENDING,
			"challenge",
			new AndCondition(new QuestCompletedCondition(WEAPONSCOLLECTOR2_QUEST_SLOT),
					new QuestNotStartedCondition(QUEST_SLOT),
					new QuestNotCompletedCondition(KANMARARN_QUEST_SLOT)),
			ConversationStates.ATTENDING,
			"You've collected so many special items, but you have never helped those down in Kanmararn city. You should complete a task there.",
			null);

	}

	/**
	 * Determines items to select from.
	 *
	 * @param exclude
	 *   An item name to exclude from returned value or {@code null} to include all. Used to prevent
	 *   re-requesting same item when player asks for "another".
	 * @return
	 *   Items that may be requested from player.
	 */
	private Map<String, Integer> getItems(final String exclude) {
		/* Updated 2022-05-22
		 *
		 * Rarity calculations (lower means more rare):
		 *   (spawn count * drop rate * (1 / respawn)) * 1000 + (spawn count * drop rate * (1 / respawn)) * 1000 ...
		 *
		 * See: https://stendhalgame.org/wiki/StendhalItemsDropScoring
		 *   Current most rare: 0.09 (black scythe)
		 *   Current least rare: 2.0 (chaos dagger)
		 *
		 * Items given as rewards from quests or otherwise acquirable via
		 * methods other than creature drops should not be included.
		 */
		final Map<String, Integer> items = new HashMap<>();
		items.put("nihonto", 1); // 1.39
		items.put("magic twoside axe", 1); // 1.72
		items.put("imperator sword", 1); // 0.33
		items.put("durin axe", 1); // 0.39
		items.put("vulcano hammer", 1); // 0.18
		items.put("xeno sword", 1); // 0.67
		items.put("black scythe", 1); // 0.09
		items.put("chaos dagger", 1); // 2.0
		items.put("black sword", 1); // 0.15
		items.put("golden orc sword", 1); // 0.09
		items.put("ice war hammer", 1); // 0.15
		items.put("orcish sword", 1); // 0.86
		items.put("black halberd", 1); // 0.12
		if (exclude != null) {
			items.remove(exclude);
		}
		return items;
	}

	private void requestItem() {
		final SpeakerNPC npc = npcs.get("Balduin");

		// If all quests are completed, ask for an item
		npc.add(ConversationStates.ATTENDING,
				"challenge",
				new AndCondition(
						new QuestCompletedCondition(WEAPONSCOLLECTOR2_QUEST_SLOT),
						new QuestNotStartedCondition(QUEST_SLOT),
						new QuestCompletedCondition(KANMARARN_QUEST_SLOT),
						new QuestCompletedCondition(ELVISH_ARMOR_QUEST_SLOT),
						new QuestCompletedCondition(CLOAKSCOLLECTOR2_QUEST_SLOT),
						new QuestCompletedCondition(CLOAKS_FOR_BARIO_QUEST_SLOT),
						new QuestCompletedCondition(OBSIDIAN_KNIFE_QUEST_SLOT),
						new QuestCompletedCondition(VAMPIRE_SWORD_QUEST_SLOT),
						new QuestCompletedCondition(MITHRIL_CLOAK_QUEST_SLOT),
						new QuestCompletedCondition(MITHRIL_SHIELD_QUEST_SLOT),
						new QuestCompletedCondition(CLUB_THORNS_QUEST_SLOT),
						new QuestCompletedCondition(IMMORTAL_SWORD_QUEST_SLOT)),
				ConversationStates.ATTENDING,
				null,
				new MultipleActions(
					new StartRecordingRandomItemCollectionAction(QUEST_SLOT, 0, getItems(null),
							"Well, you've certainly proved to the residents of Faiumoni that you could be the"
							+ " ultimate collector, but I have one more task for you. Please bring me [item]."),
					new SetQuestToTimeStampAction(QUEST_SLOT, 1)));
	}

	private void collectItem() {

		final SpeakerNPC npc = npcs.get("Balduin");

		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
						new QuestActiveCondition(QUEST_SLOT)),
				ConversationStates.QUEST_ITEM_QUESTION,
				"Did you bring me that very rare item I asked you for?",
				null);

		npc.add(ConversationStates.QUEST_ITEM_QUESTION,
				ConversationPhrases.YES_MESSAGES,
				new AndCondition(new QuestActiveCondition(QUEST_SLOT),
								new NotCondition(new PlayerHasRecordedItemWithHimCondition(QUEST_SLOT))),
				ConversationStates.ATTENDING,
				null,
				new SayRequiredItemAction(QUEST_SLOT, "Hm, no, you don't have [item], don't try to fool me!"));

		npc.add(ConversationStates.QUEST_ITEM_QUESTION,
				ConversationPhrases.YES_MESSAGES,
				new AndCondition(new QuestActiveCondition(QUEST_SLOT),
								new PlayerHasRecordedItemWithHimCondition(QUEST_SLOT)),
				ConversationStates.ATTENDING,
				"Wow, it's incredible to see this close up! Many thanks. Now, perhaps we can #deal together.",
				new MultipleActions(new DropRecordedItemAction(QUEST_SLOT),
									new SetQuestAction(QUEST_SLOT, "done"),
									new IncreaseXPAction(100000),
									new IncreaseKarmaAction(90)));

		npc.add(ConversationStates.QUEST_ITEM_QUESTION,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				null,
				new SayRequiredItemAction(QUEST_SLOT, "Very well, come back when you have [the item] with you."));
	}

	private void offerSteps() {
		final SpeakerNPC npc = npcs.get("Balduin");

		// player returns after finishing the quest and says offer
		npc.add(
				ConversationStates.ATTENDING,
				ConversationPhrases.OFFER_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"I buy black items, but I can only afford to pay you modest prices."
					+ " I will also #replace your #lost swords... for a price.",
				null);


		// player returns when the quest is in progress and says offer
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.OFFER_MESSAGES,
				new QuestNotCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"I'll buy black items from you when you have completed each #challenge I set you.", null);
	}

	private void abortQuest() {
		final SpeakerNPC npc = npcs.get("Balduin");
		// approximately 6 months
		final int expireTime = TimeUtil.MINUTES_IN_HALF_YEAR;

		npc.add(
				ConversationStates.ATTENDING,
				ConversationPhrases.ABORT_MESSAGES,
				new AndCondition(
						new QuestActiveCondition(QUEST_SLOT),
						new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, expireTime))
				),
				ConversationStates.ATTENDING,
				null,
				new SayRequiredItemAction(QUEST_SLOT, 0, "You are on a quest to find [item]. You cannot"
						+ " request a new item yet."));

		npc.add(
				ConversationStates.ATTENDING,
				ConversationPhrases.ABORT_MESSAGES,
				new AndCondition(
						new QuestActiveCondition(QUEST_SLOT),
						new TimePassedCondition(QUEST_SLOT, 1, expireTime)
				),
				ConversationStates.QUEST_OFFERED,
				null,
				new SayRequiredItemAction(QUEST_SLOT, 0, "You are on a quest to find [item]. Would you like"
						+ " to look for a different item?"));

		npc.add(
				ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				new AndCondition(
						new QuestActiveCondition(QUEST_SLOT),
						new TimePassedCondition(QUEST_SLOT, 1, expireTime)
				),
				ConversationStates.ATTENDING,
				null,
				new RequestAnotherAction());

		npc.add(
				ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				new AndCondition(
						new QuestActiveCondition(QUEST_SLOT),
						new TimePassedCondition(QUEST_SLOT, 1, expireTime)
				),
				ConversationStates.ATTENDING,
				null,
				new SayRequiredItemAction(QUEST_SLOT, 0, "Then bring me [item]."));
	}

	private void replaceLRSwords() {
		final SpeakerNPC npc = npcs.get("Balduin");
		final Map<String, Integer> prices = SingletonRepository.getShopsList().get("twohandswords",
				ShopType.ITEM_SELL);
		final EntityManager em = SingletonRepository.getEntityManager();

		class ReplaceSwordAction implements ChatAction {
			private final String sword_name;

			public ReplaceSwordAction(final String sword_name) {
				this.sword_name = sword_name;
			}

			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
				final int price = prices.get(sword_name);

				player.drop("money", price);
				raiser.addEvent(new SoundEvent(SoundID.COMMERCE, SoundLayer.CREATURE_NOISE));

				final Item sword = em.getItem(sword_name);
				sword.setBoundTo(player.getName());

				player.equipOrPutOnGround(sword);
				player.incBoughtForItem(sword_name, 1);
				player.incCommerceTransaction(npc.getName(), price, false);

				raiser.say("Here is your new " + sword_name + ". Be careful not to lose it"
					+ " this time.... Or not. I don't mind getting paid.");
			}
		}


		// NOTE: weapons collector 1 & 2 quests makes use of QUESTION_1 & QUESTION_3
		//   states. So we must start with QUESTION_4 state.

		// player lost a sword & wants to buy a new one
		npc.add(
			ConversationStates.ATTENDING,
			Arrays.asList("lost", "replace"),
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.QUESTION_3,
			"Which sword do you want to replace, the #left or the #right?",
			null);

		// player wants left sword

		npc.add(
			ConversationStates.QUESTION_3,
			"left",
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.QUESTION_4,
			"It will cost " + prices.get("l hand sword") + " to replace that sword. Do you want it?",
			null);

		npc.add(
			ConversationStates.QUESTION_4,
			ConversationPhrases.NO_MESSAGES,
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"Alright. Is there anything else I can do for you?",
			null);

		// not enough money
		npc.add(
			ConversationStates.QUESTION_4,
			ConversationPhrases.YES_MESSAGES,
			new AndCondition(
				new QuestCompletedCondition(QUEST_SLOT),
				new NotCondition(new PlayerHasItemWithHimCondition("money", prices.get("l hand sword")))
			),
			ConversationStates.ATTENDING,
			"It seems you don't have enough money.",
			null);

		npc.add(
			ConversationStates.QUESTION_4,
			ConversationPhrases.YES_MESSAGES,
			new AndCondition(
				new QuestCompletedCondition(QUEST_SLOT),
				new PlayerHasItemWithHimCondition("money", prices.get("l hand sword"))
			),
			ConversationStates.ATTENDING,
			null,
			new ReplaceSwordAction("l hand sword"));

		// player wants right sword

		npc.add(
			ConversationStates.QUESTION_3,
			"right",
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.QUESTION_5,
			"It will cost " + prices.get("r hand sword") + " to replace that sword. Do you want it?",
			null);

		npc.add(
			ConversationStates.QUESTION_5,
			ConversationPhrases.NO_MESSAGES,
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"Alright. Is there anything else I can do for you?",
			null);

		// not enough money
		npc.add(
			ConversationStates.QUESTION_5,
			ConversationPhrases.YES_MESSAGES,
			new AndCondition(
				new QuestCompletedCondition(QUEST_SLOT),
				new NotCondition(new PlayerHasItemWithHimCondition("money", prices.get("r hand sword")))
			),
			ConversationStates.ATTENDING,
			"It seems you don't have enough money.",
			null);

		npc.add(
			ConversationStates.QUESTION_5,
			ConversationPhrases.YES_MESSAGES,
			new AndCondition(
				new QuestCompletedCondition(QUEST_SLOT),
				new PlayerHasItemWithHimCondition("money", prices.get("r hand sword"))
			),
			ConversationStates.ATTENDING,
			null,
			new ReplaceSwordAction("r hand sword"));
	}


	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Ultimate Weapon Collector",
				"Balduin, the hermit who is living on Ados rock, has a last and ultimate challenge for collectors.",
				true);
		setBaseHOFScore(HOFScore.HARD);

		checkCollectingQuests();
		requestItem();
		collectItem();
		offerSteps();
		abortQuest();
		replaceLRSwords();
	}

	@Override
	public String getName() {
		return "UltimateCollector";
	}

	// This is the max level of the min levels for the other quests
	@Override
	public int getMinLevel() {
		return 100;
	}

	@Override
	public String getNPCName() {
		return "Balduin";
	}

	@Override
	public String getRegion() {
		return Region.ADOS_SURROUNDS;
	}


	private class RequestAnotherAction implements ChatAction {
		@Override
		public void fire(Player player, Sentence sentence, EventRaiser npc) {
			final String previousItem = player.getQuest(QUEST_SLOT, 0).split("=")[0];
			new StartRecordingRandomItemCollectionAction(QUEST_SLOT, 0, getItems(previousItem),
					"Perhaps finding " + Grammar.a_noun(previousItem) + " proved to be too difficult for you."
							+ " This time, I want you to find [item].").fire(player, sentence, npc);
			new SetQuestToTimeStampAction(QUEST_SLOT, 1).fire(player, sentence, npc);
		}
	}
}
