package games.stendhal.server.maps.quests;

import games.stendhal.common.Grammar;
import games.stendhal.common.Level;
import games.stendhal.common.MathHelper;
import games.stendhal.common.Rand;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * QUEST: Daily Item Fetch Quest.
 * <p>
 * PARTICIPANTS:
 * <li> Mayor of Ados
 * <li> some items
 * <p>
 * STEPS:
 * <li> talk to Mayor of Ados to get a quest to fetch an item
 * <li> bring the item to the mayor
 * <li> if you cannot bring it in one week he offers you the chance to fetch
 * another instead
 * <p>
 * REWARD:
 * <li> xp
 * <p>
 * REPETITIONS:
 * <li> once a day
 */
public class DailyItemQuest extends AbstractQuest {

	private static final String QUEST_SLOT = "daily_item";

	class DailyQuestAction extends SpeakerNPC.ChatAction {

		/**
		 * All items which are possible/easy enough to find. If you want to do
		 * it better, go ahead. *
		 */
		private final List<String> listeditems = Arrays.asList("knife",
				"dagger", "short sword", "sword", "scimitar", "katana",
				"claymore", "broadsword", "biting sword", "old scythe",
				"small axe", "hand axe", "axe", "battle axe", "bardiche",
				"scythe", "twoside axe", "halberd", "club", "staff", "mace",
				"flail", "morning star", "hammer", "war hammer",
				"wooden bow", "longbow", "wooden arrow", "steel arrow",
				"buckler", "wooden shield", "studded shield", "plate shield",
				"lion shield", "unicorn shield", "skull shield",
				"crown shield", "dress", "leather armor", "leather cuirass",
				"studded armor", "chain armor", "scale armor", "plate armor",
				"leather helmet", "studded helmet", "chain helmet",
				"leather legs", "studded legs", "chain legs", "leather boots",
				"studded boots", "cloak", "elf cloak", "dwarf cloak",
				"green dragon cloak", "cheese", "carrot", "salad", "apple",
				"bread", "meat", "ham", "sandwich", "pie", "button mushroom",
				"porcini", "toadstool", "beer", "wine", "minor potion",
				"antidote", "greater antidote", "potion", "greater potion",
				"poison", "flask", "money", "arandula", "wood", "grain",
				"flour", "iron ore", "iron", "dice", "teddy", "perch", "roach",
				"char", "trout", "surgeonfish", "onion", "leek", "clownfish",
            	"leather scale armor", "pauldroned leather cuirass",
            	"enhanced chainmail", "iron scale armor", "golden chainmail",
            	"pauldroned iron cuirass", "blue elf cloak", "enhanced mace",
            	"golden mace", "golden hammer", "aventail", "composite bow",
            	"enhanced lion shield", "spinach", "courgette", "collard",
				"fish pie", "chicken", "elvish armor", "elvish boots",
				"sclaria", "kekik", "elvish cloak", "elvish legs",
				"shuriken", "coconut", "sapphire", "emerald", "carbuncle",			       
				"cauliflower", "broccoli", "gold nugget", "gold bar",
				"pineapple", "pina colada", "ice cream");

		@Override
		public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
			String questInfo = player.getQuest("daily_item");
			String questKill = null;
			String questCount = null;
			String questLast = null;
			
			long delay = MathHelper.MILLISECONDS_IN_ONE_DAY; 

			long expireDelay = MathHelper.MILLISECONDS_IN_ONE_WEEK; 

			if (questInfo != null) {
				String[] tokens = (questInfo + ";0;0;0").split(";");
				questKill = tokens[0];
				questLast = tokens[1];
				questCount = tokens[2];
			}
			if ((questKill != null) && !"done".equals(questKill)) {
				String sayText = "You're already on a quest to fetch "
						+ Grammar.a_noun(questKill)
						+ ". Say #complete if you brought it!";
				if (questLast != null) {
					long timeRemaining = (Long.parseLong(questLast) + expireDelay)
							- System.currentTimeMillis();

					if (timeRemaining < 0L) {
						engine.say(sayText
								+ " Perhaps there are no supplies of that left at all! You could fetch #another item if you like, or return with what I first asked you.");
						return;
					}
				}
				engine.say(sayText);
				return;
			}

			if (questLast != null) {
				long timeRemaining = (Long.parseLong(questLast) + delay)
						- System.currentTimeMillis();

				if (timeRemaining > 0L) {
					engine.say("I can only give you a new quest once a day. Please check back in "
							+ TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L))
							+ ".");
					return;
				}
			}
			String itemName = Rand.rand(listeditems);
			engine.say("Ados is in need of supplies. Go fetch "
					+ Grammar.a_noun(itemName)
					+ " and say #complete, once you've brought it.");
			questLast = "" + (new Date()).getTime();
			player.setQuest("daily_item", itemName + ";" + questLast + ";"
					+ questCount);
		}
	}

	class DailyQuestCompleteAction extends SpeakerNPC.ChatAction {
		@Override
		public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
			String questInfo = player.getQuest("daily_item");
			String questKill = null;
			String questCount = null;
			String questLast = null;

			if (questInfo == null) {
				engine.say("I'm afraid I didn't send you on a #quest yet.");
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
				int reward = (next - start) / 5;
				if (player.getLevel() >= Level.maxLevel()) {
					reward = 0;
				}
				engine.say("Good work! Let me thank you on behalf of the people of Ados!");
				player.addXP(reward);
				questCount = "" + (Integer.valueOf(questCount).intValue() + 1);
				questLast = "" + (new Date()).getTime();
				player.setQuest("daily_item", "done" + ";" + questLast + ";"
						+ questCount);
			} else {
				engine.say("You didn't fetch "
						+ Grammar.a_noun(questKill)
						+ " yet. Go and get it and say #complete only once you're done.");
			}
		}
	}

	class DailyQuestAbortAction extends SpeakerNPC.ChatAction {

		@Override
		public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
			String questInfo = player.getQuest("daily_item");
			String questKill = null;
			String questCount = null;
			String questLast = null;
			// Milliseconds in a week
			long expireDelay = MathHelper.MILLISECONDS_IN_ONE_WEEK; 

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

					if (timeRemaining < 0L) {
						engine.say("I see. Please, ask me for another #quest when you think you can help Ados again.");
						// Don't make the player wait any longer and don't
						// credit the player with a count increase?
						// questCount = "" + (Integer.valueOf(questCount) + 1 );
						// questLast = "" + (new Date()).getTime();
						player.setQuest("daily_item", "done" + ";" + questLast
								+ ";" + questCount);
						return;
					}
				}
				engine.say("It hasn't been long since you've started your quest, I won't let you give up so soon.");
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
		SpeakerNPC npc = npcs.get("Mayor Chalmers");
		npc.add(ConversationStates.ATTENDING, Arrays.asList("quest", "task"),
				null, ConversationStates.ATTENDING, null,
				new DailyQuestAction());
	}

	private void step_2() {
		// get the item
	}

	private void step_3() {

		SpeakerNPC npc = npcs.get("Mayor Chalmers");

		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("complete", "done"), null,
				ConversationStates.ATTENDING, null,
				new DailyQuestCompleteAction());
	}

	private void step_4() {
		SpeakerNPC npc = npcs.get("Mayor Chalmers");
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("another", "abort"), null,
				ConversationStates.ATTENDING, null, new DailyQuestAbortAction());
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
