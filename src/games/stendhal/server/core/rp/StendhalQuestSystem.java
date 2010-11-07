/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
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


import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.AdosDeathmatch;
import games.stendhal.server.maps.quests.AmazonPrincess;
import games.stendhal.server.maps.quests.ArmorForDagobert;
import games.stendhal.server.maps.quests.Balloon;
import games.stendhal.server.maps.quests.BalloonForBobby;
import games.stendhal.server.maps.quests.BeerForHayunn;
import games.stendhal.server.maps.quests.Blackjack;
import games.stendhal.server.maps.quests.BowsForOuchit;
import games.stendhal.server.maps.quests.Campfire;
import games.stendhal.server.maps.quests.CleanStorageSpace;
import games.stendhal.server.maps.quests.CloakCollector;
import games.stendhal.server.maps.quests.CloakCollector2;
import games.stendhal.server.maps.quests.CloaksForBario;
import games.stendhal.server.maps.quests.ClubOfThorns;
import games.stendhal.server.maps.quests.CoalForHaunchy;
import games.stendhal.server.maps.quests.CrownForTheWannaBeKing;
import games.stendhal.server.maps.quests.DailyItemQuest;
import games.stendhal.server.maps.quests.DailyMonsterQuest;
import games.stendhal.server.maps.quests.DiceGambling;
import games.stendhal.server.maps.quests.DragonLair;
import games.stendhal.server.maps.quests.ElfPrincess;
import games.stendhal.server.maps.quests.ElvishArmor;
import games.stendhal.server.maps.quests.FindGhosts;
import games.stendhal.server.maps.quests.FindRatChildren;
import games.stendhal.server.maps.quests.FishSoup;
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
import games.stendhal.server.maps.quests.JailedBarbarian;
import games.stendhal.server.maps.quests.JailedDwarf;
import games.stendhal.server.maps.quests.KanmararnSoldiers;
import games.stendhal.server.maps.quests.KillDarkElves;
import games.stendhal.server.maps.quests.KillDhohrNuggetcutter;
import games.stendhal.server.maps.quests.KillEnemyArmy;
import games.stendhal.server.maps.quests.KillGnomes;
import games.stendhal.server.maps.quests.KillSpiders;
import games.stendhal.server.maps.quests.KoboldishTorcibud;
import games.stendhal.server.maps.quests.LearnAboutKarma;
import games.stendhal.server.maps.quests.LearnAboutOrbs;
import games.stendhal.server.maps.quests.LookBookforCeryl;
import games.stendhal.server.maps.quests.LookUpQuote;
import games.stendhal.server.maps.quests.Marriage;
import games.stendhal.server.maps.quests.Maze;
import games.stendhal.server.maps.quests.McPeglegIOU;
import games.stendhal.server.maps.quests.MedicineForTad;
import games.stendhal.server.maps.quests.MeetBunny;
import games.stendhal.server.maps.quests.MeetHackim;
import games.stendhal.server.maps.quests.MeetHayunn;
import games.stendhal.server.maps.quests.MeetIo;
import games.stendhal.server.maps.quests.MeetKetteh;
import games.stendhal.server.maps.quests.MeetMonogenes;
import games.stendhal.server.maps.quests.MeetSanta;
import games.stendhal.server.maps.quests.MeetZynn;
import games.stendhal.server.maps.quests.MithrilCloak;
import games.stendhal.server.maps.quests.MixtureForOrtiv;
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
import games.stendhal.server.maps.quests.SuppliesForPhalk;
import games.stendhal.server.maps.quests.TakeGoldforGrafindle;
import games.stendhal.server.maps.quests.ThePiedPiper;
import games.stendhal.server.maps.quests.ToysCollector;
import games.stendhal.server.maps.quests.UltimateCollector;
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


/**
 * Loads and manages all quests.
 */
public class StendhalQuestSystem {

	private static final StendhalQuestSystem stendhalQuestSystem = new StendhalQuestSystem();

	
	/** the logger instance. */
	private static final Logger logger = Logger.getLogger(StendhalQuestSystem.class);

	private final List<IQuest> quests = new LinkedList<IQuest>();


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

