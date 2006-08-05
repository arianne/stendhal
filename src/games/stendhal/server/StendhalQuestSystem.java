package games.stendhal.server;

import games.stendhal.server.entity.Player;
import games.stendhal.server.maps.quests.IQuest;
import games.stendhal.server.maps.quests.QuestInfo;
import games.stendhal.server.rule.defaultruleset.QuestXMLLoader;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.Log4J;

import org.apache.log4j.Logger;

public class StendhalQuestSystem {
	/** the logger instance. */
	private static final Logger logger = Log4J
			.getLogger(StendhalQuestSystem.class);

	private StendhalRPWorld world;
	private StendhalRPRuleProcessor rules;
	private List<IQuest> quests = new LinkedList<IQuest>();

	private QuestXMLLoader questInfos;
	private static StendhalQuestSystem stendhalQuestSystem;
	
	public static StendhalQuestSystem get() {
		return stendhalQuestSystem;
	}

	public StendhalQuestSystem(StendhalRPWorld world,
			StendhalRPRuleProcessor rules) {
		stendhalQuestSystem = this;
		this.world = world;
		this.rules = rules;
		questInfos = QuestXMLLoader.get();

		loadQuest("SheepGrowing");
		loadQuest("OrcishHappyMeal");
		loadQuest("LookBookforCeryl");
		loadQuest("IntroducePlayers");
		loadQuest("SevenCherubs");
		loadQuest("MeetHackim");
		loadQuest("MeetHayunn");
		loadQuest("MeetIo");
		loadQuest("MeetMonogenes");
		loadQuest("MeetNomyr");
		loadQuest("MeetZynn");
		loadQuest("BeerForHayunn");
		loadQuest("ArmorForDagobert");
		loadQuest("HatForMonogenes");
		loadQuest("NewsFromHackim");
		loadQuest("MeetKetteh");
		loadQuest("CleanStorageSpace");
		loadQuest("WeaponsCollector");
		loadQuest("ZooFood");
		loadQuest("CloaksForBario");
		loadQuest("Campfire");
	}

	public static void main(String[] args) {
		new StendhalQuestSystem(null, null);
	}

	private boolean loadQuest(String name) {
		try {
			Class entityClass = Class
					.forName("games.stendhal.server.maps.quests." + name);

			if (!IQuest.class.isAssignableFrom(entityClass)) { 
				logger.error("Class " + name + " doesn't implement IQuest interface.");
				return false;
			}

			// Create a new instance.
			logger.info("Loading Quest: " + name);
			Constructor constr = entityClass.getConstructor();
			IQuest quest = (IQuest) constr.newInstance();

			// init and add to world
			quest.init(name);
			quest.addToWorld(world, rules);

			quests.add(quest);
			return true;
		} catch (Exception e) {
			logger.warn("Quest(" + name + ") loading failed.", e);
			return false;
		}
	}
	
	private void dumpQuest(StringBuilder sb, IQuest quest, Player player) {
		QuestInfo questInfo = questInfos.get(quest.getName());
		sb.append("\t" + questInfo.getTitle() + "\r\n");
		List<String> history = quest.getHistory(player);
		for (String entry : history) {
			sb.append("\t\t" + entry + "\r\n");
		}
		sb.append("\r\n");
	}
	
	public String listQuests(Player player) {
		StringBuilder sb = new StringBuilder();
		
		// Open quests
		sb.append("\r\n\r\n");
		sb.append("Open Quests\r\n");
		sb.append("========\r\n");
		for (IQuest quest : quests) {
			if (quest.isStarted(player) && !quest.isCompleted(player)) {
				dumpQuest(sb, quest, player);
			}
		}

		// Completed Quests
		sb.append("\r\n\r\n");
		sb.append("Completed Quests\r\n");
		sb.append("============\r\n");
		for (IQuest quest : quests) {
			if (quest.isCompleted(player)) {
				dumpQuest(sb, quest, player);
			}
		}
		
		return sb.toString();
	}
}
