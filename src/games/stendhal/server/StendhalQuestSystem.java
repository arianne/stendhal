package games.stendhal.server;

import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.IQuest;
import games.stendhal.server.maps.quests.QuestInfo;
import games.stendhal.server.rule.defaultruleset.QuestXMLLoader;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.Log4J;

import org.apache.log4j.Logger;

/**
 * Loads and manages all quests 
 */
public class StendhalQuestSystem {
	/** the logger instance. */
	private static final Logger logger = Log4J
			.getLogger(StendhalQuestSystem.class);

	private List<IQuest> quests = new LinkedList<IQuest>();

	private QuestXMLLoader questInfos;
	private static StendhalQuestSystem stendhalQuestSystem;
	
	public static StendhalQuestSystem get() {
		if (stendhalQuestSystem == null) {
			stendhalQuestSystem = new StendhalQuestSystem();
		}
		return stendhalQuestSystem;
	}

	private StendhalQuestSystem() {
		// hide constructor, this is a Singleton
	}

	/**
	 * initiales the QuestSystem
	 */
	public void init() {
		questInfos = QuestXMLLoader.get();
		loadQuest("ArmorForDagobert");
		loadQuest("AthorFerryService");
		loadQuest("BeerForHayunn");
		loadQuest("Blackjack");
		loadQuest("Campfire");
		loadQuest("CleanStorageSpace");
		loadQuest("CloaksForBario");
		loadQuest("CostumeParty");
		loadQuest("DailyMonsterQuest");
		loadQuest("Deathmatch");
		loadQuest("DiceGambling");
		loadQuest("HatForMonogenes");
		loadQuest("IntroducePlayers");
		loadQuest("LookBookforCeryl");
		loadQuest("KanmararnSoldiers");
		loadQuest("MeetHackim");
		loadQuest("McPeglegIOU");
		loadQuest("MeetHayunn");
		loadQuest("MeetIo");
		loadQuest("MeetKetteh");
		loadQuest("MeetMonogenes");
		loadQuest("MeetNomyr");
		// loadQuest("MeetSanta");
		loadQuest("MeetZynn");
		loadQuest("NewsFromHackim");
		loadQuest("OrcishHappyMeal");
		loadQuest("PlinksToy");
		// loadQuest("ReverseArrow");
		// loadQuest("SemosMineTownRevivalWeeks");
		loadQuest("SevenCherubs");
		loadQuest("SheepGrowing");
		loadQuest("ToysCollector");
		loadQuest("VampireSword");
		loadQuest("WeaponsCollector");
		loadQuest("WeaponsCollector2");
		loadQuest("ZooFood");
	}

	private boolean loadQuest(String name) {
		try {
			Class questClass = Class
					.forName("games.stendhal.server.maps.quests." + name);

			if (!IQuest.class.isAssignableFrom(questClass)) { 
				logger.error("Class " + name + " doesn't implement IQuest interface.");
				return false;
			}

			// Create a new instance.
			logger.info("Loading Quest: " + name);
			Constructor constr = questClass.getConstructor();
			IQuest quest = (IQuest) constr.newInstance();

			// init and add to world
			quest.init(name);
			quest.addToWorld();

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
			String text = questInfo.getHistory().get(entry);
			if (text == null) {
				text = entry;
			}
			sb.append("\t\t * " + text + "\r\n");
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

	/**
	 * does stuff to update quest states on player login
	 *
	 * @param player Player
	 */
	public void onPlayerLogin(Player player) {
		for (IQuest quest : quests) {
			quest.onPlayerLogin(player);
		}
	}
}
