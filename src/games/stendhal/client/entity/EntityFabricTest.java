package games.stendhal.client.entity;

import static org.junit.Assert.*;
import games.stendhal.client.GameObjects;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPObject;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class EntityFabricTest {
	private class MockRPObject extends RPObject {
		private String _type;

		private String _eclass;

		private String _subclass;

		MockRPObject() {

		}

		MockRPObject(String type, String eclass) {
			_type = type;
			_eclass = eclass;
		}

		public MockRPObject(String type, String eclass, String subclass) {
			_type = type;
			_eclass = eclass;
			_subclass= subclass;
		}

		@Override
		public boolean has(String attribute) {
			if (attribute.equals("subtype"))
					return _subclass==null;
			return true;
		}

		@Override
		public String get(String attribute) throws AttributeNotFoundException {
			if (attribute.equals("type")) {
				return _type;
			} else if(attribute.equals("class")){
				return _eclass;
			}
				else {//if (attribute.equals("subtype")){
					return _subclass;
				}
			

		}

	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		GameObjects.createInstance(null);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public final void testCreateApple() {
		RPObject rp = new MockRPObject("item", "food","apple");
		Entity en = EntityFabric.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created an item by now", "item", en
				.getType());
		
	}
	@Test
	public final void testCreateSign() {
		RPObject rp =  new MockRPObject("blackboard", null);
		Entity en = EntityFabric.createEntity(rp);
		assertNotNull("entity should be created", en);
		assertEquals("we should have created a sign by now", Sign.class, en.getClass());

	}
	
}
