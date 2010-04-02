package games.stendhal.server.core.rp;

import games.stendhal.server.core.config.QuestsXMLLoader;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.AdosDeathmatch;
import games.stendhal.server.maps.quests.AmazonPrincess;
import games.stendhal.server.maps.quests.ArmorForDagobert;
import games.stendhal.server.maps.quests.Balloon;
import games.stendhal.server.maps.quests.BeerForHayunn;
import games.stendhal.server.maps.quests.Blackjack;
import games.stendhal.server.maps.quests.BowsForOuchit;
import games.stendhal.server.maps.quests.Campfire;
import games.stendhal.server.maps.quests.CleanStorageSpace;
import games.stendhal.server.maps.quests.CloakCollector;
import games.stendhal.server.maps.quests.CloakCollector2;
import games.stendhal.server.maps.quests.CloaksForBario;
import games.stendhal.server.maps.quests.ClubOfThorns;
import games.stendhal.server.maps.quests.CrownForTheWannaBeKing;
import games.stendhal.server.maps.quests.DailyItemQuest;
import games.stendhal.server.maps.quests.DailyMonsterQuest;
import games.stendhal.server.maps.quests.DiceGambling;
import games.stendhal.server.maps.quests.DragonLair;
import games.stendhal.server.maps.quests.ElfPrincess;
import games.stendhal.server.maps.quests.ElvishArmor;
import games.stendhal.server.maps.quests.FindGhosts;
import games.stendhal.server.maps.quests.FindRatChildren;
import games.stendhal.server.maps.quests.FishermansLicenseCollector;
import games.stendhal.server.maps.quests.FishermansLicenseQuiz;
import games.stendhal.server.maps.quests.HatForMonogenes;
import games.stendhal.server.maps.quests.HelpMrsYeti;
import games.stendhal.server.maps.quests.HelpTomi;
import games.stendhal.server.maps.quests.HerbsForCarmen;
import games.stendhal.server.maps.quests.HouseBuying;
import games.stendhal.server.maps.quests.HungryJoshua;
import games.stendhal.server.maps.quests.IQuest;
import games.stendhal.server.maps.quests.IcecreamForAnnie;
import games.stendhal.server.maps.quests.ImperialPrincess;
import games.stendhal.server.maps.quests.IntroducePlayers;
import games.stendhal.server.maps.quests.JailedBarbarian;
import games.stendhal.server.maps.quests.JailedDwarf;
import games.stendhal.server.maps.quests.KanmararnSoldiers;
import games.stendhal.server.maps.quests.KillDarkElves;
import games.stendhal.server.maps.quests.KillDhohrNuggetcutter;
import games.stendhal.server.maps.quests.KillGnomes;
import games.stendhal.server.maps.quests.KillSpiders;
import games.stendhal.server.maps.quests.LearnAboutKarma;
import games.stendhal.server.maps.quests.LearnAboutOrbs;
import games.stendhal.server.maps.quests.LookBookforCeryl;
import games.stendhal.server.maps.quests.LookUpQuote;
import games.stendhal.server.maps.quests.Marriage;
import games.stendhal.server.maps.quests.Maze;
import games.stendhal.server.maps.quests.McPeglegIOU;
import games.stendhal.server.maps.quests.MeetBunny;
import games.stendhal.server.maps.quests.MeetHackim;
import games.stendhal.server.maps.quests.MeetHayunn;
import games.stendhal.server.maps.quests.MeetIo;
import games.stendhal.server.maps.quests.MeetKetteh;
import games.stendhal.server.maps.quests.MeetMonogenes;
import games.stendhal.server.maps.quests.MeetSanta;
import games.stendhal.server.maps.quests.MeetZynn;
import games.stendhal.server.maps.quests.MithrilCloak;
import games.stendhal.server.maps.quests.NewsFromHackim;
import games.stendhal.server.maps.quests.ObsidianKnife;
import games.stendhal.server.maps.quests.PaperChase;
import games.stendhal.server.maps.quests.PizzaDelivery;
import games.stendhal.server.maps.quests.PlinksToy;
import games.stendhal.server.maps.quests.QuestInfo;
import games.stendhal.server.maps.quests.RainbowBeans;
import games.stendhal.server.maps.quests.ReverseArrow;
import games.stendhal.server.maps.quests.RingMaker;
import games.stendhal.server.maps.quests.SadScientist;
import games.stendhal.server.maps.quests.SemosMineTownRevivalWeeks;
import games.stendhal.server.maps.quests.SevenCherubs;
import games.stendhal.server.maps.quests.Snowballs;
import games.stendhal.server.maps.quests.SolveRiddles;
import games.stendhal.server.maps.quests.Soup;
import games.stendhal.server.maps.quests.StuffForBaldemar;
import games.stendhal.server.maps.quests.StuffForVulcanus;
import games.stendhal.server.maps.quests.SuntanCreamForZara;
import games.stendhal.server.maps.quests.TakeGoldforGrafindle;
import games.stendhal.server.maps.quests.ThePiedPiper;
import games.stendhal.server.maps.quests.ToysCollector;
// import games.stendhal.server.maps.quests.UltimateCollector;
import games.stendhal.server.maps.quests.VampireSword;
import games.stendhal.server.maps.quests.WeaponsCollector;
import games.stendhal.server.maps.quests.WeaponsCollector2;
import games.stendhal.server.maps.quests.WeeklyItemQuest;
import games.stendhal.server.maps.quests.WizardBank;
import games.stendhal.server.maps.quests.ZekielsPracticalTestQuest;
import games.stendhal.server.maps.quests.ZooFood;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

