package games.stendhal.client.entity;

import static org.junit.Assert.assertEquals;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import marauroa.common.game.RPObject;

import org.junit.Assert;
import org.junit.Test;

public class PlayerTest {

	@Test

	public final void testBuildOfferedActions() {
		RPObject rpo = new RPObject();
		rpo.put("type", "player");
		rpo.put("outfit",0);
		Player pl =new Player();
		pl.initialize(rpo);
		List<String> expected = new ArrayList<String>();
		expected.add("Look");
		expected.add("Attack");
		//expected.add("Push");
		
		expected.add("Add to Buddies");
		//expected.add("Manage Guilds");
		ArrayList<String> list = new ArrayList<String>();
		pl.buildOfferedActions(list);
		Assert.assertNotNull(list);
		Assert.assertEquals(expected, list);
	}

	@Test
	public final void testGetHearingArea() {
		RPObject rpo = new RPObject();
		rpo.put("type", "player");
		rpo.put("outfit",0);
		User pl = new User();
		pl.initialize(rpo);
		Rectangle2D rect = pl.getHearingArea();
		assertEquals(new Rectangle2D.Double(-20.0, -20.0, 40, 40), rect);
		pl.setAudibleRange(4);
		assertEquals(new Rectangle2D.Double(-20.0, -20.0, 40, 40), rect);
	}

}
