package games.stendhal.client.entity;

import games.stendhal.client.StendhalClient;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPObject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestBuildOfferedActions {
	static List<String> list = null;

	@Before
	public void setUpBefore() throws Exception {
		list = new ArrayList<String>();
	}

	@After
	public void tearDownAfter() throws Exception {
	}

	@Test
	public void testEntity() {
		StendhalClient.get();

		MockEntity me = new MockEntity();
		me.buildOfferedActions(list);
		List<String> expected = new ArrayList<String>();
		expected.add("Look");
		Assert.assertEquals(expected, list);
		Assert.assertEquals(expected.toArray(), me.offeredActions());
	}

	@Test
	public void testSheep() throws Exception {
		StendhalClient.get();
		Sheep sh = new Sheep(new MockRPObject("sheep", null));
		List<String> expected = new ArrayList<String>();
		expected.add("Look");
		expected.add("Attack");
		expected.add("Own");
		sh.buildOfferedActions(list);
		Assert.assertNotNull(list);
		Assert.assertEquals(expected, list);

	}

	@Test
	public void testChest() throws Exception {
		StendhalClient.get();
		Chest sh = new Chest(new MockRPObject("chest", null));
		List<String> expected = new ArrayList<String>();
		expected.add("Look");
		expected.add("Open");
		sh.buildOfferedActions(list);
		Assert.assertNotNull(list);
		Assert.assertEquals(expected, list);
		sh.onChangedAdded(new MockRPObject(), new MockRPObject());
		list.clear();
		expected.clear();
		expected.add("Look");
		expected.add("Inspect");
		expected.add("Close");
		sh.buildOfferedActions(list);
		Assert.assertEquals(expected, list);
	}

	@Test
	public void testDoor() throws Exception {
		StendhalClient.get();
		Door door = new Door(new MockRPObject("door", "skulldoor"));
		List<String> expected = new ArrayList<String>();
		expected.add("Look");
		expected.add("Open");
		door.buildOfferedActions(list);
		Assert.assertNotNull(list);
		Assert.assertEquals(expected, list);

		door.onChangedAdded(new MockRPObject(), new MockRPObject());
		list.clear();
		expected.clear();
		expected.add("Look");
		expected.add("Close");
		door.buildOfferedActions(list);
		Assert.assertEquals(expected, list);
	}

	class MockEntity extends Entity {

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

		/*
		 * (non-Javadoc)
		 * 
		 * @see games.stendhal.client.entity.Entity#buildOfferedActions(java.util.List)
		 */
		@Override
		protected void buildOfferedActions(List<String> list) {
			// TODO Auto-generated method stub
			super.buildOfferedActions(list);
		}

	}

	private class MockRPObject extends RPObject {
		private String _type;

		private String _eclass;

		MockRPObject() {

		}

		MockRPObject(String type, String eclass) {
			_type = type;
			_eclass = eclass;
		}

		@Override
		public boolean has(String attribute) {

			return true;
		}

		@Override
		public String get(String attribute) throws AttributeNotFoundException {
			if (attribute.equals("type")) {
				return _type;
			} else {
				return _eclass;
			}

		}

		public int getInt(String dir) {
			return 1;
		}
	}
}
