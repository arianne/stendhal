package games.stendhal.server.entity.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.ActiveEntity;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class GagManagerTest {

	@BeforeClass
	public static void setUp() throws Exception {
		Entity.generateRPClass();
		ActiveEntity.generateRPClass();
		RPEntity.generateRPClass();
		Player.generateRPClass();
		PlayerTestHelper.removePlayer("bob");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testGagAbsentPlayer() {
		Player policeman = PlayerTestHelper.createPlayer();
		Player bob = PlayerTestHelper.createPlayer("bob");
		GagManager.get().gag("bob", policeman, 1, "test");
		assertEquals("Player bob not found", policeman.getPrivateText());
		assertFalse(GagManager.isGagged(bob));
	}

	@Test
	public final void testGagPlayer() {
		Player policeman = PlayerTestHelper.createPlayer();
		Player bob = PlayerTestHelper.createPlayer("bob");
		GagManager.get().gag(bob, policeman, 1, "test", bob.getName());
		assertEquals("You have gagged bob for 1 minutes. Reason: test.",
				policeman.getPrivateText());
		assertTrue(GagManager.isGagged(bob));
		GagManager.get().release(bob);
		assertFalse(GagManager.isGagged(bob));
	}

	@Test
	public final void testnegativ() {
		Player policeman = PlayerTestHelper.createPlayer();
		Player bob = PlayerTestHelper.createPlayer("bob");
		assertEquals(null, policeman.getPrivateText());
		GagManager.get().gag(bob, policeman, -1, "test", bob.getName());
		assertEquals("Infinity (negative numbers) is not supported.", policeman
				.getPrivateText());
		assertFalse(GagManager.isGagged(bob));
	}

	@Test
	public final void testOnLoggedIn() {
		Player policeman = PlayerTestHelper.createPlayer();
		Player bob = PlayerTestHelper.createPlayer("bob");

		GagManager.get().gag(bob, policeman, 1, "test", bob.getName());
		assertEquals("You have gagged bob for 1 minutes. Reason: test.",
				policeman.getPrivateText());
		assertTrue(GagManager.isGagged(bob));
		GagManager.get().onLoggedIn(bob);
		assertTrue(GagManager.isGagged(bob));
		bob.setQuest("gag", "0");
		GagManager.get().onLoggedIn(bob);
		assertFalse(GagManager.isGagged(bob));
	}

	@Test
	public final void testOnLoggedInAfterExpiry() {

		Player bob = PlayerTestHelper.createPlayer("bob");

		bob.setQuest("gag", "" + (System.currentTimeMillis() - 5));
		assertTrue(GagManager.isGagged(bob));
		GagManager.get().onLoggedIn(bob);
		assertFalse(GagManager.isGagged(bob));
	}

	@Test
	public final void testgetTimeremaining() {
		Player bob = PlayerTestHelper.createPlayer();
		assertEquals(0L, GagManager.get().getTimeRemaining(bob));
		bob.setQuest("gag", "" + (System.currentTimeMillis() - 1000));
		assertTrue(GagManager.get().getTimeRemaining(bob) <= -1000);

	}
}
