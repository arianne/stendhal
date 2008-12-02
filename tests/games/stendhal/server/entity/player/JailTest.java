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
		StendhalRPZone jailZone = new StendhalRPZone("test_jail", 100, 100);
		MockStendlRPWorld.get().addRPZone(jailZone);
		new Jail().configureZone(jailZone, null);
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
	public final void testrepeatedJailing() throws Exception {

		final Player bob = PlayerTestHelper.createPlayer("bob");
		final StendhalRPZone zone = new StendhalRPZone("knast", 100, 100);
				Jail.jailzone = zone;
		MockStendhalRPRuleProcessor.get().addPlayer(bob);
		String jaillist = SingletonRepository.getJail().listJailed();
		
		SingletonRepository.getJail().imprison("bob", bob, 1, "test");
			assertTrue(Jail.isInJail(bob));
		
		assertEquals("bob: 1 Minutes because: test\n", SingletonRepository.getJail().listJailed().replace(jaillist, ""));
		SingletonRepository.getJail().imprison("bob", bob, 1, "test2");
		assertEquals("bob: 1 Minutes because: test2\n", SingletonRepository.getJail().listJailed().replace(jaillist, ""));
		
	}
	@Test
	public final void testIsInJail() throws Exception {

		StendhalRPZone jail = new StendhalRPZone("testknast");
		Jail jailcnf = new Jail();
		jailcnf.configureZone(jail, null);
		
		
		final Player bob = PlayerTestHelper.createPlayer("bob");
		jail.add(bob);
		
		assertFalse(Jail.isInJail(bob));
		bob.setPosition(1, 1);

		assertTrue(Jail.isInJail(bob));
		
	}

	
}
