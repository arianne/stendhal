package games.stendhal.server.maps.quests;

import games.stendhal.common.Grammar;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPC.ChatAction;
import games.stendhal.server.entity.npc.action.DecreaseKarmaAction;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * QUEST: CrownForTheWannaBeKing
 *
 * PARTICIPANTS: - Ivan Abe, the wannabe king who lives in Kalavan
 *               - Kendra Mattori, priestess living in Wizard City
 *
 * STEPS: - Ivan Abe wants you to bring him gems and gold for his crown which he
 *          believes will help him to become the new king.
 *        - Kendra Mattori gives the reward after player brought all required items.
 *
 * REWARD: - 10,000 XP
 *         - Player's ATK XP is increased by 0.1% of his/her XP.
 *
 * REPETITIONS: - None.
 */
public class CrownForTheWannaBeKing extends AbstractQuest {

	/**
	 * Name of the main NPC for this quest.
	 */
	private static final String NPC_NAME = "Ivan Abe";

	/**
	 * Name of the NPC giving the reward.
	 */
	private static final String REWARD_NPC_NAME = "Kendra Mattori";

	/**
	 * required items for the quest as itemName=count;
	 */
	private static final String NEEDED_ITEMS = "gold_bar=2;emerald=4;sapphire=3;carbuncle=2;diamond=2;obsidian=1;";

	/**
	 * Name of the slot used for this quest.
	 */
	private static final String QUEST_SLOT = "crown_for_the_wannabe_king";

	/**
	 * how much ATK XP is given as the reward: formula is player's XP *
	 * ATK_BONUS_RATE ie. 0.001 = 0.1% of the player's XP
	 */
	private static final double ATK_REWARD_RATE = 0.001;

	/**
	 * how much XP is given as the reward.
	 */
	private static final int XP_REWARD = 10000;

	/**
	 * initialize the introduction and start of the quest.
	 */
	private void step_1() {
		SpeakerNPC npc = npcs.get(NPC_NAME);
		npc.addOffer("I don't sell anything!");
		npc.addGoodbye();
		npc.addJob("My current job is unimportant, I will be the king of Kalavan!");

		/* player says hi before starting the quest */
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Greetings. Be quick with your matters, I have a lot of work to do."
					+ " And next time clean your boots, you are lucky that I'm not the king...yet!",
				null);

		npc.addQuest("Hmm you could be useful for my #plan...");
		npc.addReply("plan",
					"Soon I will dethrone the king of Kalavan and become the new king! Right now I need myself a new #crown.");

		/* player says crown */
		npc.add(ConversationStates.ATTENDING,
				"crown",
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED,
				"Yes, I need jewels and gold for my new crown. Will you help me?",
				null);

