package utilities;

import static org.junit.Assert.assertNotNull;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.player.Player;

import org.junit.After;
import org.junit.Before;

/**
 * Base class for quest tests.
 *
 * @author Martin Fuchs
 */
public abstract class ZoneAndPlayerTestImpl extends QuestHelper {

	/** Zone used in the test. */
	protected StendhalRPZone zone;

	/** Player object to be used in the test. */
	protected Player player;

	/**
	 * Protected constructor to initialize the zone member variable.
	 *
	 * @param zoneName
	 */
	protected ZoneAndPlayerTestImpl(String zoneName) {
		zone = SingletonRepository.getRPWorld().getZone(zoneName);
		assertNotNull(zone);
	}

	/**
	 * Setup and configure zone with the configurators given as parameters before starting the test.
	 *
	 * @param zoneName
	 * @param zoneConfigurators
	 */
	protected static void setupZone(String zoneName, ZoneConfigurator... zoneConfigurators) {
		StendhalRPZone zone = setupZone(zoneName);

		for (ZoneConfigurator zoneConfigurator : zoneConfigurators) {
			zoneConfigurator.configureZone(zone, null);
		}
    }

	/**
	 * Setup zone before starting the test.
	 *
	 * @param zoneName
	 */
	protected static StendhalRPZone setupZone(String zoneName) {
		StendhalRPZone zone = new StendhalRPZone(zoneName);
		SingletonRepository.getRPWorld().addRPZone(zone);

		return zone;
    }

	/**
	 * Create the player to be used in the test.
	 *
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		player = createPlayer("player");

		registerPlayer(player, zone);
	}

	/**
	 * Reset all involved NPCs.
	 *
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		removePlayer(player);
	}

}
