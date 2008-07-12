package games.stendhal.server.entity.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.PrivateTextMockingTestPlayer;
import utilities.RPClass.ArrestWarrentTestHelper;

public class JailTest {
	@BeforeClass
	public static void setUpClass() throws Exception {
		Log4J.init();
		ArrestWarrentTestHelper.generateRPClasses();
		MockStendhalRPRuleProcessor.get().clearPlayers();
		MockStendlRPWorld.get().addRPZone(new StendhalRPZone(Jail.DEFAULT_JAIL_ZONE, 100, 100));
		MockStendlRPWorld.get().addRPZone(new StendhalRPZone("-3_semos_jail", 100, 100));
	}

	@After
	public void tearDown() throws Exception {
		// release bob from jail in case he is still imprisoned
		SingletonRepository.getJail().release("bob");

		//TODO remove arrest warrant in any case - also if bob was not online when arresting him

		MockStendhalRPRuleProcessor.get().clearPlayers();
	}

	@Test
	public final void testCriminalNotInworld() {
		final PrivateTextMockingTestPlayer policeman = PlayerTestHelper.createPrivateTextMockingTestPlayer("police officer");
		PlayerTestHelper.createPlayer("bob");
		SingletonRepository.getJail().imprison("bob", policeman, 1, "test");
		assertEquals("You have jailed bob for 1 minutes. Reason: test.\r\n"
			+ "Player bob is not online, but the arrest warrant has been recorded anyway.", policeman.getPrivateTextString());
	}

	@Test
	public final void testCriminalimprison() throws Exception {
		final PrivateTextMockingTestPlayer policeman = PlayerTestHelper.createPrivateTextMockingTestPlayer("police officer");
		final PrivateTextMockingTestPlayer bob = PlayerTestHelper.createPrivateTextMockingTestPlayer("bob");
		MockStendhalRPRuleProcessor.get().addPlayer(bob);
		PlayerTestHelper.registerPlayer(bob, "-3_semos_jail");

		SingletonRepository.getJail().imprison(bob.getName(), policeman, 1, "test");
		assertTrue(Jail.isInJail(bob));
		assertEquals("You have jailed bob for 1 minutes. Reason: test.",
				policeman.getPrivateTextString());
		SingletonRepository.getJail().release(bob);
		assertFalse(Jail.isInJail(bob));
	}

	@Test
	public final void testIsInJail() throws Exception {

		final Player bob = PlayerTestHelper.createPlayer("bob");
		final StendhalRPZone zone = SingletonRepository.getRPWorld().getZone(Jail.DEFAULT_JAIL_ZONE);
		zone.add(bob);
		Jail.jailzone = zone;
		SingletonRepository.getJail().imprison("bob", bob, 1, "test");
		assertFalse(Jail.isInJail(bob));

		MockStendhalRPRuleProcessor.get().addPlayer(bob);
		bob.setPosition(1, 1);
		assertTrue(Jail.isInJail(bob));
		final Player nobob = PlayerTestHelper.createPlayer("police_officer");
		final StendhalRPZone noJail = new StendhalRPZone("noJail");
		noJail.add(nobob);
		nobob.setPosition(0, 0);
		SingletonRepository.getJail().imprison("nobob", nobob, 1, "test");
		assertFalse(Jail.isInJail(nobob));

		bob.setPosition(1, 1);
		assertFalse(Jail.isInJail(nobob));
	}

}