/**
 * Loads and manages all quests.
 */
public class StendhalQuestSystem {

	private static final StendhalQuestSystem stendhalQuestSystem = new StendhalQuestSystem();

	
	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(StendhalQuestSystem.class);

	private final List<IQuest> quests = new LinkedList<IQuest>();

	private QuestsXMLLoader questInfos;

	private StendhalQuestSystem() {
		// hide constructor, this is a Singleton
	}

	
	public static StendhalQuestSystem get() {
		return stendhalQuestSystem;
	}

	
	/**
	 * Initializes the QuestSystem.
	 */
	public void init() {
		
		questInfos = new QuestsXMLLoader();
		try {
			questInfos.load();
		} catch (SAXException e) {
			logger.error(e, e);
		}
		loadQuest(new AdosDeathmatch());
		loadQuest(new AmazonPrincess());
		loadQuest(new ArmorForDagobert());
		loadQuest(new Balloon());
		loadQuest(new BeerForHayunn());
		loadQuest(new Blackjack());
		loadQuest(new BowsForOuchit());
		loadQuest(new Campfire());
		//loadQuest(new CarmenCataclysm());
		loadQuest(new CleanStorageSpace());
		loadQuest(new CloakCollector());
		loadQuest(new CloakCollector2());
		loadQuest(new CloaksForBario());
		loadQuest(new ClubOfThorns());
		loadQuest(new CrownForTheWannaBeKing());
		loadQuest(new DailyItemQuest());
		loadQuest(new DailyMonsterQuest());
		loadQuest(new DiceGambling());
		// loadQuet(new DiogenesCataclysm());
		loadQuest(new DragonLair());
		loadQuest(new ElfPrincess());
		loadQuest(new ElvishArmor());
		loadQuest(new FindGhosts());
		loadQuest(new FindRatChildren());
		loadQuest(new FishermansLicenseQuiz());
		loadQuest(new FishermansLicenseCollector());
		loadQuest(new HatForMonogenes());
		// loadQuest(new HayunnCataclysm());
		loadQuest(new HelpTomi());
		loadQuest(new HelpMrsYeti());
		loadQuest(new HerbsForCarmen());
		loadQuest(new HouseBuying());
		loadQuest(new HungryJoshua());
		loadQuest(new IcecreamForAnnie());
		loadQuest(new ImperialPrincess());
		loadQuest(new IntroducePlayers());
		loadQuest(new JailedBarbarian());
		loadQuest(new JailedDwarf());
		loadQuest(new LearnAboutKarma());
		loadQuest(new LearnAboutOrbs());
		loadQuest(new LookBookforCeryl());
		loadQuest(new LookUpQuote());
		loadQuest(new KanmararnSoldiers());
		//loadQuest(new KillBlordroughs());
		loadQuest(new KillDarkElves());
		loadQuest(new KillDhohrNuggetcutter());
		loadQuest(new KillGnomes());
		loadQuest(new KillSpiders());
		loadQuest(new Marriage());
		// loadQuet(new MonogenesCataclysm());
		loadQuest(new MeetBunny());
		loadQuest(new Maze());
		loadQuest(new MeetHackim());
		loadQuest(new McPeglegIOU());
		loadQuest(new MeetHayunn());
		loadQuest(new MeetIo());
		loadQuest(new MeetKetteh());
		loadQuest(new MeetMonogenes());
		loadQuest(new MeetSanta());
		loadQuest(new MeetZynn());
		loadQuest(new MithrilCloak());
		loadQuest(new NewsFromHackim());
		// loadQuset(new NomyrCataclysm());
		loadQuest(new ObsidianKnife());
		loadQuest(new PizzaDelivery());
		loadQuest(new PlinksToy());
		loadQuest(new RainbowBeans());
		loadQuest(new ReverseArrow());
		loadQuest(new RingMaker());
		loadQuest(new SadScientist());
		// loadQuet(new SatoCataclysm());
		if (System.getProperty("stendhal.minetown") != null) {
			loadQuest(new SemosMineTownRevivalWeeks());
			loadQuest(new PaperChase());
		}
		loadQuest(new SolveRiddles());
		loadQuest(new SevenCherubs());
		loadQuest(new Snowballs());
		loadQuest(new Soup());
		loadQuest(new StuffForBaldemar());
		loadQuest(new StuffForVulcanus());
		loadQuest(new SuntanCreamForZara());
		loadQuest(new TakeGoldforGrafindle());
		loadQuest(new ThePiedPiper());
		loadQuest(new ToysCollector());
		// loadQuest(new UltimateCollector());
		loadQuest(new VampireSword());
		loadQuest(new WeaponsCollector());
		loadQuest(new WeaponsCollector2());
        loadQuest(new WeeklyItemQuest());
		loadQuest(new WizardBank());
		loadQuest(new ZekielsPracticalTestQuest());
		loadQuest(new ZooFood());

		TurnNotifier.get().notifyInTurns(10, new DumpSpeakerNPCtoDB());
	}

	private void loadQuest(final IQuest quest) {
		final String regex = System.getProperty("stendhal.quest.regex", ".*");
		if (!quest.getName().matches(regex)) {
			return;
		}
		
		try {
			initQuestAndAddToWorld(quest);
		} catch (Exception e) {
			logger.warn("Quest(" + quest.getName() + ") loading failed.", e);
		}
		
	}


	private void initQuestAndAddToWorld(final IQuest quest) {
	
		logger.info("Loading Quest: " + quest.getName());
		quest.addToWorld();

		quests.add(quest);
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
		sb.append("\r\nOpen Quests: ");
		boolean first = true;
		for (final IQuest quest : quests) {
			if (quest.isStarted(player) && !quest.isCompleted(player)) {
				if (!first) {
					sb.append(", ");
				}
				sb.append(quest.getName());
				first = false;
			}
		}

		// Completed Quests
		sb.append("\r\nCompleted Quests: ");
		first = true;
		for (final IQuest quest : quests) {
			if (quest.isCompleted(player)) {
				if (!first) {
					sb.append(", ");
				}
				sb.append(quest.getName());
				first = false;
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
