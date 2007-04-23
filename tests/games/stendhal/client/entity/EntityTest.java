package games.stendhal.client.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.common.game.RPObject.ID;

import org.junit.Ignore;
import org.junit.Test;

public class EntityTest {

	public class MockEntity2DView extends Entity2DView {

		public MockEntity2DView(Entity entity) {
			super(entity);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Rectangle2D getDrawnArea() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	@Test
	public final void testEntity() {
		Entity en = new MockEntity();
		
		assertEquals(0.0,en.getX());
		assertEquals(0.0,en.getY());
	
		
	}

	
	@Test // throws ounnoticed Attribute not found exception
	public final void testEntityInvalidRPObject() {
		Entity en = EntityFactory.createEntity(new RPObject());
		assertEquals(null, en);
	}
	@Test
	public final void testEntityRPObject() {
		RPObject rpo = new RPObject();
		rpo.put("type", "hugo");
		Entity en = new MockEntity();
		en.init(rpo);
		assertEquals("hugo", en.getType());
		assertEquals("hugo", en.getName());

	}

	@Test
	public final void testGet_IDToken() {
		Entity en = new MockEntity();
		assertNotNull(en.ID_Token);

	}

	@Test
	public final void testGetID() {
		
		RPObject rpo = new RPObject();
		rpo.put("type", "hugo");
		rpo.setID(new ID(1, "woohoo"));
		Entity en = new MockEntity();
		en.init(rpo);
		assertNotNull("id must not be null",en.getID());
		assertEquals(1, en.getID().getObjectID());
		assertEquals("woohoo", en.getID().getZoneID());
	}

	@Test
	public final void testGetNamegetType() {
		Entity en;
		RPObject rpo;
		rpo = new RPObject();
		rpo.put("type", "_hugo");
		en = new MockEntity();
		en.init(rpo);
		assertEquals("_hugo", en.getType());
		assertEquals(" hugo", en.getName());
		rpo = new RPObject();
		rpo.put("type", "hugo");
		rpo.put("name", "ragnarok");
		en = new MockEntity();
		en.init(rpo);
		assertEquals("hugo", en.getType());
		assertEquals("ragnarok", en.getName());
	}

	@Test
	public final void testGetXGetY() {
		Entity en;
		en = new MockEntity();

		assertEquals(0.0, en.getX());
		assertEquals(0.0, en.getY());
	}



	

	@Test
	public final void testDistance() {
		Entity en = new MockEntity();
		User.setNull(); 
		User to=null;
		assertEquals(Double.POSITIVE_INFINITY, en.distanceToUser());
		to = new User();
		
 		en.x=3;
 		en.y=4;
		assertEquals(3.0, en.getX());
		assertEquals(4.0, en.getY());
		assertEquals(25.0, en.distanceToUser());
		assertEquals(0.0, to.distanceToUser());
		
	}

	
	@Test
	public final void testGetSprite() {
		Entity en;
		RPObject rpo;
		rpo = new RPObject();
		rpo.put("type", "_hugo");

		en = new MockEntity();
		en.init(rpo);
		
		assertNotNull(en.getSprite());

	}

	@Test
	public final void testSetAudibleRangegetAudibleArea() {
		Entity en;
		en = new MockEntity();
		assertNull(en.getAudibleArea());
		en.setAudibleRange(5d);
		Rectangle2D rectangle = new Rectangle2D.Double(-5d, -5d, 10d, 10d);
		assertEquals(rectangle, en.getAudibleArea());
		en.setAudibleRange(1d);
		rectangle = new Rectangle2D.Double(-1d, -1d, 2d, 2d);
		assertEquals(rectangle, en.getAudibleArea());

	}

	@Test

	public final void testStopped() {
		Entity en = new MockEntity();
		assertTrue(en.stopped());
		
	}


	@Test
	
	public final void testGetNumSlots() {
		Entity en = new MockEntity();
		assertEquals(0,en.getNumSlots());
	}

	@Test

	public final void testGetSlot() {
		Entity en = new MockEntity();
		assertEquals(null,en.getSlot(""));
		
	}

	@Test

	public final void testGetSlots() {
		Entity en = new MockEntity();
		assertEquals(new LinkedList<RPSlot>(),en.getSlots());
	}

	
	@Test
	
	public final void testDefaultAction() {
		Entity en = new MockEntity();
		assertEquals(ActionType.LOOK, en.defaultAction());

	}

	@Test
	public final void testOfferedActions() {
		Entity en = new MockEntity();
		String[] str = new String[1];
		str[0]="Look";
		assertEquals(str, en.offeredActions());
	}

	@Test
	public final void testBuildOfferedActions() {
		Entity en = new MockEntity();
		String [] expected = {"Look"};
		assertEquals(expected, en.offeredActions());
	}

	
	private class MockEntity extends Entity {

	
		public MockEntity() {
			RPObject object = new RPObject();
			object.put("type", "entity");
			this.init(object);
		}

		@Override
		public Rectangle2D getArea() {
			return null;
		}

		@Override
		public Rectangle2D getDrawedArea() {
			return null;
		}

		@Override
		public int getZIndex() {
			return 0;
		}

		@Override
		protected Entity2DView createView() {
			return new MockEntity2DView(this);
		}
		
	}
}
