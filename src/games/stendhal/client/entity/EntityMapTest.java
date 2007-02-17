package games.stendhal.client.entity;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EntityMapTest {

	
	@Test
	public final void testGetClassStringString() {
		Class entClass= EntityMap.getClass("player", null); 
		assertEquals(Player.class, entClass);
		entClass= EntityMap.getClass(null, null);
		assertEquals(null, entClass);
	}

}
