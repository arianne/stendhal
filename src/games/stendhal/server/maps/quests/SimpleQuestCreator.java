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
package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rule.EntityManager;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;


public class SimpleQuestCreator {

	private static final Logger logger = Logger.getLogger(SimpleQuestCreator.class);

	private static SimpleQuestCreator instance;

	public static final String ID_REQUEST = "request";
	public static final String ID_ACCEPT = "accept";
	public static final String ID_REJECT = "reject";
	public static final String ID_REWARD = "reward";
	public static final String ID_VERBOSE_REWARD_PREFIX = "verbose_reward_prefix";
	public static final String ID_ALREADY_ACTIVE = "already_active";
	public static final String ID_MISSING = "missing";
	public static final String ID_NO_REPEAT = "no_repeat";
	public static final String ID_COOLDOWN_PREFIX = "cooldown_prefix"; // prefix for SayTimeRemainingAction when player cannot repeat quest yet

	public static final String ID_XP = "xp";
	public static final String ID_DEF = "def";
	public static final String ID_ATK = "atk";
	public static final String ID_RATK = "ratk";


	public static SimpleQuestCreator getInstance() {
		if (instance == null) {
			instance = new SimpleQuestCreator();
		}

		return instance;
	}

	public SimpleQuest create(final String slotName, final String properName, final String npcName) {
		return new SimpleQuest(slotName, properName, npcName);
	}

	public class SimpleQuest extends AbstractQuest {

		private final String QUEST_SLOT;
		private final String name;
		private final SpeakerNPC npc;

		private String description;

		private int repeatDelay = -1;

		private String itemToCollect;
		private int quantityToCollect = 1;

		/**
		 * usable replies are:
		 * 		request, accept, reject, reward, already_active, missing,
		 * 		no_repeat, verbose_reward_prefix, cooldown_prefix
		 */
		private final Map<String, String> replies = new HashMap<String, String>() {{
			put(ID_REQUEST, "Will you help me?");
			put(ID_REJECT, "Okay. Perhaps another time then.");
			put(ID_REWARD, "Thank you.");
			put(ID_VERBOSE_REWARD_PREFIX, "As a reward I will give you");
			put(ID_NO_REPEAT, "Thanks, but I don't need any more help.");
			put(ID_COOLDOWN_PREFIX, "If you want to help me again, please come back in");
		}};

		private double karmaReward = 0;
		private double karmaAcceptReward = 0;
		private double karmaRejectReward = 0;

		// list of items to be rewarded to player upon completion
		private final Map<String, Integer> itemReward = new HashMap<String, Integer>();

		// usable stat rewards are: xp, def, atk, ratk
		private final Map<String, Integer> statReward = new HashMap<String, Integer>();

		// if <code>true</code>, NPC will tell player what items were given as a reward
		private boolean verboseReward = true;

		private String region;


		public SimpleQuest(final String slotName, final String properName, final String npcName) {
			QUEST_SLOT = slotName;
			name = properName;
			npc = SingletonRepository.getNPCList().get(npcName);
		}

		public void setDescription(final String descr) {
			description = descr;
		}

		/**
		 * Sets the quest's repeatable status & repeat delay.
		 *
		 * @param delay
		 * 		Number of minutes player must wait before repeating quest.
		 * 		`0` means immediately repeatable. `null` or less than `0`
		 * 		means not repeatable.
		 */
		public void setRepeatable(Integer delay) {
			if (delay == null) {
				delay = -1;
			}

			repeatDelay = delay;
		}

		public void setItemToCollect(final String itemName, final int quantity) {
			itemToCollect = itemName;
			quantityToCollect = quantity;
		}

		public void setXPReward(final int xp) {
			statReward.put(ID_XP, xp);
		}

		public void setKarmaReward(final double karma) {
			karmaReward = karma;
		}

		public void setKarmaAcceptReward(final double karma) {
			karmaAcceptReward = karma;
		}

		public void setKarmaRejectReward(final double karma) {
			karmaRejectReward = karma;
		}

		public void addItemReward(final String itemName, final int quantity) {
			itemReward.put(itemName, quantity);
		}

		public void addStatReward(final String id, final int amount) {
			statReward.put(id, amount);
		}

		@SuppressWarnings("unused")
		public void addItemReward(final String itemName) {
			addItemReward(itemName, 1);
		}

		public void setVerboseReward(final boolean verbose) {
			verboseReward = verbose;
		}

		public void setReply(final String id, final String reply) {
			if (id == null) {
				logger.warn("Reply ID cannot by null");
				return;
			}
			if (reply == null) {
				logger.warn("Reply cannot be null");
				return;
			}

			replies.put(id, reply);
		}

