package games.stendhal.server.entity.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class JailTest {
	@BeforeClass
	public static void setUpClass() throws Exception {
		Log4J.init();
		MockStendhalRPRuleProcessor.get().clearPlayers();
		MockStendlRPWorld.get().addRPZone(new StendhalRPZone(Jail.DEFAULT_JAIL_ZONE, 100, 100));
		MockStendlRPWorld.get().addRPZone(new StendhalRPZone("-3_semos_jail", 100, 100));
	}

	@Test
	public final void testCriminalNotInworld() {
		Player policeman = PlayerTestHelper.createPlayer("police_officer");
		PlayerTestHelper.createPlayer("bob");
		Jail.get().imprison("bob", policeman, 1, "test");
		assertEquals("You have jailed bob for 1 minutes. Reason: test.\r\n"
			+ "Player bob is not online, but the arrest warrant has been recorded anyway.", policeman.getPrivateText());
	}

	@Test
	public final void testCriminalimprison() throws Exception {
		Player policeman = PlayerTestHelper.createPlayer("police_officer");
		Player bob = PlayerTestHelper.createPlayer("bob");
		PlayerTestHelper.registerPlayer(bob, "-3_semos_jail");

		Jail.get().imprison(bob.getName(), policeman, 1, "test");
		assertTrue(Jail.isInJail(bob));
		assertEquals("You have jailed bob for 1 minutes. Reason: test.",
				policeman.getPrivateText());
		Jail.get().release(bob);
		assertFalse(Jail.isInJail(bob));

	}

	@Test
	public final void testIsInJail() throws Exception {

		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = StendhalRPWorld.get().getZone(Jail.DEFAULT_JAIL_ZONE);
		zone.add(bob);
		Jail.jailzone = zone;
		Jail.get().imprison("bob", bob, 1, "test");
		assertFalse(Jail.isInJail(bob));

		bob.setPosition(1, 1);
		assertTrue(Jail.isInJail(bob));
		Player nobob = PlayerTestHelper.createPlayer("police_officer");
		StendhalRPZone noJail = new StendhalRPZone("noJail");
		noJail.add(nobob);
		nobob.setPosition(0, 0);
		Jail.get().imprison("nobob", nobob, 1, "test");
		assertFalse(Jail.isInJail(nobob));

		bob.setPosition(1, 1);
		assertFalse(Jail.isInJail(nobob));
	}

}
