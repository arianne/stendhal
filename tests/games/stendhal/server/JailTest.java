package games.stendhal.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class JailTest {
	private static final String ZONE_CONTENT = "Level -1/semos/jail.tmx";

	@BeforeClass
	public static void setUpClass() throws Exception {
		Log4J.init();
		MockStendhalRPRuleProcessor.get();
		MockStendlRPWorld.get();
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testCriminalNotInworld() {

		Player policeman = PlayerTestHelper.createPlayer();
		Player bob = PlayerTestHelper.createPlayer();
		bob.setName("bob");
		Jail.get().imprison("bob", policeman, 1, "test");
		assertEquals("Player bob not found", policeman.getPrivateText());

	}

	@Test
	public final void testCriminalNofreeCell() {
		Player policeman = PlayerTestHelper.createPlayer();
		Player bob = PlayerTestHelper.createPlayer();
		bob.setName("bob");
		Jail.jailzone = new StendhalRPZone(Jail.DEFAULT_JAIL_ZONE);
		Jail.get().imprison(bob, policeman, 1, "test");
		assertTrue((policeman.getPrivateText()).contains("Could not find a cell forbob"));

	}

	@Test
	public final void testCriminalimprison() throws Exception {
		Player policeman = PlayerTestHelper.createPlayer();
		Player bob = PlayerTestHelper.createPlayer();
		bob.setName("bob");
		Jail.jailzone = StendhalRPWorld.get().addArea(Jail.DEFAULT_JAIL_ZONE,
				ZONE_CONTENT);
		StendhalRPWorld.get().addArea("-3_semos_jail",
				"Level -3/semos/jail_walk.tmx");

		Jail.get().imprison(bob, policeman, 1, "test");
		assertTrue(Jail.isInJail(bob));
		assertEquals("You have jailed bob for 1 minutes. Reason: test.",
				policeman.getPrivateText());
		Jail.get().release(bob);
		assertFalse(Jail.isInJail(bob));

	}

	@Test
	public final void testIsInJail() throws Exception {

		Player bob = PlayerTestHelper.createPlayer();
		StendhalRPZone zone = StendhalRPWorld.get().addArea(
				Jail.DEFAULT_JAIL_ZONE, ZONE_CONTENT);
		zone.add(bob);
		Jail.jailzone = zone;
		Jail.get().imprison("bob", bob, 1, "test");
		assertFalse(Jail.isInJail(bob));

		bob.setPosition(1, 1);
		assertTrue(Jail.isInJail(bob));
		Player nobob = PlayerTestHelper.createPlayer();
		StendhalRPZone noJail = new StendhalRPZone("noJail");
		noJail.add(nobob);
		nobob.setPosition(0, 0);
		Jail.get().imprison("nobob", nobob, 1, "test");
		assertFalse(Jail.isInJail(nobob));

		bob.setPosition(1, 1);
		assertFalse(Jail.isInJail(nobob));

	}

}