		loadQuest(new AdosDeathmatch());
		loadQuest(new AmazonPrincess());
		loadQuest(new ArmorForDagobert());
		loadQuest(new Balloon());
		loadQuest(new BalloonForBobby());
		loadQuest(new BeerForHayunn());
		loadQuest(new Blackjack());
		loadQuest(new BowsForOuchit());
		loadQuest(new Campfire());
		loadQuest(new CleanStorageSpace());
		loadQuest(new CloakCollector());
		loadQuest(new CloakCollector2());
		loadQuest(new CloaksForBario());
		loadQuest(new ClubOfThorns());
		loadQuest(new CoalForHaunchy());
		loadQuest(new CrownForTheWannaBeKing());
		loadQuest(new DailyItemQuest());
		loadQuest(new DailyMonsterQuest());
		loadQuest(new DiceGambling());
		loadQuest(new DragonLair());
		loadQuest(new ElfPrincess());
		loadQuest(new ElvishArmor());
		loadQuest(new FindGhosts());
		loadQuest(new FindRatChildren());
		loadQuest(new FishermansLicenseQuiz());
		loadQuest(new FishermansLicenseCollector());
		loadQuest(new FishSoup());
		loadQuest(new HatForMonogenes());
		loadQuest(new HelpTomi());
		loadQuest(new HelpMrsYeti());
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
		loadQuest(new KanmararnSoldiers());
		//loadQuest(new KillBlordroughs());
		loadQuest(new KillDarkElves());
		loadQuest(new KillDhohrNuggetcutter());
		loadQuest(new KillEnemyArmy());
		loadQuest(new KillGnomes());
		loadQuest(new KillSpiders());
		loadQuest(new KoboldishTorcibud());
		loadQuest(new Marriage());
		loadQuest(new Maze());
		loadQuest(new McPeglegIOU());
		loadQuest(new MeetBunny());
		loadQuest(new MedicineForTad());
		loadQuest(new MeetHackim());
		loadQuest(new MeetHayunn());
		loadQuest(new MeetIo());
		loadQuest(new MeetKetteh());
		loadQuest(new MeetMonogenes());
		loadQuest(new MeetSanta());
		loadQuest(new MeetZynn());
		loadQuest(new MithrilCloak());
		loadQuest(new MixtureForOrtiv());
		loadQuest(new NewsFromHackim());
		loadQuest(new ObsidianKnife());
		loadQuest(new PizzaDelivery());
		loadQuest(new PlinksToy());
		loadQuest(new RainbowBeans());
		loadQuest(new ReverseArrow());
		loadQuest(new RingMaker());
		loadQuest(new SadScientist());
		loadQuest(new PaperChase()); // needs to be loaded before SemosMineTownRevivalWeeks
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
		loadQuest(new UltimateCollector());
		loadQuest(new VampireSword());
		loadQuest(new WeaponsCollector());
		loadQuest(new WeaponsCollector2());
		loadQuest(new WeeklyItemQuest());
		loadQuest(new WizardBank());
		loadQuest(new ZekielsPracticalTestQuest());
		loadQuest(new ZooFood());

		if (System.getProperty("stendhal.minetown") != null) {
			loadQuest(new SemosMineTownRevivalWeeks());
		}


		TurnNotifier.get().notifyInTurns(10, new DumpSpeakerNPCtoDB());
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
	 * adds a quest to the world
	 *
	 * @param quest Quest to add
	 */
	private void initQuestAndAddToWorld(final IQuest quest) {
		logger.info("Loading Quest: " + quest.getName());
		quest.addToWorld();
		quests.add(quest);
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
		
		// XXX TODO: add information here about is quest repeatable or no
		final List<String> history = quest.getHistory(player);
		for (final String entry : history) {
			sb.append("\t * " + entry + "\r\n");
		}
		
		final List<String> hints = quest.getHint(player);
		for (final String entry : hints) {
			sb.append("\t - " + entry + "\r\n");
		}	
	}

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

	public String listQuest(final Player player, final String questName) {
		final StringBuilder sb = new StringBuilder();
		for (final IQuest quest : quests) {
			if (quest.getName().equals(questName)) {
				dumpQuest(sb, quest, player);
			}
		}
		return sb.toString();
	}
	
	// this is being used for the InspectAction 
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

	public void unloadQuest(String questName) {
		IQuest quest = getQuest(questName);
		if (quest == null) {
			logger.error("Quest " + questName + " is not loaded", new Throwable());
			return;
		}

		boolean res = quest.removeFromWorld();
		if (res) {
			quests.remove(quest);
			logger.info("Unloading Quest: " + quest.getName());
		} else {
			logger.error(this.getClass() + " cannot be removed from the world");
		}
	}
}
