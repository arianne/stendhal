package games.stendhal.server.core.events.achievements;

import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.db.AchievementDAO;
import games.stendhal.server.core.engine.dbcommand.WriteReachedAchievementCommand;
import games.stendhal.server.entity.npc.condition.LevelGreaterThanCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasKilledNumberOfCreaturesCondition;
import games.stendhal.server.entity.npc.condition.QuestStateGreaterThanCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.player.ReadAchievementsOnLogin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import marauroa.server.db.command.DBCommandQueue;
import marauroa.server.db.command.ResultHandle;
import marauroa.server.game.db.DAORegister;

import org.apache.log4j.Logger;
/**
 * Checks for reached achievements and marks them as reached for a player if he has fullfilled them
 *  
 * @author madmetzger
 */
public class AchievementNotifier {
	
	private static final Logger logger = Logger.getLogger(AchievementNotifier.class);
	
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
			Integer id;
			try {
				id = DAORegister.get().get(AchievementDAO.class).saveAchievement(a.getIdentifier(), a.getTitle(), a.getCategory(), a.getBaseScore());
				identifiersToIds.put(a.getIdentifier(), id);
			} catch (SQLException e) {
				logger.error("Error while saving new achievement "+a.getTitle(), e);
			}
		}
		SingletonRepository.getLoginNotifier().addListener(new ReadAchievementsOnLogin());
	}

	/**
	 * collects all identifiers from the database
	 * 
	 * @return a set of all identifier strings
	 */
	private Set<String> collectAllIdentifiersFromDatabase() {
		Map<String, Integer> mapFromDB = new HashMap<String, Integer>();
		try {
			mapFromDB = DAORegister.get().get(AchievementDAO.class).loadIdentifierIdPairs();
		} catch (SQLException e) {
			logger.error("Error while loading Identifier to id map for achievements.", e);
		}
		identifiersToIds.putAll(mapFromDB);
		return mapFromDB.keySet();
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
	 * Checks on login of a player which achievements the player has reached and gives a summarizing message
	 * 
	 * @param player
	 */
	public void onLogin(Player player) {
		List<Achievement> toCheck = new ArrayList<Achievement>();
		Collection<List<Achievement>> values = achievements.values();
		for (List<Achievement> list : values) {
			toCheck.addAll(list);
		}
		List<Achievement> reached = checkAchievements(player, toCheck);
		StringBuilder sb = new StringBuilder();
		sb.append("You have reached ");
		sb.append(Integer.valueOf(reached.size()));
		sb.append(" new achievements. Please check #http://stendhalgame.org for details.");
		player.sendPrivateText(sb.toString());
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
	 * checks for each achievement if the player has reached it. in case of reaching
	 * an achievement it starts logging and notifying about reaching
	 * 
	 * @param player
	 * @param toCheck
	 */
	private List<Achievement> checkAchievements(Player player,
			List<Achievement> toCheck) {
		List<Achievement> reached = new ArrayList<Achievement>();
		for (Achievement achievement : toCheck) {
			if(achievement.isFulfilled(player) && !player.hasReachedAchievement(achievement.getIdentifier())) {
				logReachingOfAnAchievement(player, achievement);
				reached.add(achievement);
			}
		}
		return reached;
	}
	
	/**
	 * Notifies a player about reached achievements via private message
	 * 
	 * @param player
	 * @param achievements
	 */
	private void notifyPlayerAboutReachedAchievements(Player player, List<Achievement> achievements) {
		for (Achievement achievement : achievements) {
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
	private void notifyPlayerAboutReachedAchievement(Player player,
			Achievement achievement) {
		player.sendPrivateText("Congratulations! You have reached the "+achievement.getTitle()+" achievement!");
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
		Achievement killRats = new Achievement("fight.general.rats", "Rat Hunter", 
													Category.FIGHTING, "Kill 15 rats", Achievement.EASY_BASE_SCORE,
													new PlayerHasKilledNumberOfCreaturesCondition("rat", 15));
		Achievement exterminator = new Achievement("fight.general.exterminator", "Exterminator", 
													Category.FIGHTING, "Kill 10 rats of each kind", Achievement.EASY_BASE_SCORE,
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
												Category.EXPERIENCE, "Reach level 10", Achievement.EASY_BASE_SCORE,
												new LevelGreaterThanCondition(9));
		Achievement newbie50 = new Achievement("xp.lvl.50", "Novice", 
												Category.EXPERIENCE, "Reach level 50", Achievement.EASY_BASE_SCORE,
												new LevelGreaterThanCondition(49));
		Achievement newbie100 = new Achievement("xp.lvl.100", "Apprentice", 
												Category.EXPERIENCE, "Reach level 100", Achievement.MEDIUM_BASE_SCORE, 
												new LevelGreaterThanCondition(99));
		Achievement newbie597 = new Achievement("xp.lvl.597", "Stendhal Master", 
				Category.EXPERIENCE, "Reach level 597", Achievement.HARD_BASE_SCORE, 
				new LevelGreaterThanCondition(596));
		xpAchievements.add(newbie);
		xpAchievements.add(newbie50);
		xpAchievements.add(newbie100);
		xpAchievements.add(newbie597);
		return xpAchievements;
	}
	
	/**
	 * creates a collection of all available quest achievements
	 * 
	 * @return
	 */
	private Collection<Achievement> createQuestAchievements() {
		List<Achievement> questAchievements = new LinkedList<Achievement>();
		//daily monster quest achievements
		questAchievements.add(new Achievement("quest.special.dmq.10", "Semos' Protector",
												Category.QUEST,  "Finish daily monster quest 10 times", Achievement.EASY_BASE_SCORE,
												new QuestStateGreaterThanCondition("daily", 2, 9)));
		questAchievements.add(new Achievement("quest.special.dmq.50", "Semos' Guardian",
												Category.QUEST,  "Finish daily monster quest 50 times", Achievement.EASY_BASE_SCORE,
												new QuestStateGreaterThanCondition("daily", 2, 49)));
		questAchievements.add(new Achievement("quest.special.dmq.100", "Semos' Hero",
												Category.QUEST,  "Finish daily monster quest 100 times", Achievement.MEDIUM_BASE_SCORE,
												new QuestStateGreaterThanCondition("daily", 2, 99)));
		questAchievements.add(new Achievement("quest.special.dmq.250", "Semos' Champion",
												Category.QUEST,  "Finish daily monster quest 250 times", Achievement.MEDIUM_BASE_SCORE,
												new QuestStateGreaterThanCondition("daily", 2, 249)));
		questAchievements.add(new Achievement("quest.special.dmq.500", "Semos' Vanquisher",
												Category.QUEST,  "Finish daily monster quest 500 times", Achievement.HARD_BASE_SCORE,
												new QuestStateGreaterThanCondition("daily", 2, 499)));
		//daily item quest achievements
		questAchievements.add(new Achievement("quest.special.diq.10", "Ados' Supporter",
				Category.QUEST,  "Finish daily item quest 10 times", Achievement.EASY_BASE_SCORE,
				new QuestStateGreaterThanCondition("daily_item", 2, 9)));
		questAchievements.add(new Achievement("quest.special.diq.50", "Ados' Provider",
				Category.QUEST,  "Finish daily item quest 50 times", Achievement.EASY_BASE_SCORE,
				new QuestStateGreaterThanCondition("daily_item", 2, 49)));
		questAchievements.add(new Achievement("quest.special.diq.100", "Ados' Supplier",
				Category.QUEST,  "Finish daily item quest 100 times", Achievement.MEDIUM_BASE_SCORE,
				new QuestStateGreaterThanCondition("daily_item", 2, 99)));
		questAchievements.add(new Achievement("quest.special.diq.250", "Ados' Stockpiler",
				Category.QUEST,  "Finish daily item quest 250 times", Achievement.MEDIUM_BASE_SCORE,
				new QuestStateGreaterThanCondition("daily_item", 2, 249)));
		questAchievements.add(new Achievement("quest.special.diq.50", "Ados' Hoarder",
				Category.QUEST,  "Finish daily item quest 50 times", Achievement.HARD_BASE_SCORE,
				new QuestStateGreaterThanCondition("daily_item", 2, 499)));
		//weekly item quest achievement
		questAchievements.add(new Achievement("quest.special.wiq.5", "Archaeologist",
				Category.QUEST,  "Finish weekly item quest 5 times", Achievement.HARD_BASE_SCORE,
				new QuestStateGreaterThanCondition("weekly_item", 2, 4)));
		return questAchievements;
	}

}
