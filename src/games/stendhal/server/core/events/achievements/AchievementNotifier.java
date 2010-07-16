package games.stendhal.server.core.events.achievements;

import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.dbcommand.ReadAchievementIdentifierToIdMap;
import games.stendhal.server.core.engine.dbcommand.WriteAchievementCommand;
import games.stendhal.server.core.engine.dbcommand.WriteReachedAchievementCommand;
import games.stendhal.server.entity.npc.condition.LevelGreaterThanCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasKilledNumberOfCreaturesCondition;
import games.stendhal.server.entity.player.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import marauroa.server.db.command.DBCommandQueue;
import marauroa.server.db.command.ResultHandle;

public class AchievementNotifier {
	
	private static AchievementNotifier instance;
	
	private Map<Category, List<Achievement>> achievements;
	
	private Map<String, Integer> identifiersToIds;
	
	private ResultHandle handle = new ResultHandle();
	
	private AchievementNotifier() {
		achievements = new HashMap<Category, List<Achievement>>();
		identifiersToIds = new HashMap<String, Integer>();
	}
	
	public static AchievementNotifier get() {
		if(instance == null) {
			instance = new AchievementNotifier();
		}
		return instance;
	}
	
	public void initialize() {
		Map<String, Achievement> allAchievements = createAchievements();
		for(Achievement a : allAchievements.values()) {
			if(!achievements.containsKey(a.getCategory())) {
				achievements.put(a.getCategory(), new LinkedList<Achievement>());
			}
			achievements.get(a.getCategory()).add(a);
		}
		Set<String> allIdentifiersInDatabase = collectAllIdentifiersFromDatabase();
		for(String identifier : allIdentifiersInDatabase) {
			allAchievements.remove(identifier);
		}
		for (Achievement a : allAchievements.values()) {
			WriteAchievementCommand command = new WriteAchievementCommand(a);
			DBCommandQueue.get().enqueueAndAwaitResult(command, handle);
			Integer id = command.getSavedId();
			identifiersToIds.put(a.getIdentifier(), id);
		}
	}

	private Set<String> collectAllIdentifiersFromDatabase() {
		DBCommandQueue.get().enqueueAndAwaitResult(new ReadAchievementIdentifierToIdMap(), handle);
		ReadAchievementIdentifierToIdMap command = waitForResult();
		Map<String, Integer> mapFromDB = command.getIdentifierToIdMap();
		identifiersToIds.putAll(mapFromDB);
		return mapFromDB.keySet();
	}

	private ReadAchievementIdentifierToIdMap waitForResult() {
		ReadAchievementIdentifierToIdMap command = DBCommandQueue.get().getOneResult(ReadAchievementIdentifierToIdMap.class, handle);
		while(command == null) {
			command = DBCommandQueue.get().getOneResult(ReadAchievementIdentifierToIdMap.class, handle);
		}
		return command;
	}

	/**
	 * checks all for level change relevant achievements for a player
	 * 
	 * @param player
	 */
	public void onXPGain(Player player) {
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

	private void getAndCheckAchievementsInCategory(Player player, Category category) {
		if(achievements.containsKey(category)) {
			List<Achievement> toCheck = achievements.get(category);
			checkAchievements(player, toCheck);
		}
	}

	private void checkAchievements(Player player,
			List<Achievement> toCheck) {
		for (Achievement achievement : toCheck) {
			if(achievement.isFulfilled(player) && !player.hasReachedAchievement(achievement.getIdentifier())) {
				logAndNotifyReachingOfAnAchievement(player, achievement);
			}
		}
	}
	
	private void logAndNotifyReachingOfAnAchievement(Player player,
			Achievement achievement) {
		player.sendPrivateText("Congratulations! You have reached the "+achievement.getTitle()+" achievement!");
		String identifier = achievement.getIdentifier();
		String title = achievement.getTitle();
		Category category = achievement.getCategory();
		String playerName = player.getName();
		DBCommandQueue.get().enqueue(new WriteReachedAchievementCommand(identifiersToIds.get(identifier), title, category, playerName));
		player.addReachedAchievement(achievement.getIdentifier());
		new GameEvent(playerName, "reach-achievement", category.toString(), title, identifier).raise();
	}

	private Map<String, Achievement> createAchievements() {
		Map<String, Achievement> achievementMap = new HashMap<String, Achievement>();
		for(Achievement a : createExperienceAchievements()) {
			achievementMap.put(a.getIdentifier(), a);
		}
		for(Achievement a : createFightingAchievements()) {
			achievementMap.put(a.getIdentifier(), a);
		}
		return achievementMap;
	}

	private Collection<Achievement> createFightingAchievements() {
		List<Achievement> fightingAchievements = new LinkedList<Achievement>();
		Achievement killRats = new Achievement("fight.general.rats", "Rat hunter", Category.FIGHTING, "kill 15 rats", new PlayerHasKilledNumberOfCreaturesCondition("rat", 15));
		fightingAchievements.add(killRats);
		return fightingAchievements;
	}

	private Collection<Achievement> createExperienceAchievements() {
		List<Achievement> xpAchievements = new LinkedList<Achievement>();
		Achievement newbie = new Achievement("xp.lvl.10", "newbie", Category.EXPERIENCE, "reach level 10", new LevelGreaterThanCondition(9));
		Achievement newbie25 = new Achievement("xp.lvl.25", "newbie25", Category.EXPERIENCE, "reach level 25", new LevelGreaterThanCondition(24));
		xpAchievements.add(newbie);
		xpAchievements.add(newbie25);
		return xpAchievements;
	}
	
}
