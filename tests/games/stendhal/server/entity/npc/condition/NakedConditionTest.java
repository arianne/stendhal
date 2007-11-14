package games.stendhal.server.entity.npc.condition;

import static org.junit.Assert.*;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.player.Player;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import utilities.PlayerHelper;
import utilities.PlayerTestHelper;

public class NakedConditionTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	@Ignore
	public final void testHashCode() {
		fail("Not yet implemented");
	}

	@Test
	public final void testFire() {
		Player bob = PlayerTestHelper.createPlayer();
		bob.setOutfit(new Outfit(0));
		assertTrue(bob.getOutfit().isNaked());
		assertTrue(new NakedCondition().fire(bob, null, null));
		bob.setOutfit(new Outfit(100));
		assertFalse("finally dressed", bob.getOutfit().isNaked());
		assertFalse("should be false when dressed", new NakedCondition().fire(
				bob, null, null));
		fail("Not yet implemented");
	}

	@Test
	public final void testToString() {
		assertEquals("naked?", new NakedCondition().toString());
	}

	@Ignore
	@Test

	public final void testEqualsObject() {
		fail("Not yet implemented");
	}

}
