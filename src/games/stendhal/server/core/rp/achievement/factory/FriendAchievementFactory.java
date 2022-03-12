/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.rp.achievement.factory;

import java.util.Collection;
import java.util.LinkedList;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.rp.achievement.Achievement;
import games.stendhal.server.core.rp.achievement.Category;
import games.stendhal.server.core.rp.achievement.condition.QuestWithPrefixCompletedCondition;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.player.Player;


/**
 * Factory for quest achievements
 *
 * @author kymara
 */
public class FriendAchievementFactory extends AbstractAchievementFactory {

	public static final String ID_CHILD_FRIEND = "friend.quests.children";
	public static final String ID_PRIVATE_DETECTIVE = "friend.quests.find";
	public static final String ID_GOOD_SAMARITAN = "friend.karma.250";
	public static final String ID_STILL_BELIEVING = "friend.meet.seasonal";


	@Override
	protected Category getCategory() {
		return Category.FRIEND;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		final LinkedList<Achievement> achievements = new LinkedList<Achievement>();

	    // TODO: add Pacifist achievement for not participating in pvp for 6 months or more (last_pvp_action_time)

		// Befriend Susi and complete quests for all children
		achievements.add(createAchievement(
			ID_CHILD_FRIEND, "Childrens' Friend",
			"Complete quests for all children",
			Achievement.MEDIUM_BASE_SCORE, true,
			new AndCondition(
				// Susi Quest is never set to done, therefore we check just if the quest has been started (condition "anyFriends" from FoundGirl.java)
				new QuestStartedCondition("susi"),
				// Help Tad, Semos Town Hall (Medicine for Tad)
				new QuestCompletedCondition("introduce_players"),
				// Plink, Semos Plains North
				new QuestCompletedCondition("plinks_toy"),
				// Anna, in Ados
				new QuestCompletedCondition("toys_collector"),
				// Sally, Orril River
				// 'completed' doesn't work for Sally - return player.hasQuest(QUEST_SLOT) && !"start".equals(player.getQuest(QUEST_SLOT)) && !"rejected".equals(player.getQuest(QUEST_SLOT));
				new AndCondition(
					new QuestActiveCondition("campfire"),
					new QuestNotInStateCondition("campfire", "start")),
				// Annie, Kalavan city gardens
				new QuestStateStartsWithCondition("icecream_for_annie","eating;"),
				// Elisabeth, Kirdneh
				new QuestStateStartsWithCondition("chocolate_for_elisabeth","eating;"),
				// Jef, Kirdneh
				new QuestCompletedCondition("find_jefs_mom"),
				// Hughie, Ados farmhouse
				new AndCondition(
					new QuestActiveCondition("fishsoup_for_hughie"),
					new QuestNotInStateCondition("fishsoup_for_hughie", "start")),
				// Finn Farmer, George
				new QuestCompletedCondition("coded_message"),
				// Marianne, Deniran City S
				new AndCondition(
						new QuestActiveCondition("eggs_for_marianne"),
						new QuestNotInStateCondition("eggs_for_marianne", "start"))
				)));

		// quests about finding people
		achievements.add(createAchievement(
			ID_PRIVATE_DETECTIVE, "Private Detective",
			"Find all lost and hidden people",
			Achievement.HARD_BASE_SCORE, true,
			new AndCondition(
				// Rat Children (Agnus)
				new QuestCompletedCondition("find_rat_kids"),
				// Find Ghosts (Carena)
				new QuestCompletedCondition("find_ghosts"),
				// Meet Angels (any of the cherubs)
				new ChatCondition() {
					@Override
					public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
						if (!player.hasQuest("seven_cherubs")) {
							return false;
						}
						final String npcDoneText = player.getQuest("seven_cherubs");
						final String[] done = npcDoneText.split(";");
						final int left = 7 - done.length;
						return left < 0;
					}
				})));

		// earn over 250 karma
		achievements.add(createAchievement(
			ID_GOOD_SAMARITAN, "Good Samaritan",
			"Earn a very good karma",
			Achievement.MEDIUM_BASE_SCORE, true,
			new ChatCondition() {
				@Override
				public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
					return player.getKarma() > 250;
				}
			}));

		// meet Santa Claus and Easter Bunny
		achievements.add(createAchievement(
			ID_STILL_BELIEVING, "Still Believing",
			"Meet Santa Claus and Easter Bunny",
			Achievement.EASY_BASE_SCORE, true,
			new AndCondition(
				new QuestWithPrefixCompletedCondition("meet_santa_"),
				new QuestWithPrefixCompletedCondition("meet_bunny_"))));

		return achievements;
	}
}
