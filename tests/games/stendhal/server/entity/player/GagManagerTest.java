package games.stendhal.server.entity.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.engine.SingletonRepository;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.PrivateTextMockingTestPlayer;

public class GagManagerTest {

	@BeforeClass
	public static void setUp() throws Exception {
		PlayerTestHelper.generatePlayerRPClasses();
		PlayerTestHelper.removePlayer("bob");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testGagAbsentPlayer() {
		PrivateTextMockingTestPlayer policeman = PlayerTestHelper.createPrivateTextMockingTestPlayer("player");
		PrivateTextMockingTestPlayer bob = PlayerTestHelper.createPrivateTextMockingTestPlayer("bob");
		SingletonRepository.getGagManager().gag("bob", policeman, 1, "test");
		assertEquals("Player bob not found", policeman.getPrivateTextString());
		assertFalse(GagManager.isGagged(bob));
	}

	@Test
	public final void testGagPlayer() {
		PrivateTextMockingTestPlayer policeman = PlayerTestHelper.createPrivateTextMockingTestPlayer("player");
		PrivateTextMockingTestPlayer bob = PlayerTestHelper.createPrivateTextMockingTestPlayer("bob");
		SingletonRepository.getGagManager().gag(bob, policeman, 1, "test", bob.getName());
		assertEquals("You have gagged bob for 1 minutes. Reason: test.",
				policeman.getPrivateTextString());
		assertTrue(GagManager.isGagged(bob));
		SingletonRepository.getGagManager().release(bob);
		assertFalse(GagManager.isGagged(bob));
	}

	@Test
	public final void testnegativ() {
		PrivateTextMockingTestPlayer policeman = PlayerTestHelper.createPrivateTextMockingTestPlayer("player");
		PrivateTextMockingTestPlayer bob = PlayerTestHelper.createPrivateTextMockingTestPlayer("bob");
		assertEquals("", policeman.getPrivateTextString());
		SingletonRepository.getGagManager().gag(bob, policeman, -1, "test", bob.getName());
		assertEquals("Infinity (negative numbers) is not supported.", policeman
				.getPrivateTextString());
		assertFalse(GagManager.isGagged(bob));
	}

	@Test
	public final void testOnLoggedIn() {
		PrivateTextMockingTestPlayer policeman = PlayerTestHelper.createPrivateTextMockingTestPlayer("player");
		PrivateTextMockingTestPlayer bob = PlayerTestHelper.createPrivateTextMockingTestPlayer("bob");

		SingletonRepository.getGagManager().gag(bob, policeman, 1, "test", bob.getName());
		assertEquals("You have gagged bob for 1 minutes. Reason: test.",
				policeman.getPrivateTextString());
		assertTrue(GagManager.isGagged(bob));
		SingletonRepository.getGagManager().onLoggedIn(bob);
		assertTrue(GagManager.isGagged(bob));
		bob.setQuest("gag", "0");
		SingletonRepository.getGagManager().onLoggedIn(bob);
		assertFalse(GagManager.isGagged(bob));
	}

	@Test
	public final void testOnLoggedInAfterExpiry() {
		Player bob = PlayerTestHelper.createPlayer("bob");

		bob.setQuest("gag", "" + (System.currentTimeMillis() - 5));
		assertTrue(GagManager.isGagged(bob));
		SingletonRepository.getGagManager().onLoggedIn(bob);
		assertFalse(GagManager.isGagged(bob));
	}

	@Test
	public final void testgetTimeremaining() {
		Player bob = PlayerTestHelper.createPlayer("player");
		assertEquals(0L, SingletonRepository.getGagManager().getTimeRemaining(bob));
		bob.setQuest("gag", "" + (System.currentTimeMillis() - 1000));
		assertTrue(SingletonRepository.getGagManager().getTimeRemaining(bob) <= -1000);
	}
}
