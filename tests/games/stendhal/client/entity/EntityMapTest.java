package games.stendhal.client.entity;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EntityMapTest {

	@Test
	public final void testGetClassStringString() {
		Class entClass = EntityMap.getClass("player", null);
		assertEquals(Player.class, entClass);
		entClass = EntityMap.getClass(null, null);
		assertEquals(null, entClass);
	}

	@Test
	public final void testGetClassGoldsource() {
		Class entClass = EntityMap.getClass("gold_source", null);
		assertEquals(GoldSource.class, entClass);
		entClass = EntityMap.getClass(null, null);
		assertEquals(null, entClass);
	}
}
