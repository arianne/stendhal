package games.stendhal.server.core.events.achievements;

import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.dbcommand.ReadAchievementIdentifierToIdMap;
import games.stendhal.server.core.engine.dbcommand.WriteAchievementCommand;
import games.stendhal.server.core.engine.dbcommand.WriteReachedAchievementCommand;
import games.stendhal.server.entity.npc.condition.LevelGreaterThanCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasKilledNumberOfCreaturesCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.player.ReadAchievementsOnLogin;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import marauroa.server.db.command.DBCommandQueue;
import marauroa.server.db.command.ResultHandle;
/**
 * Checks for reached achievements and marks them as reached for a player if he has fullfilled them
 *  
 * @author madmetzger
 */
public class AchievementNotifier {
	
	private static AchievementNotifier instance;
	
	private Map<Category, List<Achievement>> achievements;
	
	private Map<String, Integer> identifiersToIds;
	
	private ResultHandle handle = new ResultHandle();
	
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
		if(instance == null) {
			instance = new AchievementNotifier();
		}
		return instance;
	}
	
	/**
	 * initializes the achievements that are available and registers the login listener
	 * new added achievements are added to the achievements table
	 */
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
		SingletonRepository.getLoginNotifier().addListener(new ReadAchievementsOnLogin());
	}

	/**
	 * collects all identifiers from the database
	 * 
	 * @return a set of all identifier strings
	 */
	private Set<String> collectAllIdentifiersFromDatabase() {
		DBCommandQueue.get().enqueueAndAwaitResult(new ReadAchievementIdentifierToIdMap(), handle);
		ReadAchievementIdentifierToIdMap command = waitForResult();
		Map<String, Integer> mapFromDB = command.getIdentifierToIdMap();
		identifiersToIds.putAll(mapFromDB);
		return mapFromDB.keySet();
	}

	/**
	 * waits for the result of an issued ReadAchievementIdentifierToIdMap command
	 * 
	 * @return the completed command
	 */
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
			checkAchievements(player, toCheck);
		}
	}

	/**
	 * checks for each achievement if the player has reached it. in case of reaching
	 * an achievement it starts logging and notifying about reaching
	 * 
	 * @param player
	 * @param toCheck
	 */
	private void checkAchievements(Player player,
			List<Achievement> toCheck) {
		for (Achievement achievement : toCheck) {
			if(achievement.isFulfilled(player) && !player.hasReachedAchievement(achievement.getIdentifier())) {
				notifyPlayerAboutReachedAchievement(player, achievement);
				logReachingOfAnAchievement(player, achievement);
			}
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
	private void notifyPlayerAboutReachedAchievement(Player player,
			Achievement achievement) {
		//TODO: use postman here?
		//player.sendPrivateText("Congratulations! You have reached the "+achievement.getTitle()+" achievement!");
	}

	/**
	 * creates all available achievements
	 * 
	 * @return map with key identifier and value the identified achievement
	 */
	private Map<String, Achievement> createAchievements() {
		Map<String, Achievement> achievementMap = new HashMap<String, Achievement>();
		for(Achievement a : createExperienceAchievements()) {
			achievementMap.put(a.getIdentifier(), a);
		}
		for(Achievement a : createFightingAchievements()) {
			achievementMap.put(a.getIdentifier(), a);
		}
		for(Achievement a : createQuestAchievements()) {
			achievementMap.put(a.getIdentifier(), a);
		}
		return achievementMap;
	}

	/**
	 * creates a collection of all available fighting achievements
	 * 
	 * @return
	 */
	private Collection<Achievement> createFightingAchievements() {
		List<Achievement> fightingAchievements = new LinkedList<Achievement>();
		Achievement killRats = new Achievement("fight.general.rats", "Rat hunter", 
													Category.FIGHTING, "Kill 15 rats",
													new PlayerHasKilledNumberOfCreaturesCondition("rat", 15));
		Achievement exterminator = new Achievement("fight.general.exterminator", "Exterminator", 
													Category.FIGHTING, "Kill 10 rats of each kind", 
													new PlayerHasKilledNumberOfCreaturesCondition(10, "rat", "caverat", "venomrat", "zombie rat", "venom rat", "giantrat", "ratman", "ratwoman", "archrat"));
		fightingAchievements.add(killRats);
		fightingAchievements.add(exterminator);
		return fightingAchievements;
	}

	/**
	 * creates a collection of all experience related achievements
	 * 
	 * @return
	 */
	private Collection<Achievement> createExperienceAchievements() {
		List<Achievement> xpAchievements = new LinkedList<Achievement>();
		Achievement newbie = new Achievement("xp.lvl.10", "Greenhorn", 
												Category.EXPERIENCE, "Reach level 10", 
												new LevelGreaterThanCondition(9));
		Achievement newbie50 = new Achievement("xp.lvl.50", "Apprentice", 
												Category.EXPERIENCE, "Reach level 50", 
												new LevelGreaterThanCondition(49));
		Achievement newbie100 = new Achievement("xp.lvl.100", "Apprentice", 
												Category.EXPERIENCE, "Reach level 100", 
												new LevelGreaterThanCondition(99));
		xpAchievements.add(newbie);
		xpAchievements.add(newbie50);
		xpAchievements.add(newbie100);
		return xpAchievements;
	}
	
	/**
	 * creates a collection of all available quest achievements
	 * 
	 * @return
	 */
	private Collection<Achievement> createQuestAchievements() {
		List<Achievement> questAchievements = new LinkedList<Achievement>();
		return questAchievements;
	}

}
