package games.stendhal.server.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPObject;

import org.junit.Test;

public class EntityTest {
	@Test
	public void testnextTo() throws Exception {
		Entity en = new MockEntity();
		Player pl = new Player(new RPObject());
		en.set(2, 2);
		pl.set(1, 1);
		assertEquals(1, pl.getX());
		assertEquals(1, pl.getY());
		assertTrue(en.nextTo(pl, 0.25));
		assertTrue("Player at (1,1) is next to 2,2", en.nextTo(pl.getX(), pl
				.getY(), 0.25));

		pl.set(2, 1);
		assertTrue(en.nextTo(pl, 0.25));
		assertTrue(en.nextTo(pl.getX(), pl.getY(), 0.25));
		pl.set(3, 1);
		assertTrue(en.nextTo(pl, 0.25));
		assertTrue(en.nextTo(pl.getX(), pl.getY(), 0.25));
		pl.set(1, 0);
		assertTrue(en.nextTo(pl, 0.25));
		assertTrue(en.nextTo(pl.getX(), pl.getY(), 0.25));
		pl.set(2, 0);
		assertTrue(en.nextTo(pl, 0.25));
		assertTrue(en.nextTo(pl.getX(), pl.getY(), 0.25));
		pl.set(3, 0);
		assertTrue(en.nextTo(pl, 0.25));
		assertTrue(en.nextTo(pl.getX(), pl.getY(), 0.25));
		pl.set(1, 2);
		assertTrue(en.nextTo(pl, 0.25));
		assertTrue(en.nextTo(pl.getX(), pl.getY(), 0.25));
		pl.set(2, 2);
		assertTrue(en.nextTo(pl, 0.25));
		assertTrue(en.nextTo(pl.getX(), pl.getY(), 0.25));
		pl.set(3, 2);
		assertTrue(en.nextTo(pl, 0.25));
		assertTrue(en.nextTo(pl.getX(), pl.getY(), 0.25));

	}

	class MockEntity extends Entity {

	}
}
