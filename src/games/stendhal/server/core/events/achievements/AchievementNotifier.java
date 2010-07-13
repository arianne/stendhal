package games.stendhal.server.core.events.achievements;

import games.stendhal.server.core.engine.dbcommand.ReadAchievementIdentifierToIdMap;
import games.stendhal.server.core.engine.dbcommand.WriteAchievementCommand;
import games.stendhal.server.core.engine.dbcommand.WriteReachedAchievementCommand;
import games.stendhal.server.entity.npc.condition.LevelGreaterThanCondition;
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
	
	private static Map<Category, List<Achievement>> achievements;
	
	private static Map<String, Integer> identifiersToIds;
	
	private static ResultHandle handle = new ResultHandle();
	
	public static void initialize() {
		achievements = new HashMap<Category, List<Achievement>>();
		identifiersToIds = new HashMap<String, Integer>();
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

	private static Set<String> collectAllIdentifiersFromDatabase() {
		DBCommandQueue.get().enqueueAndAwaitResult(new ReadAchievementIdentifierToIdMap(), handle);
		ReadAchievementIdentifierToIdMap command = waitForResult();
		Map<String, Integer> mapFromDB = command.getIdentifierToIdMap();
		identifiersToIds.putAll(mapFromDB);
		return mapFromDB.keySet();
	}

	private static ReadAchievementIdentifierToIdMap waitForResult() {
		ReadAchievementIdentifierToIdMap command = DBCommandQueue.get().getOneResult(ReadAchievementIdentifierToIdMap.class, handle);
		while(command == null) {
			command = DBCommandQueue.get().getOneResult(ReadAchievementIdentifierToIdMap.class, handle);
		}
		return command;
	}

	public static void onXPGain(Player player) {
		List<Achievement> toCheck = achievements.get(Category.EXPERIENCE);
		checkAchievements(player, toCheck);
	}

	private static void checkAchievements(Player player,
			List<Achievement> toCheck) {
		for (Achievement achievement : toCheck) {
			if(achievement.isFulfilled(player)) {
				//TODO remove comment when decided how fulfilled achievements are stored in player object
				//logAndNotifyReachingOfAnAchievement(player, achievement);
			}
		}
	}
	
	private static void logAndNotifyReachingOfAnAchievement(Player player,
			Achievement achievement) {
		player.sendPrivateText("Congratulations! You have reached the "+achievement.getTitle()+" achievement!");
		String identifier = achievement.getIdentifier();
		String title = achievement.getTitle();
		Category category = achievement.getCategory();
		String playerName = player.getName();
		DBCommandQueue.get().enqueue(new WriteReachedAchievementCommand(identifiersToIds.get(identifier), title, category, playerName));
	}

	private static Map<String, Achievement> createAchievements() {
		Map<String, Achievement> achievementMap = new HashMap<String, Achievement>();
		for(Achievement a : createExperienceAchievements()) {
			achievementMap.put(a.getIdentifier(), a);
		}
		return achievementMap;
	}

	private static Collection<? extends Achievement> createExperienceAchievements() {
		List<Achievement> xpAchievements = new LinkedList<Achievement>();
		Achievement newbie = new Achievement("xp.lvl.10", "newbie", Category.EXPERIENCE, "newbie description", new LevelGreaterThanCondition(9));
		Achievement newbie25 = new Achievement("xp.lvl.25", "newbie25", Category.EXPERIENCE, "newbie25 description", new LevelGreaterThanCondition(24));
		xpAchievements.add(newbie);
		xpAchievements.add(newbie25);
		return xpAchievements;
	}

}
