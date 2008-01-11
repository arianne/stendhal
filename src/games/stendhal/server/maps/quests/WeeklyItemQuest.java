package games.stendhal.server.maps.quests;

import games.stendhal.common.Grammar;
import games.stendhal.common.Level;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * QUEST: Weekly Item Fetch Quest.
 * <p>
 * PARTICIPANTS:
 * <li> Hazel, Museum Curator of Kirdneh
 * <li> some items
 * <p>
 * STEPS:
 * <li> talk to Museum Curator to get a quest to fetch a rare item
 * <li> bring the item to the Museum Curator
 * <li> if you cannot bring it in 6 weeks she offers you the chance to fetch
 * another instead
 * <p>
 * REWARD:
 * <li> xp
 * <li> between 100 and 600 money
 * <p>
 * REPETITIONS:
 * <li> once a week
 */
public class WeeklyItemQuest extends AbstractQuest {

	private static final String QUEST_SLOT = "weekly_item";
	// prevent int overflow by casting to long
	private static final long expireDelay = (long) 6 * 60 * 60 * 24 * 7 * 1000; // Milliseconds in 6 week

	class WeeklyQuestAction extends SpeakerNPC.ChatAction {

		/**
		 * All items which are hard enough to find but not tooo hard and not in Daily quest. If you want to do
		 * it better, go ahead. *
		 */
		private final List<String> listeditems = Arrays.asList("mega_potion", "lucky_charm", "ice_sword", "fire_sword",
				"great_sword", "immortal_sword", "dark_dagger", "assassin_dagger", "night_dagger", "hell_dagger",
				"golden_cloak", "shadow_cloak", "chaos_cloak", "mainio_cloak", "obsidian", "diamond", "golden_legs",
				"shadow_legs", "golden_armor", "shadow_armor", "golden_shield", "shadow_shield", "skull_staff",
				"steel_boots", "golden_boots", "shadow_boots", "stone_boots", "chaos_boots", "golden_helmet",
				"shadow_helmet", "horned_golden_helmet", "chaos_helmet", "golden_twoside_axe", "drow_sword",
				"chaos_legs", "chaos_sword", "chaos_shield", "chaos_armor", "green_dragon_shield", "egg",
				"golden_arrow", "power_arrow", "mainio_legs", "mainio_boots", "mainio_shield", "mainio_armor");

		@Override
		public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
			String questInfo = player.getQuest("weekly_item");
			String questKill = null;
			String questCount = null;
			String questLast = null;
			long delay = 7 * 60 * 60 * 24 * 1000; // Milliseconds in a week

			if (questInfo != null) {
				String[] tokens = (questInfo + ";0;0;0").split(";");
				questKill = tokens[0];
				questLast = tokens[1];
				questCount = tokens[2];
			}
			if ((questKill != null) && !"done".equals(questKill)) {
				String sayText = "You're already on a quest to bring the museum "
						+ Grammar.a_noun(questKill)
						+ ". Please say #complete if you have it with you.";
				if (questLast != null) {
					long timeRemaining = (Long.parseLong(questLast) + expireDelay)
							- System.currentTimeMillis();

					if (timeRemaining < 0) {
						engine.say(sayText
								+ " But, perhaps that is now too rare an item. I can give you #another task, or you can return with what I first asked you.");
						return;
					}
				}
				engine.say(sayText);
				return;
			}