		/**
		 * Retrieves some predefined responses.
		 *
		 * @param id
		 * @return
		 */
		private String getReply(final String id) {
			String reply = replies.get(id);

			if (reply == null) {
				if (id.equals(ID_ACCEPT)) {
					reply = "I need you to get " + Integer.toString(quantityToCollect) + " " + Grammar.plnoun(quantityToCollect, itemToCollect) + ".";
				} else if (id.equals(ID_ALREADY_ACTIVE)) {
					reply = "I have already asked you to get " + Integer.toString(quantityToCollect) + " " + Grammar.plnoun(quantityToCollect, itemToCollect)
					+ ". Are you #done?";
				} else if (id.equals(ID_MISSING)) {
					reply = "I asked you to bring me " + Integer.toString(quantityToCollect) + " " + Grammar.plnoun(quantityToCollect, itemToCollect) + ".";
				}
			}

			return reply;
		}

		public void setRegion(final String regionName) {
			region = regionName;
		}

		/**
		 * Retrives the number of times the player has completed the quest.
		 *
		 * @param player
		 * 		The Player to check.
		 * @return
		 * 		Number of times completed.
		 */
		private int getCompletedCount(final Player player) {
			final String state = player.getQuest(QUEST_SLOT, 0);

			if (state == null) {
				return 0;
			}

			int completedIndex = 2;
			if (state.equals("start") || state.equals("rejected")) {
				completedIndex = 1;
			}

			try {
				return Integer.parseInt(player.getQuest(QUEST_SLOT, completedIndex));
			} catch (NumberFormatException e) {
				return 0;
			}
		}