		/* player says yes */
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.QUESTION_1, null, new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
						player.setQuest(QUEST_SLOT, NEEDED_ITEMS);
						player.addKarma(5.0);
						engine.say("I want my crown to be beautiful and shiny. I need "
									+ Grammar.enumerateCollection(getMissingItems(player, true))
									+ ". Do you have some of those now with you?");
					}
				});

		/* player is not willing to help */
		npc.add(ConversationStates.QUEST_OFFERED, "no", null,
				ConversationStates.IDLE, 
				"Oh you don't want to help me?! Get lost, you are wasting my precious time!",
				new DecreaseKarmaAction(5.0));
	}

	/**
	 * initialize the main part of the quest
	 */
	private void step_2() {
		SpeakerNPC npc = npcs.get(NPC_NAME);

		/* player returns while quest is still active */
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, Sentence sentence,	SpeakerNPC engine) {
						return player.hasQuest(QUEST_SLOT)
								&& !player.isQuestCompleted(QUEST_SLOT)
								&& !"reward".equals(player.getQuest(QUEST_SLOT));
					}
				},
				ConversationStates.QUESTION_1,
				"Oh it's you again. Did you bring me any #items for my new crown?",
				null);

		/* player asks what exactly is missing (says items) */
		npc.add(ConversationStates.QUESTION_1, "items", null,
				ConversationStates.QUESTION_1, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
						List<String> needed = getMissingItems(player, true);
						engine.say("I need "
								+ Grammar.enumerateCollection(needed)
								+ ". Did you bring something?");
					}
				});

		/* player says he has a required item with him (says yes) */
		npc.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.QUESTION_1, "Fine, what did you bring?",
				null);

		/* create the ChatAction used for item triggers */
		ChatAction itemsChatAction = new SpeakerNPC.ChatAction() {
			@Override
			public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
				List<String> missingItems = getMissingItems(player, false);
				String item = sentence.toString();
				int missingCount = getMissingCount(item, missingItems);
				if (missingCount > 0) {
					if (dropItems(player, item, missingCount)) {
						missingItems = getMissingItems(player, false);
						if (missingItems.size() > 0) {
							engine.say("Good, do you have anything else?");
						} else {
							engine.say("You have served me well, my crown will be the mightiest of them all!"
											+ " Go to see "
											+ REWARD_NPC_NAME
											+ " in the Wizard City to get your #reward.");
							player.setQuest(QUEST_SLOT, "reward");
							player.addXP(XP_REWARD);
							player.notifyWorldAboutChanges();
							engine.setCurrentState(ConversationStates.ATTENDING);
						}
					} else {
						engine.say("You don't have " + item + " with you!");
					}
				} else {
					engine.say("You have already brought that!");
				}
			}
		};

		/* add triggers for the item names */
		for (String item : NEEDED_ITEMS.split(";")) {
			String tempItem = item.substring(0, item.indexOf('='));
			npc.add(ConversationStates.QUESTION_1, tempItem, null,
					ConversationStates.QUESTION_1, null, itemsChatAction);
		}

		/* player says he didn't bring any items (says no) */
		npc.add(ConversationStates.ATTENDING, Arrays.asList("no", "nothing"),
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, Sentence sentence, SpeakerNPC engine) {
						return player.hasQuest(QUEST_SLOT)
								&& !player.isQuestCompleted(QUEST_SLOT)
								&& !"reward".equals(player
										.getQuest(QUEST_SLOT));
					}
				}, ConversationStates.IDLE,
				"Well don't come back before you find something for me!", null);

		/* player says he didn't bring any items to different question */
		npc.add(ConversationStates.QUESTION_1, ConversationPhrases.NO_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, Sentence sentence, SpeakerNPC engine) {
						return !player.isQuestCompleted(QUEST_SLOT)
								&& !"reward".equals(player.getQuest(QUEST_SLOT));
					}
				}, ConversationStates.IDLE,
				"Farewell, come back after you have what I need!", null);


		/* player says reward */
		npc.add(ConversationStates.ATTENDING,
				"reward", null,
				ConversationStates.IDLE, "As I said, find priestess " + REWARD_NPC_NAME
					+ " in a temple at the city of wizards. She will give you your reward. Now go, I'm busy!",
				null);

		/*
		 * player returns after finishing the quest or before collecting the
		 * reward
		 */
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, Sentence sentence, SpeakerNPC engine) {
						return player.isQuestCompleted(QUEST_SLOT)
								|| "reward".equals(player.getQuest(QUEST_SLOT));
					}
				},
				ConversationStates.IDLE,
				"My new crown will be ready soon and I will dethrone the king! Mwahahaha!",
				null);
	}

	/**
	 * initialize the rewarding NPC.
	 */
	private void step_3() {
		SpeakerNPC npc = npcs.get(REWARD_NPC_NAME);

		npc.add(ConversationStates.ATTENDING, "reward",
				new QuestInStateCondition(QUEST_SLOT, "reward"),
				ConversationStates.ATTENDING, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
						engine.say("Oh yes, "
									+ NPC_NAME
									+ " told me to reward you well! I hope you enjoy your increased combat abilities!");
						rewardPlayer(player);
						player.setQuest(QUEST_SLOT, "done");
					}
				});
	}

	/**
	 * Returns a list of the names of all items that the given player still has
	 * to bring to complete the quest.
	 *
	 * @param player The player doing the quest
	 * @param hash If true, sets a # character in front of every name
	 * @return A list of item names
	 */
	private List<String> getMissingItems(Player player, boolean hash) {
		List<String> result = new LinkedList<String>();

		String missingText = player.getQuest(QUEST_SLOT);
		if (missingText != null && missingText.length() > 0) {
			List<String> missing = Arrays.asList(missingText.split(";"));
			for (String item : missing) {

				if (hash) {
					String[] pair = item.split("=");
					result.add(pair[1] + " #" + pair[0]);
				} else {
				   result.add(item);
				}
			}
		}
		return result;
	}

	/**
	 * Drop specified amount of given item. If player doesn't have enough items,
	 * all carried ones will be dropped and number of missing items is updated.
	 *
	 * @param player
	 * @param itemName
	 * @param itemCount
	 * @return true if something was dropped
	 */
	private boolean dropItems(Player player, String itemName, int itemCount) {
		boolean result = false;
		String missingText = player.getQuest(QUEST_SLOT);
		if (player.drop(itemName, itemCount)) {
			missingText = missingText.replaceFirst(itemName + "=\\d+;", "");
			player.setQuest(QUEST_SLOT, missingText);
			result = true;
		} else {
			/*
			 * handle the cases the player has part of the items or all divided
			 * in different slots
			 */
			List<Item> items = player.getAllEquipped(itemName);
			if (items != null) {
				for (Item item : items) {
					int quantity = item.getQuantity();
					if (player.drop(itemName, Math.min(itemCount, quantity))) {
						itemCount -= quantity;
						result = true;
					}
					if (itemCount == 0) {
						result = true;
						break;
					}
				}
				if (itemCount == 0) {
					missingText = missingText.replaceFirst(itemName + "=\\d+;",
							"");
				} else {
					missingText = missingText.replaceFirst(itemName + "=\\d+;",
							itemName + "=" + itemCount + ";");
				}
				player.setQuest(QUEST_SLOT, missingText);
			}
		}
		return result;
	}

	private int getMissingCount(String text, List<String> missing) {
		int result = 0;
		for (String item : missing) {
			if (item.startsWith(text + "=")) {
				result = Integer.parseInt(item.substring(item.indexOf('=') + 1));
				break;
			}
		}
		return result;
	}

	/**
	 * Give the player the reward for completing the quest.
	 *
	 * @param player
	 */
	protected void rewardPlayer(Player player) {
		player.addKarma(10.0);
		player.setATKXP(player.getATKXP() + (int) (player.getXP() * ATK_REWARD_RATE));
		player.incATKXP();
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		step_1();
		step_2();
		step_3();
	}
}
