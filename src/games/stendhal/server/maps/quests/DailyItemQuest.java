package games.stendhal.server.maps.quests;

import games.stendhal.common.Grammar;
import games.stendhal.common.Level;
import games.stendhal.common.MathHelper;
import games.stendhal.common.Rand;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

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
 * <li> 10 Karma
 * <p>
 * REPETITIONS:
 * <li> once a day
 */
public class DailyItemQuest extends AbstractQuest {

	private static final String QUEST_SLOT = "daily_item";

	
	/**
	 * All items which are possible/easy enough to find. If you want to do
	 * it better, go ahead. *
	 * not to use yet, just getting it ready.
	 */
	private static Map<String,Integer> items;

	private static void buildItemsMap() {
		items = new HashMap<String, Integer>();
		items.put("knife",1);
		items.put("dagger",1);
		items.put("short sword",1);
		items.put("sword",1);
		items.put("scimitar",1);
		items.put("katana",1);
		items.put("claymore",1);
		items.put("broadsword",1);
		items.put("biting sword",1);
		items.put("old scythe",1);
		items.put("small axe",1);
		items.put("hand axe",1);
		items.put("axe",1);
		items.put("battle axe",1);
		items.put("bardiche",1);
		items.put("scythe",1);
		items.put("twoside axe",1);
		items.put("halberd",1);
		items.put("club",1);
		items.put("staff",1);
		items.put("mace",1);
		items.put("flail",1);
		items.put("morning star",1);
		items.put("hammer",1);
		items.put("war hammer",1);
		items.put("wooden bow",1);
		items.put("longbow",1);
		items.put("wooden arrow",1);
		items.put("steel arrow",1);
		items.put("buckler",1);
		items.put("wooden shield",1);
		items.put("studded shield",1);
		items.put("plate shield",1);
		items.put("lion shield",1);
		items.put("unicorn shield",1);
		items.put("skull shield",1);
		items.put("crown shield",1);
		items.put("dress",1);
		items.put("leather armor",1);
		items.put("leather cuirass",1);
		items.put("studded armor",1);
		items.put("chain armor",1);
		items.put("scale armor",1);
		items.put("plate armor",1);
		items.put("leather helmet",1);
		items.put("studded helmet",1);
		items.put("chain helmet",1);
		items.put("leather legs",1);
		items.put("studded legs",1);
		items.put("chain legs",1);
		items.put("leather boots",1);
		items.put("studded boots",1);
		items.put("cloak",1);
		items.put("elf cloak",1);
		items.put("dwarf cloak",1);
		items.put("green dragon cloak",1);
		items.put("cheese",10);
		items.put("carrot",10);
		items.put("salad",10);
		items.put("apple",5);
		items.put("bread",5);
		items.put("meat",10);
		items.put("ham",10);
		items.put("sandwich",5);
		items.put("pie",5);
		items.put("button mushroom",10);
		items.put("porcini",10);
		items.put("toadstool",15);
		items.put("beer",10);
		items.put("wine",10);
		items.put("minor potion",5);
		items.put("antidote",5);
		items.put("greater antidote",5);
		items.put("potion",5);
		items.put("greater potion",5);
		items.put("poison",5);
		items.put("flask",5);
		items.put("money",100);
		items.put("arandula",5);
		items.put("wood",10);
		items.put("grain",20);
		items.put("flour",5);
		items.put("iron ore",10);
		items.put("iron",5);
		items.put("dice",1);
		items.put("teddy",1);
		items.put("perch",5);
		items.put("roach",5);
		items.put("char",5);
		items.put("trout",5);
		items.put("surgeonfish",5);
		items.put("onion",5);
		items.put("leek",5);
		items.put("clownfish",5);
		items.put("leather scale armor",1);
		items.put("pauldroned leather cuirass",1);
		items.put("enhanced chainmail",1);
		items.put("iron scale armor",1);
		items.put("golden chainmail",1);
		items.put("pauldroned iron cuirass",1);
		items.put("blue elf cloak",1);
		items.put("enhanced mace",1);
		items.put("golden mace",1);
		items.put("golden hammer",1);
		items.put("aventail",1);
		items.put("composite bow",1);
		items.put("enhanced lion shield",1);
		items.put("spinach",5);
		items.put("courgette",5);
		items.put("collard",5);
	}
	
