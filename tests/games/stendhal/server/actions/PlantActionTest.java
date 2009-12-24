package games.stendhal.server.actions;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Seed;
import games.stendhal.server.entity.mapstuff.spawner.FlowerGrower;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.RPClass.GrowingPassiveEntityRespawnPointTestHelper;

public class PlantActionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
		GrowingPassiveEntityRespawnPointTestHelper.generateRPClasses();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests for executeWithNUllValues.
	 */
	@Test
	public void testExecuteWithNUllValues() {
		final PlantAction plantAction = new PlantAction();
		final Player player = PlayerTestHelper.createPlayer("bob");
		assertNotNull(player);
		final Seed seed = (Seed) SingletonRepository.getEntityManager().getItem("seed");
		assertNotNull(seed);
		assertFalse(plantAction.execute());

		plantAction.setUser(player);
		plantAction.setSeed(null);
		assertFalse(plantAction.execute());

		plantAction.setUser(null);
		plantAction.setSeed(seed);
		assertFalse(plantAction.execute());

	}

	/**
	 * Tests for execute.
	 */
	@Test
	public void testExecute() {
		final PlantAction plantAction = new PlantAction();
		final Player player = PlayerTestHelper.createPlayer("bob");
		assertNotNull(player);
		final StendhalRPZone zone = new StendhalRPZone("zone");
		SingletonRepository.getRPWorld().addRPZone(zone);
		zone.add(player);

		final Seed seed = (Seed) SingletonRepository.getEntityManager().getItem("seed");
		assertNotNull(seed);
		zone.add(seed);
		seed.setPosition(1, 0);

		plantAction.setUser(player);
		plantAction.setSeed(seed);
		assertTrue(plantAction.execute());

		assertNotNull(player.getZone().getEntityAt(1, 0));
		assertTrue(player.getZone().getEntityAt(1, 0) instanceof FlowerGrower);

	}
	

	/**
	 * Tests for executeSeedInBag.
	 */
	@Test
	public void testExecuteSeedInBag() {
		final PlantAction plantAction = new PlantAction();
		final Player player = PlayerTestHelper.createPlayer("bob");
		assertNotNull(player);
		final StendhalRPZone zone = new StendhalRPZone("zone");
		SingletonRepository.getRPWorld().addRPZone(zone);
		zone.add(player);
		
		final Seed seed = (Seed) SingletonRepository.getEntityManager().getItem("seed");
		assertNotNull(seed);
		player.equip("bag", seed);
		
		plantAction.setUser(player);
		plantAction.setSeed(seed);
		assertFalse(plantAction.execute());
	}

	/**
	 * Tests for executeNonameSeed.
	 */
	@Test
	public void testExecuteNonameSeed() {
		final PlantAction plantAction = new PlantAction();
		final Player player = PlayerTestHelper.createPlayer("bob");
		assertNotNull(player);
		final StendhalRPZone zone = new StendhalRPZone("zone");
		SingletonRepository.getRPWorld().addRPZone(zone);
		zone.add(player);

		final Seed seed = (Seed) SingletonRepository.getEntityManager().getItem("seed");
		assertNotNull(seed);
		zone.add(seed);
		seed.setPosition(1, 0);

		plantAction.setUser(player);
		plantAction.setSeed(seed);
		assertTrue(plantAction.execute());

		final Entity entity = player.getZone().getEntityAt(1, 0);
		assertNotNull(entity);
		if (entity instanceof FlowerGrower) {
			final FlowerGrower flg = (FlowerGrower) entity;
			flg.setToFullGrowth();
			flg.onUsed(player);
			assertNull(player.getZone().getEntityAt(1, 0));
			assertTrue(player.isEquipped("lilia"));
		} else {
			fail("seed produced non flowergrower");
		}
		

	}
	
	/**
	 * Tests for executeDaisiesSeed.
	 */
	@Test
	public void testExecuteDaisiesSeed() {
		final PlantAction plantAction = new PlantAction();
		final Player player = PlayerTestHelper.createPlayer("bob");
		assertNotNull(player);
		final StendhalRPZone zone = new StendhalRPZone("zone");
		SingletonRepository.getRPWorld().addRPZone(zone);
		zone.add(player);

		final Seed seed = (Seed) SingletonRepository.getEntityManager().getItem("seed");
		assertNotNull(seed);
		seed.setInfoString("daisies");
		zone.add(seed);
		seed.setPosition(1, 0);

		plantAction.setUser(player);
		plantAction.setSeed(seed);
		assertTrue(plantAction.execute());

		final Entity entity = player.getZone().getEntityAt(1, 0);
		assertNotNull(entity);
		if (entity instanceof FlowerGrower) {
			final FlowerGrower flg = (FlowerGrower) entity;
			flg.setToFullGrowth();
			flg.onUsed(player);
			assertNull(player.getZone().getEntityAt(1, 0));
			assertTrue("player has daisies", player.isEquipped("daisies"));
		} else {
			fail("seed produced non flowergrower");
		}
		

	}
}
