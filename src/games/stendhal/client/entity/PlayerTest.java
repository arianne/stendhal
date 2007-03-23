package games.stendhal.client.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.awt.geom.Rectangle2D;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class PlayerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	@Ignore
	public final void testOnEnter() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public final void testOnChangedAdded() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public final void testGetArea() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public final void testGetDrawedArea() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public final void testBuildOfferedActions() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public final void testOnAction() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public final void testBuildAnimations() {
		fail("Not yet implemented"); // TODO
	}

	@Test (expected=NullPointerException.class)
	public final void testPlayerNull() {
		new Player(null);
		
	}

	@Test (expected=AttributeNotFoundException.class)
	public final void testPlayerNewRPObject() {
		new Player(new RPObject());
		
	}
	@Test // (expected=AttributeNotFoundException.class)
	public final void testPlayer() {
		RPObject rpo = new RPObject();
		rpo.put("type","player");
		new Player(rpo);
		fail("Attribute Outfit cannot be found and  throws an exception, which is not reflected here");
		
	}
	
	@Test
	public final void testGetHearingArea() {
		RPObject rpo = new RPObject();
		rpo.put("type","player");
		Player pl = new Player(rpo);
		Rectangle2D rect = pl.getHearingArea();
		assertEquals(new Rectangle2D.Double(-20.0,-20.0,40,40), rect);
		pl.setAudibleRange(4);
		assertEquals(new Rectangle2D.Double(-20.0,-20.0,40,40), rect);
	}

}