		/**
		 * Action to execute when player starts quest.
		 *
		 * @return
		 * 		`ChatAction`
		 */
		private ChatAction startAction() {
			return new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					player.addKarma(karmaAcceptReward);
					player.setQuest(QUEST_SLOT, "start;" + Integer.toString(getCompletedCount(player)));
				}
			};
		}

		/**
		 * Action to execute when player rejects quest.
		 *
		 * @return
		 * 		`ChatAction`
		 */
		private ChatAction rejectAction() {
			return new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					player.addKarma(karmaRejectReward);
					player.setQuest(QUEST_SLOT, "rejected;" + Integer.toString(getCompletedCount(player)));
				}
			};
		}

		/**
		 * Action to execute when player completes quest
		 *
		 * @return
		 * 		`ChatAction`
		 */
		private ChatAction completeAction() {
			return new ChatAction() {
				@Override
				public void fire(Player player, Sentence sentence, EventRaiser npc) {
					// drop collected items
					player.drop(itemToCollect, quantityToCollect);

					final StringBuilder sb = new StringBuilder();
					sb.append(getReply(ID_REWARD));

					final int rewardCount = itemReward.size();

					if (verboseReward && rewardCount > 0) {
						sb.append(" " + getReply(ID_VERBOSE_REWARD_PREFIX).trim() + " ");

						int idx = 0;
						for (final String itemName: itemReward.keySet()) {
							final int quantity = itemReward.get(itemName);

							if (idx == rewardCount - 1) {
								sb.append("and ");
							}

							sb.append(Integer.toString(quantity) + " " + Grammar.plnoun(quantity, itemName));

							if (idx < rewardCount - 1) {
								if (rewardCount == 2) {
									sb.append(" ");
								} else {
									sb.append(", ");
								}
							}

							idx++;
						}

						sb.append(".");
					}

					npc.say(sb.toString());

					// reward player
					final Integer xpReward = statReward.get(ID_XP);
					final Integer defReward = statReward.get(ID_DEF);
					final Integer atkReward = statReward.get(ID_ATK);
					final Integer ratkReward = statReward.get(ID_RATK);

					if (xpReward != null) {
						player.addXP(xpReward);
					}
					if (defReward != null) {
						player.addDefXP(defReward);
					}
					if (atkReward != null) {
						player.addAtkXP(atkReward);
					}
					if (ratkReward != null) {
						player.addRatkXP(ratkReward);
					}

					player.addKarma(karmaReward);

					for (final String itemName: itemReward.keySet()) {
						final EntityManager em = SingletonRepository.getEntityManager();
						final Item item = em.getItem(itemName);
						final int quantity = itemReward.get(itemName);

						if (item instanceof StackableItem) {
							((StackableItem) item).setQuantity(quantity);
						}

						if (item != null) {
							player.equipOrPutOnGround(item);
						}
					}

					player.setQuest(QUEST_SLOT, "done;" + System.currentTimeMillis() + ";" + Integer.toString(getCompletedCount(player) + 1));
				}
			};
		}

		@Override
		public void addToWorld() {
			fillQuestInfo(name, description, isRepeatable());

			final ChatCondition canStartCondition = new ChatCondition() {
				@Override
				public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
					final String questState = player.getQuest(QUEST_SLOT, 0);
					if (questState == null || questState.equals("rejected")) {
						return true;
					}

					if (isRepeatable() && questState.equals("done")) {
						return new TimePassedCondition(QUEST_SLOT, 1, repeatDelay).fire(player, sentence, npc);
					}

					return false;
				}
			};

			final ChatCondition questRepeatableCondition = new ChatCondition() {
				@Override
				public boolean fire(Player player, Sentence sentence, Entity npc) {
					return isRepeatable();
				}
			};


			npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				canStartCondition,
				ConversationStates.QUEST_OFFERED,
				getReply(ID_REQUEST),
				null);

			npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(
					questRepeatableCondition,
					new QuestInStateCondition(QUEST_SLOT, 0, "done")),
				ConversationStates.ATTENDING,
				null,
				new SayTimeRemainingAction(QUEST_SLOT, 1, repeatDelay, getReply(ID_COOLDOWN_PREFIX)));

			npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(
					new NotCondition(questRepeatableCondition),
					new QuestInStateCondition(QUEST_SLOT, 0, "done")),
				ConversationStates.ATTENDING,
				getReply(ID_NO_REPEAT),
				null);

			npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				getReply(ID_ALREADY_ACTIVE),
				null);

			npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				getReply(ID_ACCEPT),
				startAction());

			npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				getReply(ID_REJECT),
				rejectAction());

			npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.FINISH_MESSAGES,
				new AndCondition(
					new QuestActiveCondition(QUEST_SLOT),
					new PlayerHasItemWithHimCondition(itemToCollect, quantityToCollect)),
				ConversationStates.ATTENDING,
				null,
				completeAction());

			npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.FINISH_MESSAGES,
				new AndCondition(
					new QuestActiveCondition(QUEST_SLOT),
					new NotCondition(new PlayerHasItemWithHimCondition(itemToCollect, quantityToCollect))),
				ConversationStates.ATTENDING,
				getReply(ID_MISSING),
				null);
		}

		@Override
		public String getSlotName() {
			return QUEST_SLOT;
		}

		@Override
		public String getName() {
			final StringBuilder sb = new StringBuilder();
			boolean titleCase = true;

			for (char c: name.toCharArray()) {
				if (Character.isSpaceChar(c)) {
					titleCase = true;
				} else if (titleCase) {
					c = Character.toTitleCase(c);
					titleCase = false;
				}

				sb.append(c);
			}

			return sb.toString().replace(" ", "");
		}

		@Override
		public String getNPCName() {
			if (npc == null) {
				return null;
			}

			return npc.getName();
		}

		@Override
		public String getRegion() {
			return region;
		}

		@Override
		public boolean isRepeatable(final Player player) {
			if (!isRepeatable()) {
				return false;
			}

			return new AndCondition(new QuestCompletedCondition(QUEST_SLOT),
					new TimePassedCondition(QUEST_SLOT, 1, repeatDelay)).fire(player, null, null);
		}

		/**
		 * Checks if this quest has be set for repetition.
		 *
		 * @return
		 * 		<code>true</code> if players are allowed to do this quest more than once.
		 */
		private boolean isRepeatable() {
			return repeatDelay >= 0;
		}

		@Override
		public List<String> getHistory(final Player player) {
			final List<String> res = new ArrayList<String>();

			if (!player.hasQuest(QUEST_SLOT)) {
				return res;
			}

			final String[] questState = player.getQuest(QUEST_SLOT).split(";");

			if (questState[0].equals("rejected")) {
				res.add("I do not want to help " + getNPCName() + ".");
			} else if (questState[0].equals("start")) {
				res.add(getNPCName() + " asked me to get " + Integer.toString(quantityToCollect) + " " + Grammar.plnoun(quantityToCollect, itemToCollect) + ".");
				if (player.isEquipped(itemToCollect, quantityToCollect)) {
					res.add("I have what " + getNPCName() + " asked for.");
				} else {
					res.add("I have not found what I am looking for yet.");
				}
			} else if (questState[0].equals("done")) {
				res.add("I found " + Integer.toString(quantityToCollect) + " " + Grammar.plnoun(quantityToCollect, itemToCollect) + " for " + getNPCName() + ".");

				if (isRepeatable()) {
					final int completions = getCompletedCount(player);
					String plural = "time";
					if (completions != 1) {
						plural += "s";
					}

					res.add("I have done this quest " + Integer.toString(completions) + " " + plural + ".");
				}
			}

			return res;
		}
	}
}
