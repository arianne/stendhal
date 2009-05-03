package games.stendhal.server.core.rp;

import games.stendhal.server.core.config.QuestsXMLLoader;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.IQuest;
import games.stendhal.server.maps.quests.QuestInfo;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Loads and manages all quests.
 */
public class StendhalQuestSystem {

	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(StendhalQuestSystem.class);

	private final List<IQuest> quests = new LinkedList<IQuest>();

	private QuestsXMLLoader questInfos;

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
	 * Initializes the QuestSystem.
	 */
	public void init() {
		/*
		 * TODO: Refactor What about loading this from a XML file like zones?
		 */

		questInfos = SingletonRepository.getQuestsXMLLoader();
		loadQuest("AdosDeathmatch");
		loadQuest("AmazonPrincess");
		loadQuest("ArmorForDagobert");
		loadQuest("Balloon");
		loadQuest("BeerForHayunn");
		loadQuest("Blackjack");
		loadQuest("Campfire");
		// loadQuest("CarmenCataclysm");
		loadQuest("CleanStorageSpace");
		loadQuest("CloakCollector");
		loadQuest("CloakCollector2");
		loadQuest("CloaksForBario");
		loadQuest("ClubOfThorns");
		loadQuest("CrownForTheWannaBeKing");
		loadQuest("DailyItemQuest");
		loadQuest("DailyMonsterQuest");
		loadQuest("DiceGambling");
		// loadQuest("DiogenesCataclysm");
		loadQuest("DragonLair");
		loadQuest("ElfPrincess");
		loadQuest("ElvishArmor");
		loadQuest("FindGhosts");
		loadQuest("FishermansLicenseQuiz");
		loadQuest("FishermansLicenseCollector");
		loadQuest("HatForMonogenes");
		// loadQuest("HayunnCataclysm");
		loadQuest("HelpTomi");
		loadQuest("HouseBuying");
		loadQuest("HungryJoshua");
		loadQuest("IcecreamForAnnie");
		loadQuest("ImperialPrincess");
		loadQuest("IntroducePlayers");
		loadQuest("JailedBarbarian");
		loadQuest("JailedDwarf");
		loadQuest("LearnAboutKarma");
		loadQuest("LearnAboutOrbs");
		loadQuest("LookBookforCeryl");
		loadQuest("LookUpQuote");
		loadQuest("KanmararnSoldiers");
		loadQuest("KillDarkElves");
		loadQuest("KillDhohrNuggetcutter");
		loadQuest("KillSpiders");
		loadQuest("Marriage");
		// loadQuest("MonogenesCataclysm");
		// loadQuest("MeetBunny");
		loadQuest("MeetHackim");
		loadQuest("McPeglegIOU");
		loadQuest("MeetHayunn");
		loadQuest("MeetIo");
		loadQuest("MeetKetteh");
		loadQuest("MeetMonogenes");
	    loadQuest("MeetSanta");
		loadQuest("MeetZynn");
		loadQuest("MithrilCloak");
		loadQuest("NewsFromHackim");
		// loadQuest("NomyrCataclysm");
		loadQuest("ObsidianKnife");
		loadQuest("PizzaDelivery");
		loadQuest("PlinksToy");
		loadQuest("RainbowBeans");
		loadQuest("ReverseArrow");
		loadQuest("RingMaker");
		// loadQuest("SatoCataclysm");
		// loadQuest("SemosMineTownRevivalWeeks");
		loadQuest("SolveRiddles");
		loadQuest("SevenCherubs");
		loadQuest("Snowballs");
		loadQuest("Soup");
		loadQuest("StuffForBaldemar");
		loadQuest("StuffForVulcanus");
		loadQuest("SuntanCreamForZara");
		loadQuest("TakeGoldforGrafindle");
		loadQuest("ToysCollector");
		loadQuest("VampireSword");
		loadQuest("WeaponsCollector");
		loadQuest("WeaponsCollector2");
        loadQuest("WeeklyItemQuest");
		loadQuest("WizardBank");
		loadQuest("ZooFood");
	}

	private boolean loadQuest(final String name) {
		final String regex = System.getProperty("stendhal.quest.regex", ".*");
		if (!name.matches(regex)) {
			return false;
		}
		
		try {
			final Class< ? > questClass = Class.forName("games.stendhal.server.maps.quests."
					+ name);

			if (!IQuest.class.isAssignableFrom(questClass)) {
				logger.error("Class " + name
						+ " doesn't implement IQuest interface.");
				return false;
			}

			// Create a new instance.
			logger.info("Loading Quest: " + name);
			final Constructor< ? > constr = questClass.getConstructor();
			final IQuest quest = (IQuest) constr.newInstance();

			// init and add to world
			quest.init(name);
			quest.addToWorld();

			quests.add(quest);
			return true;
		} catch (final Exception e) {
			logger.warn("Quest(" + name + ") loading failed.", e);
			return false;
		}
	}

	private void dumpQuest(final StringBuilder sb, final IQuest quest, final Player player) {
		final QuestInfo questInfo = questInfos.get(quest.getName());
		sb.append("\t" + questInfo.getTitle() + "\r\n");
		final List<String> history = quest.getHistory(player);
		for (final String entry : history) {
			String text = questInfo.getHistory().get(entry);
			if (text == null) {
				text = entry;
			}
			sb.append("\t\t * " + text + "\r\n");
		}
		sb.append("\r\n");
	}

	public String listQuests(final Player player) {
		final StringBuilder sb = new StringBuilder();

		// Open quests
		sb.append("\r\n\r\n");
		sb.append("Open Quests\r\n");
		sb.append("========\r\n");
		for (final IQuest quest : quests) {
			if (quest.isStarted(player) && !quest.isCompleted(player)) {
				dumpQuest(sb, quest, player);
			}
		}

		// Completed Quests
		sb.append("\r\n\r\n");
		sb.append("Completed Quests\r\n");
		sb.append("============\r\n");
		for (final IQuest quest : quests) {
			if (quest.isCompleted(player)) {
				dumpQuest(sb, quest, player);
			}
		}

		return sb.toString();
	}

	public String listQuest(final Player player, final String questName) {
		final StringBuilder sb = new StringBuilder();
		for (final IQuest quest : quests) {
			if (quest.getName().equals(questName)) {
				dumpQuest(sb, quest, player);
			}
		}
		return sb.toString();
	}
}
