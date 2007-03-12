package games.stendhal.client.entity;

import static org.junit.Assert.*;

import java.awt.geom.Rectangle2D;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PlayerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testOnEnter() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testOnChangedAdded() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetArea() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testGetDrawedArea() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testBuildOfferedActions() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testOnAction() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testBuildAnimations() {
		fail("Not yet implemented"); // TODO
	}

	@Test (expected=NullPointerException.class)
	public final void testPlayerNull() {
		Player pl = new Player(null);
		
	}

	@Test (expected=AttributeNotFoundException.class)
	public final void testPlayerNewRPObject() {
		Player pl = new Player(new RPObject());
		
	}
	@Test 
	public final void testPlayer() {
		RPObject rpo = new RPObject();
		rpo.put("type","player");
		Player pl = (Player) EntityFabric.createEntity(rpo);
		
	}
	
	@Test
	public final void testGetHearingArea() {
		RPObject rpo = new RPObject();
		rpo.put("type","player");
		Player pl = (Player) EntityFabric.createEntity(rpo);
		Rectangle2D rect = pl.getHearingArea();
		assertEquals(new Rectangle2D.Double(-20.0,-20.0,40,40), rect);
		pl.setAudibleRange(4);
		assertEquals(new Rectangle2D.Double(-20.0,-20.0,40,40), rect);
	}

}
