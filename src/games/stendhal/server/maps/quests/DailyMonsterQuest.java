package games.stendhal.server.maps.quests;

import games.stendhal.common.Level;
import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.LevelBasedComparator;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * QUEST: Daily Monster Kill Quest.
 * <p>
 * PARTICIPANTS:
 * <li> Mayor
 * <li> some creatures
 * <p>
 * STEPS:
 * <li> talk to Mayor to get a quest to kill one of a named creature class
 * <li> kill one creature of that class
 * <li> tell Mayor that you are done
 * <li> if after 7 days you were not able to kill the creature, you have an
 * option to get another quest
 * <p>
 * REWARD: - xp - 5 karma
 * <p>
 * REPETITIONS: - once a day
 */

public class DailyMonsterQuest extends AbstractQuest {

	private static final String QUEST_SLOT = "daily";

	class DailyQuestAction implements ChatAction {

		/** All creatures, sorted by level. */
		private List<Creature> sortedcreatures;

		private String debugString;

		public DailyQuestAction() {
			refreshCreatureList();
		}

		private void refreshCreatureList() {
			final Collection<Creature> creatures = SingletonRepository.getEntityManager().getCreatures();
			sortedcreatures = new LinkedList<Creature>();
			for (Creature creature : creatures) {
				if (!creature.isRare()) {
					sortedcreatures.add(creature);
				}
			}
			Collections.sort(sortedcreatures, new LevelBasedComparator());
		}
		
		public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {

			final String questInfo = player.getQuest("daily");
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

			if ((questKill != null) && !"done".equals(questKill)) {
				final String sayText = "You're already on a quest to slay a "
						+ questKill + ". Say #complete if you're done with it!";
				if (questLast != null) {
					final long timeRemaining = (Long.parseLong(questLast) + expireDelay)
							- System.currentTimeMillis();

					if (timeRemaining < 0L) {
						engine.say(sayText
								+ " If you can't find one, perhaps it won't bother Semos either. You could kill #another creature if you like.");
						return;
					}
				}
				engine.say(sayText);
				return;
			}

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
			refreshCreatureList();
			// Creature selection magic happens here
			final Creature pickedCreature = pickIdealCreature(player.getLevel(),
					false, sortedcreatures);

			// shouldn't happen
			if (pickedCreature == null) {
				engine.say("Thanks for asking, but there's nothing you can do for me now.");
				return;
			}

			String creatureName = pickedCreature.getName();

			// don't ask level 0 players to kill a bat as this cannot be found
			// anywhere they have a chance to survive.
			if ("bat".equals(creatureName)) {
				creatureName = "rat";
			}
			
			engine.say("Semos is in need of help. Go kill a " + creatureName
					+ " and say #complete, once you're done.");
			player.removeKill(creatureName);
			questLast = "" + (new Date()).getTime();
			player.setQuest("daily", creatureName + ";" + questLast + ";"
					+ questCount);

		}

		// Returns a random creature near the players level, returns null if
		// there is a bug.
		// The ability to set a different level is for testing purposes
		public Creature pickIdealCreature(final int level, final boolean testMode, final List<Creature> creatureList) {
			// int level = player.getLevel();

			// start = lower bound, current = upper bound, for the range of
			// acceptable monsters for this specific player
			int current = -1;
			int start = 0;

			boolean lowerBoundIsSet = false;
			for (final Creature creature : creatureList) {
				current++;
				// Set the strongest creature
				if (creature.getLevel() > level + 5) {
					current--;
					break;
				}
				// Set the weakest creature
				if ((!lowerBoundIsSet) && (creature.getLevel() > 0)
						&& (creature.getLevel() >= level - 5)) {
					start = current;
					lowerBoundIsSet = true;
				}
			}

			// possible with low lvl player and no low lvl creatures.
			if (current < 0) {
				current = 0;
			}

			// possible if the player is ~5 levels higher than the highest level
			// creature
			if (!lowerBoundIsSet) {
				start = current;
			}

			// make sure the pool of acceptable monsters is at least
			// minSelected, the additional creatures will be weaker
			if (current >= start) {
				final int minSelected = 5;
				final int numSelected = current - start + 1;
				if (numSelected < minSelected) {
					start = start - (minSelected - numSelected);
					// don't let the lower bound go too low
					if (start < 0) {
						start = 0;
					}
				}
			}

			// shouldn't happen
			if ((current < start) || (start < 0)
					|| (current >= creatureList.size())) {
				if (testMode) {
					debugString += "\r\n" + level + " : ERROR start=" + start
							+ ", current=" + current;
				}
				return null;
			}

			// pick a random creature from the acceptable range.
			final int result = start + new Random().nextInt(current - start + 1);
			final Creature cResult = creatureList.get(result);

			if (testMode) {
				debugString += "\r\n" + level + " : OK start=" + start
						+ ", current=" + current + ", result=" + result
						+ ", cResult=" + cResult.getName() + ". OPTIONS: ";
				for (int i = start; i <= current; i++) {
					final Creature cTemp = creatureList.get(i);
					debugString += cTemp.getName() + ":" + cTemp.getLevel()
							+ "; ";
				}
			}

			return cResult;

		}

