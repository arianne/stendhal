/***************************************************************************
 *                   (C) Copyright 2003-2024 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.rp;


import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.constants.Occasion;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.npc.quest.BuiltQuest;
import games.stendhal.server.entity.npc.quest.QuestManuscript;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.*;
import games.stendhal.server.maps.quests.antivenom_ring.AntivenomRing;

/**
 * Loads and manages all quests.
 */
public class StendhalQuestSystem {

	/** The logger instance. */
	private static final Logger logger = Logger.getLogger(StendhalQuestSystem.class);

	/** The singleton instance. */
	private static StendhalQuestSystem instance;

	private final static List<IQuest> quests = new LinkedList<IQuest>();

	private final static List<IQuest> cached = new ArrayList<>();
	private static boolean cacheLoaded = false;


	/**
	 * gets the singleton instance of the StendhalQuestSystem
	 *
	 * @return StendhalQuestSystem
	 */
	public static StendhalQuestSystem get() {
		if (instance == null) {
			instance = new StendhalQuestSystem();
		}

		return instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private StendhalQuestSystem() {
		// singleton
	}

	/**
	 *
	 * @param player
	 */
	public static void updatePlayerQuests(Player player) {
		for(int i=0; i<quests.size(); i++) {
			quests.get(i).updatePlayer(player);
		}
	}

	/**
	 * Initializes the QuestSystem.
	 */
	public void init() {
		loadQuest(new AGrandfathersWish());
		//deactivated AdMemoriaInPortfolio
		//loadQuest(new AdMemoriaInPortfolio());
		loadQuest(new AdosDeathmatch());
		loadQuest(new AdventureIsland());
		loadQuest(new AmazonPrincess());
		loadQuest(new AntivenomRing());
		loadQuest(new ArmorForDagobert());
		loadQuest(new BalloonForBobby());
		loadQuest(new BeerForHayunn());
		loadQuest(new Blackjack());
		loadQuest(new BowsForOuchit());
		loadQuest(new Campfire());
		// deactivated capture the flag
		//loadQuest(new CaptureFlagQuest());
		loadQuest(new ChocolateForElisabeth());
		loadQuest(new CleanAthorsUnderground());
		loadQuest(new CleanStorageSpace());
		loadQuest(new CloakCollector());
		loadQuest(new CloakCollector2());
		loadQuest(new CloaksForBario());
		loadQuest(new ClubOfThorns());
		loadQuest(new CoalForHaunchy());
		loadQuest(new CodedMessageFromFinnFarmer());
		loadQuest(new CollectEnemyData());
		loadQuest(new CrownForTheWannaBeKing());
		loadQuest(new DailyItemQuest());
		loadQuest(new DailyMonsterQuest());
		loadQuest(new DiceGambling());
		loadQuest(new DragonLair());
		loadQuest(new EasterGiftsForChildren());
		loadQuest(new EggsForMarianne());
		loadQuest(new ElfPrincess());
		loadQuest(new ElvishArmor());
		loadQuest(new EmotionCrystals());
		loadQuest(new FindGhosts());
		loadQuest(new FindJefsMom());
		loadQuest(new FindRatChildren());
		loadQuest(new FishermansLicenseQuiz());
		loadQuest(new FishermansLicenseCollector());
		loadQuest(new FishSoup());
		loadQuest(new FishSoupForHughie());
		loadQuest(new FruitsForCoralia());
		loadQuest(new GoodiesForRudolph());
		loadQuest(new GuessKills());
		loadQuest(new HatForMonogenes());
		loadQuest(new HelpTomi());
		loadQuest(new HelpMrsYeti());
		loadQuest(new HelpWithTheHarvest());
		loadQuest(new HerbsForCarmen());
		loadQuest(new HouseBuying());
		loadQuest(new HungryJoshua());
		loadQuest(new IcecreamForAnnie());
		loadQuest(new ImperialPrincess());
		loadQuest(new JailedBarbarian());
		loadQuest(new JailedDwarf());
		loadQuest(new LearnAboutKarma());
		loadQuest(new LearnAboutOrbs());
		loadQuest(new LookBookforCeryl());
		loadQuest(new LookUpQuote());
		loadQuest(new LuckyFourLeafClover());
		loadQuest(new KanmararnSoldiers());
		loadQuest(new KillBlordroughs());
		loadQuest(new KillDarkElves());
		loadQuest(new KillDhohrNuggetcutter());
		loadQuest(new KillEnemyArmy());
		loadQuest(new KillGnomes());
		loadQuest(new KillMonks());
		loadQuest(new KillSpiders());
		loadQuest(new KoboldishTorcibud());
		loadQuest(new Marriage());
		loadQuest(new Maze());
		loadQuest(new McPeglegIOU());
		loadQuest(new MealForGroongo());
		loadQuest(new MeetBunny());
		loadQuest(new MedicineForTad());
		loadQuest(new MeetHackim());
		loadQuest(new MeetHayunn());
		loadQuest(new MeetIo());
		loadQuest(new MeetKetteh());
		loadQuest(new MeetMarieHenri());
		loadQuest(new MeetMonogenes());
		loadQuest(new MeetSanta());
		loadQuest(new MeetZynn());
		loadQuest(new MithrilCloak());
		loadQuest(new MixtureForOrtiv());
		loadQuest(new MuseumEntranceFee());
		loadQuest(new NewsFromHackim());
		loadQuest(new ObsidianKnife());
		loadQuest(new PizzaDelivery());
		loadQuest(new PlinksToy());
		loadQuest(new RainbowBeans());
		loadQuest(new RestockFlowerShop());
		loadQuest(new ReverseArrow());
		loadQuest(new RingMaker());
		loadQuest(new SadScientist());
		loadQuest(new ScubaLicenseQuiz());
		loadQuest(new SheepGrowing());
		loadQuest(new SolveRiddles());
		loadQuest(new SevenCherubs());
		loadQuest(new Snowballs());
		loadQuest(new Soup());
		loadQuest(new StuffForBaldemar());
		loadQuest(new StuffForVulcanus());
		loadQuest(new SuntanCreamForZara());
		loadQuest(new SuppliesForPhalk());
		loadQuest(new TakeGoldforGrafindle());
		loadQuest(new ThePiedPiper());
		loadQuest(new ToysCollector());
		loadQuest(new TrapsForKlaas());
		//loadQuest(new TutorialIsland());
		loadQuest(new UltimateCollector());
		loadQuest(new UnicornHornsForZelan());
		loadQuest(new VampireSword());
		loadQuest(new WaterForXhiphin());
		loadQuest(new WeaponsCollector());
		loadQuest(new WeaponsCollector2());
		loadQuest(new WeeklyItemQuest());
		loadQuest(new WizardBank());
		loadQuest(new ZekielsPracticalTestQuest());
		loadQuest(new ZooFood());

		if (Occasion.MINETOWN) {
			loadQuest(new PaperChase()); // needs to be loaded before SemosMineTownRevivalWeeks
			loadQuest(new MineTownRevivalWeeks());
		}
		if (Occasion.MINETOWN_CONSTRUCTION) {
			loadQuest(new MineTownRevivalWeeksConstruction());
		}

		TurnNotifier.get().notifyInTurns(10, new DumpGameInformationForWebsite());
	}

	/**
	 * loads the quests and adds it to the world
	 *
	 * @param quest a Quest
	 */
	public void loadQuest(final IQuest quest) {

		// for quicker startup, check the stendhal.quest.regex parameter
		final String regex = System.getProperty("stendhal.quest.regex", ".*");
		if (!quest.getName().matches(regex)) {
			return;
		}

		// load the quest and add it to the world
		try {
			initQuestAndAddToWorld(quest);
		} catch (Exception e) {
			logger.error("Quest(" + quest.getName() + ") loading failed.", e);
		}
	}

	/**
	 * loads the quests and adds it to the world
	 *
	 * @param quest a Quest
	 */
	public void loadQuest(final QuestManuscript quest) {
		loadQuest(new BuiltQuest(quest.story()));
	}

	/**
	 * adds a quest to the world
	 *
	 * @param quest Quest to add
	 */
	private void initQuestAndAddToWorld(final IQuest quest) {
		if (isLoaded(quest)) {
			logger.warn("Not loading previously loaded quest: " + quest.getName());
			return;
		}

		logger.info("Loading Quest: " + quest.getName());
		quest.addToWorld();
		quests.add(quest);
	}

	/**
	 * Reloads quests & resets entities. Quest must support
	 * `IQuest.removeFromWorld`.
	 *
	 * @param slots
	 *     Slot identifiers of quests to reload.
	 * @return
	 *     <code>true</code> if quests reloaded successfully.
	 */
	public void reloadQuestSlots(final String... slots) {
		for (final String slot: slots) {
			if (!isLoadedSlot(slot)) {
				continue;
			}
			final IQuest q = getQuestFromSlot(slot);
			// unload then reload
			if (unloadQuest(q)) {
				initQuestAndAddToWorld(q);
			}
		}
	}

	/**
	 * Caches a quest for loading later.
	 *
	 * @param quest
	 * 		Quest to be cached.
	 */
	public void cacheQuest(final IQuest quest) {
		// don't cache quests if server has already been initialized
		if (cacheLoaded) {
			loadQuest(quest);
			return;
		}

		if (quest == null) {
			logger.error("Attempted to cache null quest");
			return;
		}

		if (cached.contains(quest)) {
			logger.warn("Quest previously cached: " + quest.getName());
			return;
		}

		cached.add(quest);
	}

	/**
	 * Loads all quests stored in the cache.
	 */
	public void loadCachedQuests() {
		for (final IQuest quest: cached) {
			loadQuest(quest);
		}

		// empty cache
		cached.clear();
		cacheLoaded = true;
	}

	/**
	 *
	 * @param sb - string builder of mother function
	 * @param quest - show this quest to payer
	 * @param player - player which quest history need to be shown to himself
	 */
	private void dumpQuest(final StringBuilder sb, final IQuest quest, final Player player) {
		final QuestInfo questInfo = quest.getQuestInfo(player);
		sb.append(questInfo.getName() + " : ");
		sb.append(questInfo.getDescription() + "\r\n");
		if (questInfo.getSuggestedMinLevel() > player.getLevel()) {
			sb.append("(This task may be too dangerous for your level of experience)\r\n");
		}

		final List<String> history = quest.getHistory(player);
		for (final String entry : history) {
			sb.append("\t * " + entry + "\r\n");
		}

		final List<String> hints = quest.getHint(player);
		for (final String entry : hints) {
			sb.append("\t - " + entry + "\r\n");
		}
	}

	/**
	 * lists all quest the player knows about, including open and completed quests.
	 *
	 * @param player Player to create the report for
	 * @return quest report
	 */
	public String listQuests(final Player player) {
		final StringBuilder sb = new StringBuilder();

		// Open quests
		sb.append("\r\n#'Open Quests': ");
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
		sb.append("\r\n#'Completed Quests': ");
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

	/**
	 * creates a report on a specified quest for a specified player
	 *
	 * @param player Player
	 * @param questName name of quest
	 * @return quest report
	 */
	public String listQuest(final Player player, final String questName) {
		final StringBuilder sb = new StringBuilder();
		for (final IQuest quest : quests) {
			if (quest.getName().equals(questName)) {
				dumpQuest(sb, quest, player);
			}
		}
		return sb.toString();
	}

	/**
	 * dumps the internal quest states for the specified player. This is used for the InspectAction.
	 *
	 * @param player Player to create the report for
	 * @return report
	 */
	public String listQuestsStates(final Player player) {
		final StringBuilder sb = new StringBuilder();

		// Open quests
		sb.append("\r\n#'Open Quests': ");

		for (final IQuest quest : quests) {
			if (quest.isStarted(player) && !quest.isCompleted(player)) {
				sb.append("\r\n" + quest.getName() + " (" + quest.getSlotName() + "): " + player.getQuest(quest.getSlotName()));
			}
		}

		// Completed Quests
		sb.append("\n#'Completed Quests': ");
		for (final IQuest quest : quests) {
			if (quest.isCompleted(player)) {
				sb.append("\r\n" + quest.getName() + " (" + quest.getSlotName() + "): " + player.getQuest(quest.getSlotName()));
			}
		}

		return sb.toString();
	}

	/**
	 * gets a list of open quests
	 *
	 * @param player Player to return the list for
	 * @return list of open quests
	 */
	public List<String> getOpenQuests(Player player) {
		List<String> res = new LinkedList<String>();
		for (final IQuest quest : quests) {
			if (quest.isStarted(player) && !quest.isCompleted(player) && quest.isVisibleOnQuestStatus(player)) {
				res.add(quest.getQuestInfo(player).getName());
			}
		}
		return res;
	}

	/**
	 * Gets a list of completed quests.
	 *
	 * @param player Player to return the list for
	 * @param repeatIsCompleted
	 *   Open quests being repeated count as completed.
	 * @return list of completed quests
	 */
	public List<String> getCompletedQuests(Player player, final boolean repeatIsCompleted) {
		Collection<IQuest> tmp = findCompletedQuests(player, repeatIsCompleted);
		List<String> res = new ArrayList<String>(tmp.size());
		for (IQuest quest : tmp) {
			if (quest.isVisibleOnQuestStatus(player)) {
				res.add(quest.getQuestInfo(player).getName());
			}
		}
		return res;
	}

	/**
	 * Gets a list of completed quests.
	 *
	 * @param player Player to return the list for
	 * @return list of completed quests
	 */
	public List<String> getCompletedQuests(Player player) {
		return getCompletedQuests(player, false);
	}

	/**
	 * Get the list of quests a player has completed, and can now do again.
	 *
	 * @param player
	 * @return list of quest names
	 */
	public List<String> getRepeatableQuests(Player player) {
		Collection<IQuest> tmp = findCompletedQuests(player);
		List<String> res = new ArrayList<String>();
		for (IQuest quest : tmp) {
			if (quest.isVisibleOnQuestStatus(player) && quest.isRepeatable(player)) {
				res.add(quest.getQuestInfo(player).getName());
			}
		}
		return res;
	}

	/**
	 * Find the quests that a player has completed.
	 *
	 * @param player
	 *   Player in question.
	 * @param repeatIsCompleted
	 *   Open quests being repeated count as completed.
	 * @return
	 *   Completed quests.
	 */
	private Collection<IQuest> findCompletedQuests(Player player, final boolean repeatIsCompleted) {
		List<IQuest> res = new ArrayList<IQuest>();
		for (IQuest quest : quests) {
			final boolean completed = repeatIsCompleted ? quest.getCompletedCount(player) > 0 : quest.isCompleted(player);
			if (completed && quest.isVisibleOnQuestStatus(player)) {
				res.add(quest);
			}
		}
		return res;
	}

	/**
	 * Find the quests that a player has completed.
	 *
	 * @param player
	 *   Player in question.
	 * @return
	 *   Completed quests.
	 */
	private Collection<IQuest> findCompletedQuests(final Player player) {
		return findCompletedQuests(player, false);
	}

	/**
	 * Retrieves points total for completed quests to apply to Hall of Fame.
	 *
	 * @param player
	 *   Player to whom points are being attributed.
	 * @return
	 *   Total score for completed quests.
	 */
	public int getTotalHOFScore(final Player player) {
		int score = 0;
		for (final IQuest quest: findCompletedQuests(player, true)) {
			score += quest.getHOFScore(player).value;
		}
		return score;
	}

	/**
	 * gets the description of a quest
	 *
	 * @param player player to get the details for
	 * @param questName name of quest
	 * @return description
	 */
	public String getQuestDescription(final Player player, final String questName) {
		for (final IQuest quest : quests) {
			final QuestInfo questInfo = quest.getQuestInfo(player);
			if (questInfo.getName().equals(questName)) {
				return questInfo.getDescription();
			}
		}
		return "";
	}

	/**
	 * If the quest is too dangerous, add a warning unless the player has
	 * already completed it.
	 *
	 * @param player Player to get the warning for
	 * @param questName   quest
	 * @return warning or empty string
	 */
	public String getQuestLevelWarning(Player player, String questName) {
		for (final IQuest quest : quests) {
			final QuestInfo questInfo = quest.getQuestInfo(player);
			if (questInfo.getName().equals(questName)
					&& (questInfo.getSuggestedMinLevel() > player.getLevel())
					&& !quest.isCompleted(player)) {
				return "This task may be too dangerous for your level of experience.";
			}
		}
		return "";
	}
	/**
	 * gets details on the progress of the quest
	 *
	 * @param player player to get the details for
	 * @param questName name of quest
	 * @return details
	 */
	public List<String> getQuestProgressDetails(final Player player, final String questName) {
		List<String> res = new LinkedList<String>();
		for (final IQuest quest : quests) {
			final QuestInfo questInfo = quest.getQuestInfo(player);
			if (questInfo.getName().equals(questName)) {
				final List<String> history = quest.getFormattedHistory(player);
				for (final String entry : history) {
					res.add(entry);
				}
			}
		}
		return res;
	}


	/**
	 * gets the IQuest object for a named quest.
	 *
	 * @param questName name of quest
	 * @return IQuest or <code>null</code> if it does not exist.
	 */
	public IQuest getQuest(String questName) {
		for (final IQuest quest : quests) {
			if (quest.getName().equals(questName)) {
				return quest;
			}
		}

		return null;
	}

	/**
	 * gets the IQuest object for a quest.
	 *
	 * @param questSlot
	 * 		Slot name used for quest.
	 * @return
	 * 		IQuest or <code>null</code> if it does not exist.
	 */
	public IQuest getQuestFromSlot(final String questSlot) {
		for (final IQuest quest : quests) {
			if (quest.getSlotName().equals(questSlot)) {
				return quest;
			}
		}

		return null;
	}

	/**
	 * Unloads a quest and removes the things related to it from world.
	 * <p>Note: The quest in question has to support this</p>
	 *
	 * @param quest
	 *     Quest instance.
	 * @return
	 *     <code>true</code> if quest was unloaded.
	 */
	public boolean unloadQuest(final IQuest quest) {
		logger.info("Unloading Quest: " + quest.getName());
		// remove from loaded list before calling removeFromWorld to prevent redundancies
		quests.remove(quest);
		if (quest.removeFromWorld()) {
			return true;
		} else {
			logger.error(quest.getClass().getName() + " cannot be removed from the world");
		}
		// removal failed, re-add to loaded list
		quests.add(quest);
		return false;
	}

	/**
	 * Unloads a quest and removes the things related to it from world.
	 * <p>Note: The quest in question has to support this</p>
	 *
	 * @param questName
	 *     Name of quest to unload.
	 * @return
	 *     <code>true</code> if quest was unloaded.
	 */
	public boolean unloadQuest(String questName) {
		final IQuest quest = getQuest(questName);
		if (quest == null) {
			logger.error("Quest " + questName + " is not loaded", new Throwable());
			return true;
		}
		return unloadQuest(quest);
	}

	/**
	 * Retrieves all loaded instances with slot.
	 */
	private List<IQuest> _getAllBySlot(final String slot) {
		final List<IQuest> qfound = new ArrayList<>();
		for (final IQuest q: quests) {
			if (q.getSlotName().equals(slot)) {
				qfound.add(q);
			}
		}
		return qfound;
	}

	/**
	 * Unloads a quest and removes the things related to it from world.
	 * <p>Note: The quest in question has to support this</p>
	 *
	 * @param slot
	 *     Quest slot identifier.
	 * @return
	 *     <code>true</code> if quest was unloaded.
	 */
	public boolean unloadQuestSlot(final String slot) {
		final List<IQuest> qfound = _getAllBySlot(slot);
		final int qcount = qfound.size();
		if (qcount == 0) {
			logger.error("Quest " + slot + " is not loaded", new Throwable());
			return true;
		}
		if (qcount > 1) {
			logger.warn("Multiple instances of " + slot + " were loaded");
		}
		boolean unloaded = true;
		for (final IQuest q: qfound) {
			unloaded = unloaded && unloadQuest(q);
		}
		return unloaded;
	}

	/**
	 * gets a list of incomplete quests in a specified region
	 *
	 * @param player Player to return the list for
	 * @param region Region to check in
	 * @return list of incomplete quests in the region
	 */
	public List<String> getIncompleteQuests(Player player, String region) {
		List<String> res = new LinkedList<String>();
		for (final IQuest quest : quests) {
			if (region.equals(quest.getRegion()) && !quest.isCompleted(player) && quest.isVisibleOnQuestStatus(player)) {
				res.add(quest.getQuestInfo(player).getName());
			}
		}
		return res;
	}



	/**
	 * gets a list of the unique npc names for unstarted quests in a specified region
	 *
	 * @param player Player to return the list for
	 * @param region Region to check in
	 * @return list of the unique npc names for unstarted quests in a specified region
	 */
	public List<String> getNPCNamesForUnstartedQuestsInRegionForLevel(Player player, String region) {
		final int playerlevel = player.getLevel();
		List<String> res = new LinkedList<String>();
		for (final IQuest quest : quests) {
			if (region.equals(quest.getRegion()) && !quest.isStarted(player) && quest.isVisibleOnQuestStatus(player) && quest.getMinLevel()<playerlevel) {
				// don't add a name twice
				if (!res.contains(quest.getNPCName())) {
					res.add(quest.getNPCName());
				}
			}
		}
		return res;
	}


	/**
	 * Gets quest descriptions for unstarted quests in a specified region
	 * matching a specific npc name.
	 *
	 * @param player Player to return the list for
	 * @param region Region to check in
	 * @param name npc name
	 * @return quest description (there may be more than one)
	 */
	public List<String> getQuestDescriptionForUnstartedQuestInRegionFromNPCName(Player player, String region, String name) {
		List<String> res = new LinkedList<String>();
		if (name == null) {
			return res;
		}
		final int playerlevel = player.getLevel();
		for (final IQuest quest : quests) {
			if (region.equals(quest.getRegion()) && !quest.isStarted(player) && quest.isVisibleOnQuestStatus(player) && quest.getMinLevel()<playerlevel && name.equals(quest.getNPCName())) {
				res.add(quest.getQuestInfo(player).getDescription());
			}
		}
		return res;
	}

	/**
	 * Checks if a quest instance has been added to the world.
	 *
	 * @param quest
	 *     <code>IQuest</code> instance to be checked.
	 * @return
	 *     <code>true</code> if the instance matches stored quests.
	 */
	public boolean isLoaded(final IQuest quest) {
		for (final IQuest loaded: quests) {
			if (loaded.equals(quest)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if a quest has been added to the world.
	 *
	 * @param slot
	 *     Quest slot identifier.
	 * @return
	 *     <code>true</code> if quest slot matches stored quests.
	 */
	public boolean isLoadedSlot(final String slot) {
		for (final IQuest loaded: quests) {
			if (loaded.getSlotName().equals(slot)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Retrieves a list of slot identifiers from loaded quests.
	 */
	public List<String> getLoadedSlots() {
		final List<String> slots = new ArrayList<>();
		for (final IQuest q: quests) {
			slots.add(q.getSlotName());
		}
		return slots;
	}
}
