/***************************************************************************
 *                     Copyright © 2023 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.rp.achievement.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getSpeakerNPC;
import static utilities.ZoneAndPlayerTestImpl.setupZone;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.BalloonForBobby;
import games.stendhal.server.maps.quests.BeerForHayunn;
import games.stendhal.server.maps.quests.Blackjack;
import games.stendhal.server.maps.quests.BowsForOuchit;
import games.stendhal.server.maps.quests.DailyMonsterQuest;
import games.stendhal.server.maps.quests.ElfPrincess;
import games.stendhal.server.maps.quests.GoodiesForRudolph;
import games.stendhal.server.maps.quests.HatForMonogenes;
import games.stendhal.server.maps.quests.HerbsForCarmen;
import games.stendhal.server.maps.quests.HungryJoshua;
import games.stendhal.server.maps.quests.IQuest;
import games.stendhal.server.maps.quests.KillMonks;
import games.stendhal.server.maps.quests.LearnAboutOrbs;
import games.stendhal.server.maps.quests.LookBookforCeryl;
import games.stendhal.server.maps.quests.Maze;
import games.stendhal.server.maps.quests.MealForGroongo;
import games.stendhal.server.maps.quests.MedicineForTad;
import games.stendhal.server.maps.quests.MeetHackim;
import games.stendhal.server.maps.quests.MeetHayunn;
import games.stendhal.server.maps.quests.MeetIo;
import games.stendhal.server.maps.quests.MeetKetteh;
import games.stendhal.server.maps.quests.MeetMonogenes;
import games.stendhal.server.maps.quests.NewsFromHackim;
import games.stendhal.server.maps.quests.PizzaDelivery;
import games.stendhal.server.maps.quests.RestockFlowerShop;
import games.stendhal.server.maps.quests.SheepGrowing;
import games.stendhal.server.maps.quests.houses.HouseBuyingMain;
import utilities.AchievementTestHelper;
import utilities.QuestHelper;
import utilities.QuestRunner;


public class QuestAchievementFactoryTest extends AchievementTestHelper {

	private Player player;
	private StendhalRPZone testzone;
	private List<IQuest> qloaded = new ArrayList<>();


	@Before
	public void setUp() {
		player = createPlayerWithOutFit("player");
		assertNotNull(player);
		init(player);
		testzone = setupZone("testzone");
		registerPlayer(player, "testzone");
		assertNotNull(player.getZone());
	}

	@After
	public void tearDown() {
		QuestHelper.unloadQuests(qloaded);
		for (int idx = qloaded.size()-1; idx >= 0; idx--) {
			assertFalse(QuestHelper.isLoaded(qloaded.get(idx)));
			qloaded.remove(idx);
		}
		assertEquals(0, qloaded.size());
		//~ assertEquals(0, QuestHelper.getLoadedSlots().size());
		// clean up NPCs
		//~ assertTrue(NPCTestHelper.removeAllNPCs());
		removePlayer(player);
	}

	private void loadQuests(final IQuest... qs) {
		final int qcount = qloaded.size();
		QuestHelper.loadQuests(qs);
		for (final IQuest q: qs) {
			assertTrue(QuestHelper.isLoaded(q));
			qloaded.add(q);
		}
		assertEquals(qcount + qs.length, qloaded.size());
	}

	private void setupZones(final String... zones) {
		for (final String zone: zones) {
			setupZone(zone);
		}
	}

	private void loadConfigurators(final ZoneConfigurator... zc) {
		setupZone("testzone", zc);
	}

	/* TODO:
	 * - Helper of Ados City Dwellers
	 * - Quest Junkie
	 */

	@Test
	public void testFairgoer() {
		final int required = 5;
		final String id = "quest.bobby.balloons.000" + required;
		loadConfigurators(new games.stendhal.server.maps.fado.city.SmallBoyNPC());
		final SpeakerNPC bobby = getSpeakerNPC("Bobby");
		assertNotNull(bobby);
		loadQuests(new BalloonForBobby());
		final Engine en = bobby.getEngine();
		for (int completions = 0; completions < required; completions++) {
			assertFalse(achievementReached(player, id));
			player.setOutfit(new Outfit("detail=1"));
			en.step(player, "hi");
			en.step(player, "yes");
			en.step(player, "bye");
		}
		assertEquals(String.valueOf(required), player.getQuest("balloon_bobby", 1));
		assertTrue(achievementReached(player, id));
	}

	@Test
	public void testFaiumonisCasanova() {
		final int required = 25;
		final String id = "quest.special.elf_princess.00" + required;
		setupZones("int_semos_house");
		loadConfigurators(
			new games.stendhal.server.maps.nalwor.tower.PrincessNPC(),
			new games.stendhal.server.maps.semos.house.FlowerSellerNPC()
		);
		assertNotNull(getSpeakerNPC("Tywysoga"));
		assertNotNull(getSpeakerNPC("Rose Leigh"));
		loadQuests(new ElfPrincess());
		for (int completions = 0; completions < required; completions++) {
			assertFalse(achievementReached(player, id));
			QuestRunner.doQuestElfPrincess(player);
		}
		assertEquals(String.valueOf(required), player.getQuest("elf_princess", 2));
		assertTrue(achievementReached(player, id));
	}

	@Test
	public void testFloralFondness() {
		final int required = 50;
		final String id = "quest.flowershop.00" + required;
		loadConfigurators(new games.stendhal.server.maps.nalwor.flowershop.FlowerGrowerNPC());
		assertNotNull(getSpeakerNPC("Seremela"));
		loadQuests(new RestockFlowerShop());
		for (int completions = 0; completions < required; completions++) {
			assertFalse(achievementReached(player, id));
			QuestRunner.doQuestRestockFlowerShop(player);
		}
		assertEquals(String.valueOf(required), player.getQuest("restock_flowershop", 2));
		assertTrue(achievementReached(player, id));
	}

	@Test
	public void testHeretic() {
		final int required = 25;
		final String id = "quest.special.kill_monks.00" + required;
		loadConfigurators(new games.stendhal.server.maps.ados.city.ManWithHatNPC());
		assertNotNull(getSpeakerNPC("Andy"));
		loadQuests(new KillMonks());
		for (int completions = 0; completions < required; completions++) {
			assertFalse(achievementReached(player, id));
			QuestRunner.doQuestKillMonks(player);
		}
		assertEquals(String.valueOf(required), player.getQuest("kill_monks", 2));
		assertTrue(achievementReached(player, id));
	}

	@Test
	public void testPathfinder() {
		final String id = "quest.special.maze";
		loadConfigurators(new games.stendhal.server.maps.ados.magician_house.WizardNPC());
		final SpeakerNPC haizen = getSpeakerNPC("Haizen");
		assertNotNull(haizen);
		loadQuests(new Maze());
		assertFalse(achievementReached(player, id));
		final Engine en = haizen.getEngine();
		en.step(player, "hi");
		en.step(player, "maze");
		en.step(player, "yes");
		final StendhalRPZone maze = player.getZone();
		assertNotNull(maze);
		// check that player is in maze
		assertEquals("player_maze", maze.getName());
		final Portal exit = maze.getPortals().get(0);
		player.setPosition(exit.getX(), exit.getY());
		assertEquals(exit.getX(), player.getX());
		assertEquals(exit.getY(), player.getY());
		exit.onUsed(player);
		// portal doesn't teleport in tests
		//~ assertEquals("testzone", player.getZone().getName());
		assertTrue(achievementReached(player, id));
	}

	@Test
	public void testPatientlyWaitingOnGrumpy() {
		final int required = 50;
		final String id = "quest.groongo.meals.00" + required;
		loadConfigurators(
			new games.stendhal.server.maps.fado.hotel.TroublesomeCustomerNPC(),
			new games.stendhal.server.maps.fado.hotel.HotelChefNPC()
		);
		assertNotNull(getSpeakerNPC("Groongo Rahnnt"));
		assertNotNull(getSpeakerNPC("Stefan"));
		loadQuests(new MealForGroongo());
		for (int completions = 0; completions < required; completions++) {
			assertFalse(achievementReached(player, id));
			QuestRunner.doQuestMealForGroongo(player);
		}
		assertEquals(String.valueOf(required), player.getQuest("meal_for_groongo", 7));
		assertTrue(achievementReached(player, id));
	}

	// FIXME: this may not be accurate until dynamically loading quests from region is fixed
	@Test
	public void testAideToSemosFolk() {
		final String id = "quest.special.semos";
		loadConfigurators(
			// Balduin
			new games.stendhal.server.maps.ados.rock.WeaponsCollectorNPC(),
			// Carmen
			new games.stendhal.server.maps.semos.city.HealerNPC(),
			// Ceryl
			new games.stendhal.server.maps.semos.library.LibrarianNPC(),
			// Eliza
			new games.stendhal.server.maps.ados.coast.FerryConveyerNPC(),
			// Fidorea
			new games.stendhal.server.maps.ados.city.MakeupArtistNPC(),
			// Hackim Easso
			new games.stendhal.server.maps.semos.blacksmith.BlacksmithAssistantNPC(),
			// Haizen
			new games.stendhal.server.maps.ados.magician_house.WizardNPC(),
			// Hayunn Naratha
			new games.stendhal.server.maps.semos.guardhouse.RetiredAdventurerNPC(),
			// Ilisa
			new games.stendhal.server.maps.semos.temple.HealerNPC(),
			// Io Flotto
			new games.stendhal.server.maps.semos.temple.TelepathNPC(),
			// Jenny
			new games.stendhal.server.maps.semos.plains.MillerNPC(),
			// Joshua
			new games.stendhal.server.maps.ados.goldsmith.GoldsmithNPC(),
			// Jynath
			new games.stendhal.server.maps.orril.magician_house.WitchNPC(),
			// Karl
			new games.stendhal.server.maps.ados.forest.FarmerNPC(),
			// Katinka
			new games.stendhal.server.maps.ados.outside.AnimalKeeperNPC(),
			// Ketteh Wehoh
			new games.stendhal.server.maps.semos.townhall.DecencyAndMannersWardenNPC(),
			// Leander
			new games.stendhal.server.maps.semos.bakery.ChefNPC(),
			// Marcus
			new games.stendhal.server.maps.semos.jail.GuardNPC(),
			// Martin Farmer
			new games.stendhal.server.maps.ados.wall.HolidayingManNPC(),
			// Mayor Sakhs
			new games.stendhal.server.maps.semos.townhall.MayorNPC(),
			// Monogenes
			new games.stendhal.server.maps.semos.city.GreeterNPC(),
			// Nishiya
			new games.stendhal.server.maps.semos.village.SheepSellerNPC(),
			// Ouchit
			new games.stendhal.server.maps.semos.tavern.BowAndArrowSellerNPC(),
			// Rudolph
			new games.stendhal.server.maps.semos.city.RudolphNPC(),
			// Sato
			new games.stendhal.server.maps.semos.city.SheepBuyerNPC(),
			// Tad
			new games.stendhal.server.maps.semos.hostel.BoyNPC(),
			// Tor'Koom
			new games.stendhal.server.maps.semos.dungeon.SheepBuyerNPC(),
			// Xin Blanca
			new games.stendhal.server.maps.semos.tavern.TraderNPC(),
			// Xoderos
			new games.stendhal.server.maps.semos.blacksmith.BlacksmithNPC()
		);
		// Cyk
		new HouseBuyingMain().createAthorNPC(testzone);
		// Ramon
		setupZone("-1_athor_ship_w2");
		loadQuests(new Blackjack());

		// FIXME: loading quests from resource broken
		//~ qloaded.addAll(QuestHelper.loadRegionalQuests(Region.SEMOS_CITY));
		loadQuests(
			new MeetHayunn(),
			new BeerForHayunn(),
			new MeetMonogenes(),
			new HatForMonogenes(),
			new SheepGrowing(),
			new MedicineForTad(),
			new MeetIo(),
			new MeetHackim(),
			new NewsFromHackim(),
			new BowsForOuchit(),
			new HungryJoshua(),
			new LookBookforCeryl(),
			new MeetKetteh(),
			new PizzaDelivery(),
			new HerbsForCarmen(),
			new LearnAboutOrbs(),
			new DailyMonsterQuest(),
			new GoodiesForRudolph()
		);

		// Meet Hayunn Naratha
		assertNotNull(getSpeakerNPC("Hayunn Naratha"));
		//~ assertTrue(QuestHelper.isLoaded("meet_hayunn"));
		QuestRunner.doQuestMeetHayunn(player);
		assertFalse(achievementReached(player, id));
		player.drop("studded shield");
		assertFalse(player.isEquipped("studded shield"));

		// Beer for Hayunn
		//~ assertTrue(QuestHelper.isLoaded("beer_hayunn"));
		QuestRunner.doQuestBeerForHayunn(player);
		assertFalse(achievementReached(player, id));

		// Meet Monogenes
		assertNotNull(getSpeakerNPC("Monogenes"));
		//~ assertTrue(QuestHelper.isLoaded("Monogenes"));
		QuestRunner.doQuestMeetMonogenes(player);
		assertFalse(achievementReached(player, id));

		// Hat for Monogenes
		//~ assertTrue(QuestHelper.isLoaded("hat_monogenes"));
		QuestRunner.doQuestHatForMonogenes(player);
		assertFalse(achievementReached(player, id));

		// Sheep Growing for Nishiya
		assertNotNull(getSpeakerNPC("Nishiya"));
		assertNotNull(getSpeakerNPC("Sato"));
		//~ assertTrue(QuestHelper.isLoaded("sheep_growing"));
		QuestRunner.doQuestSheepGrowing(player);
		assertFalse(achievementReached(player, id));

		// Medicine for Tad
		assertNotNull(getSpeakerNPC("Tad"));
		assertNotNull(getSpeakerNPC("Ilisa"));
		//~ assertTrue(QuestHelper.isLoaded("introduce_players"));
		QuestRunner.doQuestMedicineForTad(player);
		assertFalse(achievementReached(player, id));

		// Meet Io
		assertNotNull(getSpeakerNPC("Io Flotto"));
		//~ assertTrue(QuestHelper.isLoaded("meet_io"));
		QuestRunner.doQuestMeetIo(player);
		assertFalse(achievementReached(player, id));

		// Meet Hackim
		assertNotNull(getSpeakerNPC("Hackim Easso"));
		//~ assertTrue(QuestHelper.isLoaded("meet_hackim"));
		QuestRunner.doQuestMeetHackim(player);
		assertFalse(achievementReached(player, id));

		// News from Hackim
		assertNotNull(getSpeakerNPC("Xin Blanca"));
		//~ assertTrue(QuestHelper.isLoaded("news_hackim"));
		QuestRunner.doQuestNewsFromHackim(player);
		assertFalse(achievementReached(player, id));
		player.drop("leather legs");
		assertFalse(player.isEquipped("leather legs"));

		// Bows for Ouchit
		assertNotNull(getSpeakerNPC("Ouchit"));
		//~ assertNotNull(getSpeakerNPC("Karl"));
		//~ assertTrue(QuestHelper.isLoaded("bows_ouchit"));
		QuestRunner.doQuestBowsForOuchit(player);
		assertFalse(achievementReached(player, id));

		// Hungry Joshua
		assertNotNull(getSpeakerNPC("Xoderos"));
		assertNotNull(getSpeakerNPC("Joshua"));
		//~ assertTrue(QuestHelper.isLoaded("hungry_joshua"));
		QuestRunner.doQuestHungryJoshua(player);
		assertFalse(achievementReached(player, id));

		// Look for a Book for Ceryl
		assertNotNull(getSpeakerNPC("Ceryl"));
		assertNotNull(getSpeakerNPC("Jynath"));
		//~ assertTrue(QuestHelper.isLoaded("ceryl_book"));
		QuestRunner.doQuestLookBookforCeryl(player);
		assertFalse(achievementReached(player, id));

		// Meet Ketteh Wehoh
		assertNotNull(getSpeakerNPC("Ketteh Wehoh"));
		//~ assertTrue(QuestHelper.isLoaded("Ketteh"));
		QuestRunner.doQuestMeetKetteh(player);
		assertFalse(achievementReached(player, id));

		// Pizza Delivery
		assertNotNull(getSpeakerNPC("Leander"));
		assertNotNull(getSpeakerNPC("Jenny"));
		assertNotNull(getSpeakerNPC("Tor'Koom"));
		assertNotNull(getSpeakerNPC("Martin Farmer"));
		assertNotNull(getSpeakerNPC("Haizen"));
		assertNotNull(getSpeakerNPC("Fidorea"));
		assertNotNull(getSpeakerNPC("Eliza"));
		assertNotNull(getSpeakerNPC("Katinka"));
		assertNotNull(getSpeakerNPC("Cyk"));
		assertNotNull(getSpeakerNPC("Marcus"));
		assertNotNull(getSpeakerNPC("Ramon"));
		assertNotNull(getSpeakerNPC("Balduin"));
		//~ assertTrue(QuestHelper.isLoaded("pizza_delivery"));
		QuestRunner.doQuestPizzaDelivery(player);
		assertFalse(achievementReached(player, id));

		// Herbs for Carmen
		assertNotNull(getSpeakerNPC("Carmen"));
		//~ assertTrue(QuestHelper.isLoaded("herbs_for_carmen"));
		QuestRunner.doQuestHerbsForCarmen(player);
		assertFalse(achievementReached(player, id));
		player.drop("minor potion", player.getNumberOfEquipped("minor potion"));
		assertFalse(player.isEquipped("minor potion"));

		// Learn About Orbs
		assertNotNull(getSpeakerNPC("Ilisa"));
		//~ assertTrue(QuestHelper.isLoaded("learn_scrying"));
		QuestRunner.doQuestLearnAboutOrbs(player);
		assertFalse(achievementReached(player, id));

		// initialize creatures
		if (SingletonRepository.getEntityManager().getCreatures().size() == 0) {
			SingletonRepository.getEntityManager().populateCreatureList();
		}

		// Daily Monster
		assertNotNull(getSpeakerNPC("Mayor Sakhs"));
		//~ assertTrue(QuestHelper.isLoaded("daily"));
		QuestRunner.doQuestDailyMonster(player);
		assertFalse(achievementReached(player, id));

		// Goodies for Rudolph
		assertNotNull(getSpeakerNPC("Rudolph"));
		//~ assertTrue(QuestHelper.isLoaded("goodies_rudolph"));
		QuestRunner.doQuestGoodiesForRudolph(player);

		assertTrue(achievementReached(player, id));
	}
}
