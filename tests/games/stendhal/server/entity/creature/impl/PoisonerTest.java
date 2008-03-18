package games.stendhal.server.entity.creature.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class PoisonerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
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

	@Test
	public void testPoisoner() {
		String poisontype ="greater poison";
		ConsumableItem poison = (ConsumableItem) SingletonRepository.getEntityManager().getItem(poisontype);

		Poisoner poisoner = new Poisoner(100, poison);
		Player victim = PlayerTestHelper.createPlayer("bob");
		poisoner.attack(victim);
		assertTrue(victim.isPoisoned());
	}

	@Test
	public void testPoisonerProbabilityZero() {
		String poisontype ="greater poison";
		ConsumableItem poison = (ConsumableItem) SingletonRepository.getEntityManager().getItem(poisontype);

		Poisoner poisoner = new Poisoner(0, poison);
		Player victim = PlayerTestHelper.createPlayer("bob");
		poisoner.attack(victim);
		assertFalse(victim.isPoisoned());
	}
}
