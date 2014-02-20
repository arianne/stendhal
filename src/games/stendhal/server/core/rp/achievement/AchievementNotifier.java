/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.rp.achievement;

import games.stendhal.common.constants.SoundLayer;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.db.AchievementDAO;
import games.stendhal.server.core.engine.dbcommand.WriteReachedAchievementCommand;
import games.stendhal.server.core.rp.achievement.factory.AbstractAchievementFactory;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.player.ReadAchievementsOnLogin;
import games.stendhal.server.entity.player.UpdatePendingAchievementsOnLogin;
import games.stendhal.server.events.ReachedAchievementEvent;
import games.stendhal.server.events.SoundEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marauroa.server.db.command.DBCommandQueue;
import marauroa.server.game.db.DAORegister;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * Checks for reached achievements and marks them as reached for a player if he has fulfilled them
 *
 * @author madmetzger
 */
public final class AchievementNotifier {

	private static final Logger logger = Logger.getLogger(AchievementNotifier.class);

	private static AchievementNotifier instance;

	final private Map<Category, List<Achievement>> achievements;

	final private Map<String, Integer> identifiersToIds;

	private AchievementNotifier() {
		achievements = new HashMap<Category, List<Achievement>>();
		identifiersToIds = new HashMap<String, Integer>();
	}

	/**
	 * singleton accessor method
	 *
	 * @return the AchievementNotifier
	 */
	public static AchievementNotifier get() {
    	synchronized(AchievementNotifier.class) {
			if(instance == null) {
				instance = new AchievementNotifier();
			}
    	}
		return instance;
	}

	/**
	 * initializes the achievements that are available and registers the login listener
	 * new added achievements are added to the achievements table
	 */
	public void initialize() {
		//read all configured achievements and put them into the categorized map
		final Map<String, Achievement> allAchievements = createAchievements();
		for(Achievement a : allAchievements.values()) {
			if(!achievements.containsKey(a.getCategory())) {
				achievements.put(a.getCategory(), new LinkedList<Achievement>());
			}
			achievements.get(a.getCategory()).add(a);
		}
		//collect all identifiers from database
		final Map<String, Integer> allIdentifiersInDatabase = collectAllIdentifiersFromDatabase();
		//update stored data with configured achievements
		identifiersToIds.putAll(allIdentifiersInDatabase);
		for(Map.Entry<String, Integer> it : allIdentifiersInDatabase.entrySet()) {
			final String identifier = it.getKey();
			final Achievement achievement = allAchievements.get(identifier);
			// this happens if an achievement is not configured anymore but already in the database
			// in that case we should keep it as players could have reached it
			// useful to stop checking for a certain achievement but keep results
			if (achievement != null) {
				try {
					DAORegister.get().get(AchievementDAO.class).updateAchievement(it.getValue(), achievement);
				} catch (SQLException e) {
					logger.error("Error while updating existing achievement " + achievement.getTitle(), e);
				}
			}
		}
		// remove already stored achievements before saving them
		for(String identifier : allIdentifiersInDatabase.keySet()) {
			allAchievements.remove(identifier);
		}
		//save new achievements and add their identifier and id to the identifierToId map
		for (Achievement a : allAchievements.values()) {
			try {
				Integer id = DAORegister.get().get(AchievementDAO.class).insertAchievement(a);
				identifiersToIds.put(a.getIdentifier(), id);
			} catch (SQLException e) {
				logger.error("Error while saving new achievement "+a.getTitle(), e);
			}
		}
		// register the login notifier that checks for each player the pending achievements on login
		SingletonRepository.getLoginNotifier().addListener(new UpdatePendingAchievementsOnLogin());
		// register the login notifier that checks for each player the reached achievements on login
		SingletonRepository.getLoginNotifier().addListener(new ReadAchievementsOnLogin());
	}

	/**
	 * collects all identifiers from the database
	 *
	 * @return a set of all identifier strings
	 */
	private Map<String, Integer> collectAllIdentifiersFromDatabase() {
		Map<String, Integer> mapFromDB = new HashMap<String, Integer>();
		try {
			mapFromDB = DAORegister.get().get(AchievementDAO.class).loadIdentifierIdPairs();
		} catch (SQLException e) {
			logger.error("Error while loading Identifier to id map for achievements.", e);
		}
		return mapFromDB;
	}

