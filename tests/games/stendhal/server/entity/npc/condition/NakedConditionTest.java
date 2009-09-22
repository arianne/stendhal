package games.stendhal.server.entity.npc.condition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class NakedConditionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		MockStendlRPWorld.reset();
	}

	@Test
	public final void testFire() {
		final Player bob = PlayerTestHelper.createPlayer("player");
		bob.setOutfit(new Outfit(0));
		assertTrue(bob.getOutfit().isNaked());
		assertTrue(new NakedCondition().fire(bob, null, null));
		bob.setOutfit(new Outfit(100));
		assertFalse("finally dressed", bob.getOutfit().isNaked());
		assertFalse("should be false when dressed", new NakedCondition().fire(
				bob, null, null));

	}

	@Test
	public final void testToString() {
		assertEquals("naked?", new NakedCondition().toString());
	}

	@Test
	public void testEquals() throws Throwable {

		assertFalse(new NakedCondition().equals(null));

		final NakedCondition obj = new NakedCondition();
		assertTrue(obj.equals(obj));
		assertTrue(new NakedCondition().equals(new NakedCondition()));

	}

	@Test
	public final void testHashCode() {
		final NakedCondition obj = new NakedCondition();
		assertEquals(obj.hashCode(), obj.hashCode());
		assertEquals(new NakedCondition().hashCode(),
				new NakedCondition().hashCode());

	}
}
