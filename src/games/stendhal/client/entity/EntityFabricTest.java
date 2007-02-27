package games.stendhal.client.entity;

import static org.junit.Assert.*;
import games.stendhal.client.GameObjects;

import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPObject;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class EntityFabricTest {
private class MockRPObject extends RPObject{
 private String _type;
private String _eclass;

MockRPObject() {
	
}
 MockRPObject(String type, String eclass) {
		_type = type;
		_eclass=eclass;
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
	
}
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		GameObjects.createInstance(null);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public final void testCreateEntity() {
		RPObject rp = new MockRPObject("item","food");
		Entity en=EntityFabric.createEntity(rp); 
		assertNotNull("entity should be created",en);
		assertEquals("we should have created an item by now","item", en.getType() );
		
		
	}

}