			if (questLast != null) {
				long timeRemaining = (Long.parseLong(questLast) + delay)
						- System.currentTimeMillis();

				if (timeRemaining > 0) {
					engine.say("The museum can only afford to send you to fetch an item once a week. Please check back in "
							+ TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L))
							+ ".");
					return;
				}
			}
			String itemName = Rand.rand(listeditems);
			engine.say("I want Kirdneh's museum to be the greatest in the land! Please fetch "
					+ Grammar.a_noun(itemName)
					+ " and say #complete, once you've brought it.");
			questLast = "" + (new Date()).getTime();
			player.setQuest("weekly_item", itemName + ";" + questLast + ";"
					+ questCount);
		}
	}

	class WeeklyQuestCompleteAction extends SpeakerNPC.ChatAction {
		@Override
		public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
			String questInfo = player.getQuest("weekly_item");
			String questKill = null;
			String questCount = null;
			String questLast = null;

			if (questInfo == null) {
				engine.say("I don't remember giving you any #task yet.");
				return;
			}
			String[] tokens = (questInfo + ";0;0").split(";");
			questKill = tokens[0];
			questLast = tokens[1];
			questCount = tokens[2];
			if (questCount.equals("null")) {
				questCount = "0";
			}
			if ("done".equals(questKill)) {
				engine.say("You already completed the last quest I had given to you.");
				return;
			}
			if (player.drop(questKill)) {
				int start = Level.getXP(player.getLevel());
				int next = Level.getXP(player.getLevel() + 1);
				int reward = 3 * (next - start) / 5;
				if (player.getLevel() >= Level.maxLevel()) {
					reward = 0;
				}
				int goldamount;
				StackableItem money = (StackableItem) StendhalRPWorld
								.get().getRuleManager().getEntityManager()
								.getItem("money");
				goldamount = 100 * Rand.roll1D6();
				money.setQuantity(goldamount);
				player.equip(money, true);
				engine.say("Wonderful! Here is " + Integer.toString(goldamount) + " money to cover your expenses.");
				player.addXP(reward);
				questCount = "" + (new Integer(questCount) + 1);
				questLast = "" + (new Date()).getTime();
				player.setQuest("weekly_item", "done" + ";" + questLast + ";"
						+ questCount);
			} else {
				engine.say("You don't seem to have "
						+ Grammar.a_noun(questKill)
						+ " with you. Please get it and say #complete only then.");
			}
		}
	}

	class WeeklyQuestAbortAction extends SpeakerNPC.ChatAction {

		@Override
		public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
			String questInfo = player.getQuest("weekly_item");
			String questKill = null;
			String questCount = null;
			String questLast = null;

			if (questInfo != null) {
				String[] tokens = (questInfo + ";0;0;0").split(";");
				questKill = tokens[0];
				questLast = tokens[1];
				questCount = tokens[2];
			}

			if ((questKill != null) && !"done".equals(questKill)) {
				if (questLast != null) {
					long timeRemaining = (Long.parseLong(questLast) + expireDelay)
							- System.currentTimeMillis();

					if (timeRemaining < 0) {
						engine.say("I see. Please, ask me for another #quest when you think you can help Kirdneh museum again.");
						// Don't make the player wait any longer and don't
						// credit the player with a count increase?
						// questCount = "" + (new Integer(questCount) + 1 );
						// questLast = "" + (new Date()).getTime();
						player.setQuest("weekly_item", "done" + ";" + questLast
								+ ";" + questCount);
						return;
					}
				}
				engine.say("It hasn't been long since you've started your quest, you shouldn't give up so soon.");
				return;
			}
			engine.say("I'm afraid I didn't send you on a #quest yet.");
		}
	}

	@Override
	public void init(String name) {
		super.init(name, QUEST_SLOT);
	}

	private void step_1() {
		SpeakerNPC npc = npcs.get("Hazel");
		npc.add(ConversationStates.ATTENDING, Arrays.asList("quest", "task", "exhibits"),
				null, ConversationStates.ATTENDING, null,
				new WeeklyQuestAction());
	}

	private void step_2() {
		// get the item
	}

	private void step_3() {

		SpeakerNPC npc = npcs.get("Hazel");

		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("complete", "done"), null,
				ConversationStates.ATTENDING, null,
				new WeeklyQuestCompleteAction());
	}

	private void step_4() {
		SpeakerNPC npc = npcs.get("Hazel");
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("another", "abort"), null,
				ConversationStates.ATTENDING, null, new WeeklyQuestAbortAction());
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		step_1();
		step_2();
		step_3();
		step_4();
	}

}
