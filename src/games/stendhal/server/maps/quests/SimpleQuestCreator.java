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

import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rp.StendhalQuestSystem;
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
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;

public class SimpleQuestCreator {

	private static SimpleQuestCreator instance;

	public static SimpleQuestCreator getInstance() {
		if (instance == null) {
			instance = new SimpleQuestCreator();
		}

		return instance;
	}

	public SimpleQuest create(final String slotName, final String properName, final String npcName) {
		return new SimpleQuest(slotName, properName, npcName);
	}

	private class SimpleQuest extends AbstractQuest {

		private final String QUEST_SLOT;
		private final String name;
		private final SpeakerNPC npc;

		private String description;

		private boolean repeatable = false;
		private int repeatDelay = 0;

		private String itemToCollect;
		private int quantityToCollect = 1;

		private Map<String, String> replies = new HashMap<String, String>() {{
			// default replies
			put("request", "Will you help me?");
			put("reject", "Okay. Perhaps another time then.");
			put("reward", "Thank you.");
		}};

		private int xpReward = 0;
		private double karmaReward = 0;
		private Map<String, Integer> itemReward = new HashMap<String, Integer>();

		private String region;


		public SimpleQuest(final String slotName, final String properName, final String npcName) {
			QUEST_SLOT = slotName;
			name = properName;
			npc = SingletonRepository.getNPCList().get(npcName);
		}

		@SuppressWarnings("unused")
		public void setDescription(final String descr) {
			description = descr;
		}

		@SuppressWarnings("unused")
		public void setRepeatable(final boolean repeatable) {
			this.repeatable = repeatable;
		}

		@SuppressWarnings("unused")
		public void setRepeatDelay(final int delay) {
			repeatDelay = delay;
		}

		@SuppressWarnings("unused")
		public void setItemToCollect(final String itemName, final int quantity) {
			itemToCollect = itemName;
			quantityToCollect = quantity;
		}

		@SuppressWarnings("unused")
		public void setXPReward(final int xp) {
			xpReward = xp;
		}

		@SuppressWarnings("unused")
		public void setKarmaReward(final double karma) {
			karmaReward = karma;
		}

		public void addItemReward(final String itemName, final int quantity) {
			itemReward.put(itemName, quantity);
		}

		@SuppressWarnings("unused")
		public void addItemReward(final String itemName) {
			addItemReward(itemName, 1);
		}

		public void setReply(final String rType, final String reply) {
			replies.put(rType, reply);
		}

		private String getReply(final String rType) {
			String reply = replies.get(rType);

			if (rType.equals("accept")) {
				if (reply == null) {
					reply = "I need you to get " + Integer.toString(quantityToCollect) + " " + Grammar.plnoun(quantityToCollect, itemToCollect) + ".";
				} else {
					reply = reply + " I need you to get " + Integer.toString(quantityToCollect) + " " + Grammar.plnoun(quantityToCollect, itemToCollect) + ".";
				}
			}

			return reply;
		}

		@SuppressWarnings("unused")
		public void setRequestReply(final String reply) {
			setReply("request", reply);
		}

		@SuppressWarnings("unused")
		public void setAcceptReply(final String reply) {
			setReply("accept", reply);
		}

		@SuppressWarnings("unused")
		public void setRejectReply(final String reply) {
			setReply("reject", reply);
		}

		@SuppressWarnings("unused")
		public void setRewardReply(final String reply) {
			setReply("reward", reply);
		}

		@SuppressWarnings("unused")
		public void setRegion(final String regionName) {
			region = regionName;
		}

		private void rewardPlayer(final Player player) {
			player.addXP(xpReward);
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

			player.setQuest(QUEST_SLOT, "done;" + System.currentTimeMillis());
		}

		@SuppressWarnings("unused")
		public void register() {
			StendhalQuestSystem.get().loadQuest(this);
		}

