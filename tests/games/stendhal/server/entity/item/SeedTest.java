package games.stendhal.server.entity.item;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.RPClass.GrowingPassiveEntityRespawnPointTestHelper;

public class SeedTest {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
		GrowingPassiveEntityRespawnPointTestHelper.generateRPClasses();
	}

	@Test
	public void testOnUsed() {
		Seed seed = (Seed) SingletonRepository.getEntityManager().getItem("seed");
		assertNotNull(seed);
		assertFalse(seed.onUsed(null));
		Player player = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("zone");
		zone.add(player);
		zone.add(seed);
		assertTrue(seed.onUsed(player));
		
	}

}