	static class DailyQuestAction implements ChatAction {

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
				"sclaria", "kekik", "elvish cloak", "elvish legs", "elvish sword",
				"shuriken", "coconut", "sapphire", "emerald", "carbuncle",			       
				"cauliflower", "broccoli", "gold nugget", "gold bar",
				"pineapple", "pina colada", "icecream", "black pearl", "tea",
				"milk", "canned tuna", "snowglobe", "tomato", "fairy cake", 
				"pizza", "chocolate bar", "tuna sandwich", "sausage", "cheese sausage",
				"hotdog", "cheeseydog", "vanilla shake", "chocolate shake", "honey",
				"licorice", "marbles", "robins hat", "soup", "deadly poison", 
				"disease poison", "mega poison");

		public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
			final String questInfo = player.getQuest("daily_item");
			String questKill = null;
			String questCount = null;
			String questLast = null;
			
			final long delay = MathHelper.MILLISECONDS_IN_ONE_DAY; 

			final long expireDelay = MathHelper.MILLISECONDS_IN_ONE_WEEK; 

			if (questInfo != null) {
				final String[] tokens = (questInfo + ";0;0;0").split(";");
				questKill = tokens[0];
				questLast = tokens[1];
				questCount = tokens[2];
			}
			
			// if the quest state is not empty, but does not start with a ; (?) and not done, and enough time has not passed : state required item
			if ((questKill != null) && !"done".equals(questKill)) {
				final String sayText = "You're already on a quest to fetch "
						+ Grammar.a_noun(questKill)
						+ ". Say #complete if you brought it!";
				// if the quest state starts with a ; or a done but time passed :  state required item and then offer new quest
				if (questLast != null) {
					final long timeRemaining = (Long.parseLong(questLast) + expireDelay)
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
			// quest starts with done; and time not passed : state time remaining
			if (questLast != null) {
				final long timeRemaining = (Long.parseLong(questLast) + delay)
						- System.currentTimeMillis();

				if (timeRemaining > 0L) {
					engine.say("I can only give you a new quest once a day. Please check back in "
							+ TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L))
							+ ".");
					return;
				}
			}
			// quest starts with done; and time passed : start recording a new required item thing and timestamp the slot
			final String itemName = Rand.rand(listeditems);
			engine.say("Ados is in need of supplies. Go fetch "
					+ Grammar.a_noun(itemName)
					+ " and say #complete, once you've brought it.");
			questLast = "" + (new Date()).getTime();
			player.setQuest("daily_item", itemName + ";" + questLast + ";"
					+ questCount);
		}
	}

	static class DailyQuestCompleteAction implements ChatAction {
		public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
			final String questInfo = player.getQuest("daily_item");
			String questKill = null;
			String questCount = null;
			String questLast = null;
			// quest not started: say this text
			if (questInfo == null) {
				engine.say("I'm afraid I didn't send you on a #quest yet.");
				return;
			}
			final String[] tokens = (questInfo + ";0;0").split(";");
			questKill = tokens[0];
			questLast = tokens[1];
			questCount = tokens[2];
			if (questCount.equals("null")) {
				questCount = "0";
			}
			// quest completed: say this text
			if ("done".equals(questKill)) {
				engine.say("You already completed the last quest I had given to you.");
				return;
			}
			// quest active, and player has the required item with him
			// then, increase xp (can't use action here :/ )
			// add karma (again depends on xp/level)
			// consider making a increase xp action for this kind as a few quests need it, need the fraction like here 8 and in daily monster 5, to be a parameter
			// say something
			// set quest done
			// timestamp quest
			// increment quest
			if (player.drop(questKill)) {
				final int start = Level.getXP(player.getLevel());
				final int next = Level.getXP(player.getLevel() + 1);
				int reward = (next - start) / 8;
				if (player.getLevel() >= Level.maxLevel()) {
					reward = 0;
					// no reward so give a lot karma instead, 100 in all
					player.addKarma(90.0);
				}
				engine.say("Good work! Let me thank you on behalf of the people of Ados!");
				player.addXP(reward);
				player.addKarma(10.0);
				questCount = "" + (Integer.valueOf(questCount).intValue() + 1);
				questLast = "" + (new Date()).getTime();
				player.setQuest("daily_item", "done" + ";" + questLast + ";"
						+ questCount);
			} else {			
				// quest active, and player does not have  required item with him
				// just need to use the state required item action				
				engine.say("You didn't fetch "
						+ Grammar.a_noun(questKill)
						+ " yet. Go and get it and say #complete only once you're done.");
			}
		}
	}

	static class DailyQuestAbortAction implements ChatAction {

		public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
			final String questInfo = player.getQuest("daily_item");
			String questKill = null;
			String questCount = null;
			String questLast = null;
			// Milliseconds in a week
			final long expireDelay = MathHelper.MILLISECONDS_IN_ONE_WEEK; 

			if (questInfo != null) {
				final String[] tokens = (questInfo + ";0;0;0").split(";");
				questKill = tokens[0];
				questLast = tokens[1];
				questCount = tokens[2];
			}


			if ((questKill != null) && !"done".equals(questKill)) {
				if (questLast != null) {
					final long timeRemaining = (Long.parseLong(questLast) + expireDelay)
							- System.currentTimeMillis();
					
					// quest active, the state doesn't start with ; or done, and enough time has passed 
					// set to done; only and preserve the rest					
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
				// quest active, the state doesn't start with ; or done, and enough time has not passed 
				engine.say("It hasn't been long since you've started your quest, I won't let you give up so soon.");
				return;
			}
			// quest started with ; or is done or is empty
			engine.say("I'm afraid I didn't send you on a #quest yet.");
		}
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
		res.add("FIRST_CHAT");
		final String questState = player.getQuest(QUEST_SLOT);
		if ("rejected".equals(questState)) {
			res.add("QUEST_REJECTED");
		}
		if (player.hasQuest(QUEST_SLOT) && !player.isQuestCompleted(QUEST_SLOT)) {
			final String[] tokens = (questState + ";0;0;0").split(";");
			final String questItem = tokens[0];
			if (!player.isEquipped(questItem)) {
				res.add("QUEST_ACTIVE");
			} else {
				res.add("QUEST_UNCLAIMED");
			}
		}
		if (player.isQuestCompleted(QUEST_SLOT)) {
			final String[] tokens = (questState + ";0;0;0").split(";");
			final String questLast = tokens[1];
			final long timeRemaining = (Long.parseLong(questLast) + MathHelper.MILLISECONDS_IN_ONE_DAY)
			- System.currentTimeMillis();

			if (timeRemaining > 0L) {
				res.add("DONE_TODAY");
			} else {
				res.add("DONE_REPEATABLE");
			}
		}
		return res;
	}
	
	private void step_1() {
		final SpeakerNPC npc = npcs.get("Mayor Chalmers");
		npc.add(ConversationStates.ATTENDING, Arrays.asList("quest", "task"),
				null, ConversationStates.ATTENDING, null,
				new DailyQuestAction());
	}

	private void step_2() {
		// get the item
	}

	private void step_3() {
		final SpeakerNPC npc = npcs.get("Mayor Chalmers");

		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("complete", "done"), null,
				ConversationStates.ATTENDING, null,
				new DailyQuestCompleteAction());
	}

	private void step_4() {
		final SpeakerNPC npc = npcs.get("Mayor Chalmers");
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("another", "abort"), null,
				ConversationStates.ATTENDING, null, new DailyQuestAbortAction());
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		
		buildItemsMap();
		
		step_1();
		step_2();
		step_3();
		step_4();
	}

	@Override
	public String getName() {
		return "DailyItemQuest";
	}

	@Override
	public int getMinLevel() {
		return 0;
	}
}