	/**
	 * checks all for level change relevant achievements for a player
	 *
	 * @param player
	 */
	public void onLevelChange(Player player) {
		getAndCheckAchievementsInCategory(player, Category.EXPERIENCE);
	}


	/**
	 * checks all achievements for a player that should be checked when a player kills sth
	 *
	 * @param player
	 */
	public void onKill(Player player) {
		getAndCheckAchievementsInCategory(player, Category.FIGHTING);
	}

	/**
	 * check all achievements for a player that are relevant on finishing a quest
	 *
	 * @param player
	 */
	public void onFinishQuest(Player player) {
		getAndCheckAchievementsInCategory(player, Category.QUEST);
		getAndCheckAchievementsInCategory(player, Category.QUEST_ADOS_ITEMS);
		getAndCheckAchievementsInCategory(player, Category.QUEST_SEMOS_MONSTER);
		getAndCheckAchievementsInCategory(player, Category.QUEST_KIRDNEH_ITEM);
		getAndCheckAchievementsInCategory(player, Category.FRIEND);
		getAndCheckAchievementsInCategory(player, Category.OBTAIN);
		getAndCheckAchievementsInCategory(player, Category.PRODUCTION);
		getAndCheckAchievementsInCategory(player, Category.QUEST_MITHRILBOURGH_ENEMY_ARMY);
	}

	/**
	 * check all achievements for a player that belong to the zone category
	 *
	 * @param player
	 */
	public void onZoneEnter(Player player) {
		getAndCheckAchievementsInCategory(player, Category.OUTSIDE_ZONE);
		getAndCheckAchievementsInCategory(player, Category.UNDERGROUND_ZONE);
		getAndCheckAchievementsInCategory(player, Category.INTERIOR_ZONE);
	}

	/**
	 * check all achievements for a player that belong to the age category
	 *
	 * @param player
	 */
	public void onAge(Player player) {
		getAndCheckAchievementsInCategory(player, Category.AGE);
	}

	/**
	 * check all achievements for a player that belong to the item category
	 *
	 * @param player
	 */
	public void onItemLoot(Player player) {
		getAndCheckAchievementsInCategory(player, Category.ITEM);
	}

	/**
	 * check all achievements for a player that belong to the production category
	 *
	 * @param player
	 */
	public void onProduction(Player player) {
		getAndCheckAchievementsInCategory(player, Category.PRODUCTION);
	}

	/**
	 * check all achievements for a player that belong to the obtain category
	 *
	 * @param player
	 */
	public void onObtain(Player player) {
		getAndCheckAchievementsInCategory(player, Category.OBTAIN);
	}

	/**
	 * Award a player with an achievement that wasn't yet reached by the player
	 * (used for example in the wishing well)
	 *
	 * @param player the player object to award
	 * @param achievementIdentifier the identifier of the achievement that should be awarded
	 */
	public void awardAchievementIfNotYetReached(Player player, String achievementIdentifier) {
		if(!player.hasReachedAchievement(achievementIdentifier)) {
			boolean found = false;
			for(List<Achievement> achievementList : this.achievements.values()) {
				if(!found) {
					for(Achievement achievement : achievementList) {
						if (achievement.getIdentifier().equals(achievementIdentifier)) {
							logReachingOfAnAchievement(player, achievement);
							notifyPlayerAboutReachedAchievement(player, achievement);
							found = true;
						}
					}
				}
			}
			if(!found) {
				logger.warn("Tried to award non existing achievement identifier "+achievementIdentifier+" to "+player.getName());
			}
		}
	}

	/**
	 * Checks on login of a player which achievements the player has reached and gives a summarizing message
	 *
	 * @param player
	 */
	public void onLogin(Player player) {
		List<Achievement> toCheck = new ArrayList<Achievement>();
		//Avoid checking of zone achievements on login to
		//prevent double check when player is initially placed into a zone
		final Map<Category,List<Achievement>> map = new HashMap<Category, List<Achievement>>(achievements);
		map.remove(Category.OUTSIDE_ZONE);
		map.remove(Category.UNDERGROUND_ZONE);
		Collection<List<Achievement>> values = map.values();
		for (List<Achievement> list : values) {
			toCheck.addAll(list);
		}
		final List<Achievement> reached = checkAchievements(player, toCheck);
		// only send notice if actually a new added achievement was reached by doing nothing
		if(!reached.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append("You have reached ");
			sb.append(Integer.valueOf(reached.size()));
			sb.append(" new "+Grammar.plnoun(reached.size(), "achievement")+". Please check #http://stendhalgame.org for details.");
			player.sendPrivateText(sb.toString());
		}
	}

