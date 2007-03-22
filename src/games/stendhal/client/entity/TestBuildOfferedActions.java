package games.stendhal.client.entity;

import games.stendhal.client.StendhalClient;


import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests if the offeredActions contextMenu is provided with the right keywords
 * uses a MOCKEntitty and a MOCKRPObject for initialisation
 */
public class TestBuildOfferedActions {
	static List<String> list = null;

	@Before
	public void setUpBefore() throws Exception {
		list = new ArrayList<String>();
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
		
		RPObject rp =  new MockRPObject("sheep", null);
		Entity en = EntityFabric.createEntity(rp);
		
		List<String> expected = new ArrayList<String>();
		expected.add("Look");
		expected.add("Attack");
		expected.add("Own");
		en.buildOfferedActions(list);
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
public void testCarrot() throws Exception{
	StendhalClient.get();
	RPObject rp = new MockRPObject("growing_entity_spawner", "items/grower/carrot_grower");
	rp.add(	"max_ripeness",1);	
	rp.add(	"width",1);	
	rp.add(	"height",1);	
	Entity en = EntityFabric.createEntity(rp);
	List<String> expected = new ArrayList<String>();
	expected.add("Look");
	expected.add("Pick");
    en.buildOfferedActions(list);
	Assert.assertNotNull(list);
	Assert.assertEquals(expected, list);
	Assert.assertEquals(new String[]{"Pick","Look"}, en.offeredActions());
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
	@Test
	public void testBox(){
		StendhalClient.get();
		Box box = new Box(new MockRPObject("item", "box"));
		List<String> expected = new ArrayList<String>();
		expected.add("Look");
		expected.add("Open");
		box.buildOfferedActions(list);
		Assert.assertNotNull(list);
		Assert.assertEquals(expected, list);
		
	}

	class MockEntity extends Entity {

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

		/*
		 * (non-Javadoc)
		 * 
		 * @see games.stendhal.client.entity.Entity#buildOfferedActions(java.util.List)
		 */
		@Override
		protected void buildOfferedActions(List<String> list) {
			super.buildOfferedActions(list);
		}

	}

	private class MockRPObject extends RPObject {
		private String _type;

		private String _eclass;

		MockRPObject() {
				// no super(), so implementation needed for use
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
			}
			return _eclass;

		}

		@Override
		public int getInt(String dir) {
			return 1;
		}

		@Override
		public double getDouble(String arg0) throws AttributeNotFoundException {
			return 0.0;
		}
	}
}
