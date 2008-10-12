package games.stendhal.client.entity;

import static org.junit.Assert.*;

import java.awt.geom.Rectangle2D;

import marauroa.common.game.RPObject;

import org.junit.Test;

public class PlayerTest {
	@Test
	public final void testGetHearingArea() {
		final RPObject rpo = new RPObject();
		rpo.put("type", "player");
		rpo.put("outfit", 0);
		final User pl = new User();
		pl.initialize(rpo);

		final Rectangle2D rect = pl.getHearingArea();
		assertEquals(new Rectangle2D.Double(-20.0, -20.0, 40, 40), rect);
	}
	
	@Test
	public final void testIsBadBoy() {
		Player george = new Player();
		assertFalse(george.isBadBoy());
		
		RPObject player = new RPObject();
		player.put("x", 1);
		player.put("y", 1);
		
		RPObject changes = new RPObject();
		george.onChangedAdded(player, changes);
		assertFalse(george.isBadBoy());
		
		changes.put("last_player_kill_time", 1);
		george.onChangedAdded(player, changes);
		assertTrue(george.isBadBoy());
		
	}
		
	@Test
	public final void testAmnesty() {
		Player george = new Player();
		assertFalse(george.isBadBoy());
		
		RPObject player = new RPObject();
		player.put("x", 1);
		player.put("y", 1);
		
		RPObject changes = new RPObject();
		changes.put("last_player_kill_time", 1);
		george.onChangedAdded(player, changes);
		assertTrue(george.isBadBoy());
		
		
		george.onChangedRemoved(player, changes);
		assertFalse(george.isBadBoy());
	}
	
	@Test
	public final void testHappy() throws Exception {
		Player barrack = new Player();
		RPObject player = new RPObject();
		player.put("x", 1);
		player.put("y", 1);
		RPObject changes = new RPObject();
		changes.put("happy", "I feel fine!");
		assertFalse(barrack.isHappy());
		barrack.onChangedAdded(player, changes);
		assertTrue(barrack.isHappy());
	}
		
}