		// Debug Only, Preforms tests
		// Populates debugString with test data.
		public void testAllLevels() {
			debugString = "";
			int max = Level.maxLevel();
			// in case max level is set to infinity in the future.
			if (max > 1100) {
				max = 1100;
			}
			for (int i = 0; i <= max; i++) {
				pickIdealCreature(i, true, sortedcreatures);
			}
		}
	}

	class DailyQuestCompleteAction implements ChatAction {

		public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
			final String questInfo = player.getQuest("daily");
			String questKill = null;
			String questCount = null;
			String questLast = null;

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
			if ("done".equals(questKill)) {
				engine.say("You already completed the last quest I had given to you.");
				return;
			}
			if (player.hasKilled(questKill)) {
				final int start = Level.getXP(player.getLevel());
				final int next = Level.getXP(player.getLevel() + 1);
				int reward = (next - start) / 5;
				if (player.getLevel() >= Level.maxLevel()) {
					reward = 0;
					// no reward so give a lot karma instead, 100 in all
					player.addKarma(95.0);
				}
				engine.say("Good work! Let me thank you in the name of the people of Semos!");
				player.addXP(reward);
				player.addKarma(5.0);
				questCount = "" + (Integer.valueOf(questCount) + 1);
				questLast = "" + (new Date()).getTime();
				player.setQuest("daily", "done" + ";" + questLast + ";"
						+ questCount);
			} else {
				engine.say("You didn't kill a "
						+ questKill
						+ " yet. Go and do it and say #complete only after you're done.");
			}
		}
	}

	class DailyQuestAbortAction implements ChatAction {

		public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
			final String questInfo = player.getQuest("daily");
			String questKill = null;
			String questCount = null;
			String questLast = null;
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

					if (timeRemaining < 0L) {
						engine.say("As you wish, ask me for another #quest when you think you have what it takes to help Semos again.");
						// Don't make the player wait any longer and don't
						// credit the player with a count increase?
						// questCount = "" + (Integer.valueOf(questCount) + 1 );
						// questLast = "" + (new Date()).getTime();
						player.setQuest("daily", "done" + ";" + questLast + ";"
								+ questCount);
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
			final String questKill = tokens[0];
			if (!player.hasKilled(questKill)) {
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
		final SpeakerNPC npc = npcs.get("Mayor Sakhs");
		npc.add(ConversationStates.ATTENDING, Arrays.asList("quest", "task"),
				null, ConversationStates.ATTENDING, null,
				new DailyQuestAction());
	}

	private void step_2() {
		// kill the monster
	}

	private void step_3() {
		final SpeakerNPC npc = npcs.get("Mayor Sakhs");

		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("complete", "done"), null,
				ConversationStates.ATTENDING, null,
				new DailyQuestCompleteAction());
	}

	private void step_4() {
		final SpeakerNPC npc = npcs.get("Mayor Sakhs");
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

	@Override
	public String getName() {
		return "DailyMonsterQuest";
	}

}
