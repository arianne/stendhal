package games.stendhal.client.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.geom.Rectangle2D;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPObject.ID;

import org.junit.Test;

public class EntityTest {

	@Test
	public final void testEntity() {
		final Entity en = new MockEntity();

		assertEquals(0.0, en.getX(), 0.001);
		assertEquals(0.0, en.getY(), 0.001);

	}

	@Test
	public final void testInitialize() {
		MockEntity en;
		RPObject rpo;
		rpo = new RPObject();
		rpo.put("type", "_hugo");

		en = new MockEntity();
		assertEquals(0, en.count);
		en.initialize(rpo);
		assertEquals("onPosition should only be called once ", 1, en.count);
	}

	@Test
	public final void testEntityRPObject() {
		final RPObject rpo = new RPObject();
		rpo.put("type", "hugo");
		rpo.put("name", "bob");

		final Entity en = new MockEntity();
		en.initialize(rpo);
		assertEquals("hugo", en.getType());
		assertEquals("bob", en.getName());

	}

	@Test
	public final void testGetID() {

		final RPObject rpo = new RPObject();
		rpo.put("type", "hugo");
		rpo.setID(new ID(1, "woohoo"));
		final Entity en = new MockEntity();
		en.initialize(rpo);
		assertNotNull("id must not be null", en.getID());
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
		en.initialize(rpo);
		assertEquals("_hugo", en.getType());

		rpo = new RPObject();
		rpo.put("type", "hugo");
		rpo.put("name", "ragnarok");
		en = new MockEntity();
		en.initialize(rpo);
		assertEquals("hugo", en.getType());
		assertEquals("ragnarok", en.getName());
	}

	@Test
	public final void testGetXGetY() {
		Entity en;
		en = new MockEntity();

		assertEquals(0.0, en.getX(), 0.001);
		assertEquals(0.0, en.getY(), 0.001);
	}

	@Test
	public final void testDistance() {
		final Entity en = new MockEntity();
		User.setNull();
		User to = null;
		assertEquals(Double.POSITIVE_INFINITY, User.squaredDistanceTo(en.x, en.y), 0.001);
		to = new User();

		en.x = 3;
		en.y = 4;
		assertEquals(3.0, en.getX(), 0.001);
		assertEquals(4.0, en.getY(), 0.001);
		assertEquals(25.0, User.squaredDistanceTo(en.x, en.y), 0.001);
		assertEquals(0.0, User.squaredDistanceTo(to.x, to.y), 0.001);

	}

	@Test
	public final void testGetSlot() {
		final Entity en = new MockEntity();
		assertEquals(null, en.getSlot(""));

	}

	private class MockEntity extends Entity {
		private int count;

		public MockEntity() {
			rpObject = new RPObject();
			rpObject.put("type", "entity");
		}

		@Override
		public Rectangle2D getArea() {
			return null;
		}

		@Override
		protected void onPosition(final double x, final double y) {
			count++;
			super.onPosition(x, y);

		}

	}
}
