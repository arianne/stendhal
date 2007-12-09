package games.stendhal.server.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.player.Player;

import org.junit.Test;

import utilities.PlayerTestHelper;

public class EntityTest {
	@Test
	public void testnextTo() throws Exception {
		PlayerTestHelper.generatePlayerRPClasses();
		Entity en = new MockEntity();
		Player pl = PlayerTestHelper.createPlayer();

		en.setPosition(2, 2);

		pl.setPosition(1, 1);
		assertEquals(1, pl.getX());
		assertEquals(1, pl.getY());

		// The function nextTo(Entity, double step) takes into account the width of both objects.
		assertTrue(en.nextTo(pl, 0.25));

		// The second overload nextTo(int x, int y, double step) can only look at the width of one object.
		assertFalse("Player at (1,1) is NOT next to (2,2)",
				en.nextTo(pl.getX(), pl.getY(), 0.25));
		assertFalse("Player at (1,1) is NOT next to (2,2) with distance 0.5",
				en.nextTo(pl.getX(), pl.getY(), 0.5));
		assertFalse("Player at (1,1) is NOT next to (2,2) with distance 0.75",
				en.nextTo(pl.getX(), pl.getY(), 0.75));
		assertTrue("Player at (1,1) is next to (2,2) with distance 1",
				en.nextTo(pl.getX(), pl.getY(), 1));

		pl.setPosition(2, 1);
		assertTrue(en.nextTo(pl, 0.25));
		assertTrue(en.nextTo(pl.getX(), pl.getY(), 1));

		pl.setPosition(3, 1);
		assertTrue(en.nextTo(pl, 0.25));
		assertTrue(en.nextTo(pl.getX(), pl.getY(), 1));

		pl.setPosition(1, 0);
		assertFalse(en.nextTo(pl, 0.25));
		assertFalse(en.nextTo(pl.getX(), pl.getY(), 1));

		pl.setPosition(2, 0);
		assertFalse(en.nextTo(pl, 0.25));
		assertFalse(en.nextTo(pl.getX(), pl.getY(), 1));

		pl.setPosition(3, 0);
		assertFalse(en.nextTo(pl, 0.25));
		assertFalse(en.nextTo(pl.getX(), pl.getY(), 1));

		pl.setPosition(1, 2);
		assertTrue(en.nextTo(pl, 0.25));
		assertTrue(en.nextTo(pl.getX(), pl.getY(), 1));

		pl.setPosition(2, 2);
		assertTrue(en.nextTo(pl, 0.25));
		assertTrue(en.nextTo(pl.getX(), pl.getY(), 1));

		pl.setPosition(3, 2);
		assertTrue(en.nextTo(pl, 0.25));
		assertTrue(en.nextTo(pl.getX(), pl.getY(), 1));

	}

	class MockEntity extends Entity {

	}
}
