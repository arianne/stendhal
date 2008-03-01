package games.stendhal.client.entity;

import static org.junit.Assert.assertEquals;

import java.awt.geom.Rectangle2D;

import marauroa.common.game.RPObject;

import org.junit.Test;

public class PlayerTest {
	@Test
	public final void testGetHearingArea() {
		RPObject rpo = new RPObject();
		rpo.put("type", "player");
		rpo.put("outfit", 0);
		User pl = new User();
		pl.initialize(rpo);

		Rectangle2D rect = pl.getHearingArea();
		assertEquals(new Rectangle2D.Double(-20.0, -20.0, 40, 40), rect);
		pl.setAudibleRange(4);
		assertEquals(new Rectangle2D.Double(-20.0, -20.0, 40, 40), rect);
	}
}