		@Override
		public void addToWorld() {
			fillQuestInfo(name, description, repeatable);

			final ChatCondition canStartCondition = new ChatCondition() {
				@Override
				public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
					if (player.getQuest(QUEST_SLOT) == null) {
						return true;
					}

					if (repeatable && player.getQuest(QUEST_SLOT, 0).equals("done")) {
						return new TimePassedCondition(QUEST_SLOT, 1, repeatDelay).fire(player, sentence, npc);
					}

					return false;
				}
			};

			final ChatCondition questRepeatableCondition = new ChatCondition() {
				@Override
				public boolean fire(Player player, Sentence sentence, Entity npc) {
					return repeatable;
				}
			};

			final ChatAction rewardAction = new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
					// drop collected items
					player.drop(itemToCollect, quantityToCollect);

					final StringBuilder sb = new StringBuilder();
					sb.append(getReply("reward"));

					final int rewardCount = itemReward.size();

					if (rewardCount > 0) {
						sb.append(" As a reward I will give you ");

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

					rewardPlayer(player);
				}
			};


			npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				canStartCondition,
				ConversationStates.QUEST_OFFERED,
				getReply("request"),
				null);

			npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(
					questRepeatableCondition,
					new QuestInStateCondition(QUEST_SLOT, 0, "done")),
				ConversationStates.ATTENDING,
				null,
				new SayTimeRemainingAction(QUEST_SLOT, 1, repeatDelay, "If you want to help me again, please come back in "));

			npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(
					new NotCondition(questRepeatableCondition),
					new QuestInStateCondition(QUEST_SLOT, 0, "done")),
				ConversationStates.ATTENDING,
				"Thanks, but I don't need any more help.",
				null);

			npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"I have already asked you to get " + Integer.toString(quantityToCollect) + " " + Grammar.plnoun(quantityToCollect, itemToCollect) + ".",
				null);

			npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				getReply("accept"),
				new SetQuestAction(QUEST_SLOT, "start"));

			npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				getReply("reject"),
				null);

			npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.FINISH_MESSAGES,
				new AndCondition(
					new QuestActiveCondition(QUEST_SLOT),
					new PlayerHasItemWithHimCondition(itemToCollect, quantityToCollect)),
				ConversationStates.ATTENDING,
				null,
				rewardAction);

			npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.FINISH_MESSAGES,
				new AndCondition(
					new QuestActiveCondition(QUEST_SLOT),
					new NotCondition(new PlayerHasItemWithHimCondition(itemToCollect, quantityToCollect))),
				ConversationStates.ATTENDING,
				"I asked you to bring me " + Integer.toString(quantityToCollect) + " " + Grammar.plnoun(quantityToCollect, itemToCollect) + ".",
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
			if (!repeatable) {
				return false;
			}

			return new AndCondition(new QuestCompletedCondition(QUEST_SLOT),
					new TimePassedCondition(QUEST_SLOT, 1, repeatDelay)).fire(player, null, null);
		}

		@Override
		public List<String> getHistory(final Player player) {
			final List<String> res = new ArrayList<String>();

			if (!player.hasQuest(QUEST_SLOT)) {
				return res;
			}

			final String[] questState = player.getQuest(QUEST_SLOT).split(";");

			if (questState[0].equals("start")) {
				res.add(getNPCName() + " asked me to get " + Integer.toString(quantityToCollect) + " " + Grammar.plnoun(quantityToCollect, itemToCollect) + ".");
				if (player.isEquipped(itemToCollect, quantityToCollect)) {
					res.add("I have what " + getNPCName() + " asked for.");
				} else {
					res.add("I have not found what I am looking for yet.");
				}
			} else if (questState[0].equals("done")) {
				res.add("I found " + Integer.toString(quantityToCollect) + " " + Grammar.plnoun(quantityToCollect, itemToCollect) + " for " + getNPCName() + ".");
			}

			return res;
		}
	}
}