	/**
	 * retrieve all achievements for a category and check if player has reached each of the found achievements
	 *
	 * @param player
	 * @param category
	 */
	private void getAndCheckAchievementsInCategory(Player player, Category category) {
		if(achievements.containsKey(category)) {
			List<Achievement> toCheck = achievements.get(category);
			List<Achievement> reached = checkAchievements(player, toCheck);
			notifyPlayerAboutReachedAchievements(player, reached);
		}
	}

	/**
	 * Checks for each achievement if the player has reached it. in case of reaching
	 * an achievement it starts logging and notifying about reaching.
	 *
	 * @param player player to be checked
	 * @param toCheck list of checked achievements
	 * @return list of reached achievements
	 */
	private List<Achievement> checkAchievements(Player player,
			List<Achievement> toCheck) {
		List<Achievement> reached = new ArrayList<Achievement>();

		// continue checking only if player's achievements are already loaded from the database
		if (!player.arePlayerAchievementsLoaded()) {
			return reached;
		}

		for (Achievement achievement : toCheck) {
			if(achievement.isFulfilled(player) && !player.hasReachedAchievement(achievement.getIdentifier())) {
				logReachingOfAnAchievement(player, achievement);
				if (achievement.isActive()) {
					reached.add(achievement);
				}
			}
		}
		return reached;
	}

	/**
	 * Notifies a player about reached achievements via private message
	 *
	 * @param player
	 * @param achievementsToNotifyAbout list of achievements the player should
	 * 	be notified about
	 */
	private void notifyPlayerAboutReachedAchievements(Player player, List<Achievement> achievementsToNotifyAbout) {
		for (Achievement achievement : achievementsToNotifyAbout) {
			notifyPlayerAboutReachedAchievement(player, achievement);
		}
	}

	/**
	 * logs reached achievement to gameEvents table and reached_achievment table
	 *
	 * @param player
	 * @param achievement
	 */
	private void logReachingOfAnAchievement(Player player, Achievement achievement) {
		String identifier = achievement.getIdentifier();
		String title = achievement.getTitle();
		Category category = achievement.getCategory();
		String playerName = player.getName();
		DBCommandQueue.get().enqueue(new WriteReachedAchievementCommand(identifiersToIds.get(identifier), title, category, playerName));
		player.addReachedAchievement(achievement.getIdentifier());
		new GameEvent(playerName, "reach-achievement", category.toString(), title, identifier).raise();
	}

	/**
	 * notifies the player about reaching an achievement
	 *
	 * @param player
	 * @param achievement
	 */
	private void notifyPlayerAboutReachedAchievement(Player player, Achievement achievement) {
		if (achievement.isActive()) {
			player.addEvent(new ReachedAchievementEvent(achievement));
			player.addEvent(new SoundEvent("yay-1", SoundLayer.USER_INTERFACE));
			player.notifyWorldAboutChanges();
		}
	}

	/**
	 * creates all available achievements
	 *
	 * @return map with key identifier and value the identified achievement
	 */
	private Map<String, Achievement> createAchievements() {
		Map<String, Achievement> achievementMap = new HashMap<String, Achievement>();
		for(AbstractAchievementFactory factory : AbstractAchievementFactory.createFactories()) {
			for(Achievement a : factory.createAchievements()) {
				achievementMap.put(a.getIdentifier(), a);
			}
		}
		return achievementMap;
	}

	/**
	 * gets a list of all Achievements
	 *
	 * @return list of achievements
	 */
	public ImmutableList<Achievement> getAchievements() {
		Builder<Achievement> builder = ImmutableList.builder();
		for (List<Achievement> temp : achievements.values()) {
			builder.addAll(temp);
		}
		return builder.build();
	}

}
