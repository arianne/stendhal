package games.stendhal.client.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import games.stendhal.common.Direction;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPObject.ID;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class EntityTest {

	@Test
	public final void testEntity() {
		Entity en = new MockEntity();
		assertEquals(0l, en.getModificationCount());
	}

	@Test (expected= AttributeNotFoundException.class)
	public final void testEntityInvalidRPObject() {
		new MockEntity(new RPObject() );
	}
	
	@Test 
	public final void testEntityRPObject() {
		RPObject rpo= new RPObject();
		rpo.put("type", "hugo");
		Entity en = new MockEntity(rpo );
		assertEquals(Direction.STOP, en.getDirection());
		assertEquals("hugo", en.getType());
		assertEquals("hugo", en.getName());
		
		
	}
	
	@Test
	public final void testGet_IDToken() {
		RPObject rpo= new RPObject();
		rpo.put("type", "hugo");
		Entity en = new MockEntity(rpo );
		assertNotNull( en.get_IDToken());
		
	}

	@Test
	public final void testGetID() {
		RPObject rpo= new RPObject();
		rpo.put("type", "hugo");
		rpo.setID(new ID(1,"woohoo"));
		Entity en = new MockEntity(rpo );
		assertNotNull(en.getID());
		assertEquals(1, en.getID().getObjectID());
		assertEquals("woohoo", en.getID().getZoneID());
	}

	@Test
	public final void testGetNamegetType() {
		Entity en;
		RPObject rpo;
		rpo= new RPObject();
		rpo.put("type", "_hugo");
		en = new MockEntity(rpo );
		assertEquals("_hugo", en.getType());
		assertEquals(" hugo", en.getName());
		rpo= new RPObject();
		rpo.put("type", "hugo");
		rpo.put("name", "ragnarok");
		en = new MockEntity(rpo );
		assertEquals("hugo", en.getType());
		assertEquals("ragnarok", en.getName());
	}

	
	@Test
	public final void testGetXGetY() {
		Entity en;
		RPObject rpo;
		rpo= new RPObject();
		rpo.put("type", "_hugo");
		
		en = new MockEntity(rpo );
		
		assertEquals(0.0,en.getX());
		assertEquals(0.0,en.getY());
		en.onMove(3, 4, Direction.STOP, 0);
		assertEquals(3.0,en.getX());
		assertEquals(4.0,en.getY());
		
		
	}

	
	@Test
	public final void testGetDirection() {
		Entity en;
		RPObject rpo;
		rpo= new RPObject();
		rpo.put("type", "_hugo");
		
		en = new MockEntity(rpo );
		assertEquals(Direction.STOP,en.getDirection());
			
	}

	@Test
	public final void testGetPosition() {
		Entity en;
		RPObject rpo;
		rpo= new RPObject();
		rpo.put("type", "_hugo");
		
		en = new MockEntity(rpo );
		assertEquals(new Point2D.Double(0.0,0.0),en.getPosition());
	}

	@Test
	public final void testGetSpeed() {
		Entity en;
		RPObject rpo;
		rpo= new RPObject();
		rpo.put("type", "_hugo");
		
		en = new MockEntity(rpo );
		assertEquals(0.0,en.getSpeed());
	}

	@Test
	public final void testDistance() {
		Entity en;
		RPObject rpo;
		rpo= new RPObject();
		rpo.put("type", "_hugo");
		rpo.put("x", 0);
		rpo.put("y",0);
		en = new MockEntity(rpo );
		en.onMove(3, 4, Direction.STOP, 0);
		assertEquals(3.0,en.getX());
		assertEquals(4.0,en.getY());
		assertEquals(25.0,en.distance(rpo));
	}

	@Test
	@Ignore
	public final void testTranslate() {
		assertEquals("data/sprites/hugo.png", Entity.translate("hugo"));
	}

	@Test
	public final void testGetSprite() {
		Entity en;
		RPObject rpo;
		rpo= new RPObject();
		rpo.put("type", "_hugo");
		
		en = new MockEntity(rpo );
		assertNotNull(en.getSprite());
		
	}


	
	@Test
	public final void testSetAudibleRangegetAudibleArea() {
		Entity en;
		RPObject rpo;
		rpo= new RPObject();
		rpo.put("type", "_hugo");
		
		en = new MockEntity(rpo );
		assertNull(en.getAudibleArea() );
		en.setAudibleRange(5d);
		Rectangle2D rectangle= new Rectangle2D.Double(-5d,-5d,10d,10d); 
		assertEquals(rectangle,en.getAudibleArea());
		en.setAudibleRange(1d);
		 rectangle= new Rectangle2D.Double(-1d,-1d,2d,2d); 
		assertEquals(rectangle,en.getAudibleArea());

	}

	@Test
	@Ignore
	public final void testLoadSprite() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public final void testCalcDeltaMovement() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public final void testOnMove() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public final void testOnStop() {
		Entity en;
		RPObject rpo;
		rpo= new RPObject();
		rpo.put("type", "_hugo");
		
		en = new MockEntity(rpo );
		assertTrue(en.stopped());
		en.onStop(0,0);
		assertTrue(en.stopped());
	}

	@Test
	@Ignore
	public final void testOnEnter() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public final void testOnLeave() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public final void testOnEnterZone() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public final void testOnLeaveZone() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public final void testOnAdded() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public final void testOnChangedAdded() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public final void testOnChangedRemoved() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public final void testOnRemoved() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public final void testOnCollideWith() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public final void testOnCollide() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public final void testDraw() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public final void testMove() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public final void testStopped() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public final void testPlaySoundStringIntIntInt() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public final void testPlaySoundStringIntInt() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public final void testGetNumSlots() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public final void testGetSlot() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public final void testGetSlots() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public final void testGetModificationCount() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public final void testIsModified() {
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
	public final void testDefaultAction() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public final void testOfferedActions() {
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
	public final void testCompareTo() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	@Ignore
	public final void testGetZIndex() {
		fail("Not yet implemented"); // TODO
	}
private class MockEntity extends Entity{

	public MockEntity(RPObject object) {
		super(object);
	}
	public MockEntity() {
		super();
	}
	@Override
	public Rectangle2D getArea() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle2D getDrawedArea() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getZIndex() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
}
