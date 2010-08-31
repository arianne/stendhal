package games.stendhal.client.entity.factory;

import static org.junit.Assert.assertEquals;
import games.stendhal.client.entity.Gate;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.Player;
import games.stendhal.client.entity.UseableEntity;

import org.junit.Test;

public class EntityMapTest {

	/**
	 * Tests for getClassStringString.
	 */
	@Test
	public final void testGetClassStringString() {
		Class< ? extends IEntity> entClass = EntityMap.getClass("player", null,
				null);
		assertEquals(Player.class, entClass);
		entClass = EntityMap.getClass(null, null, null);
		assertEquals(null, entClass);
	}

	/**
	 * Tests for getClassGoldsource.
	 */
	@Test
	public final void testGetClassGoldsource() {
		Class< ? extends IEntity> entClass = EntityMap.getClass("gold_source",
				null, null);
		assertEquals(UseableEntity.class, entClass);
		entClass = EntityMap.getClass(null, null, null);
		assertEquals(null, entClass);
	}

	/**
	 * Tests for getSeed.
	 */
	@Test
	public final void testGetSeed() {
		Class< ? extends IEntity> entClass = EntityMap.getClass("gold_source",
				null, null);
		assertEquals(UseableEntity.class, entClass);
		entClass = EntityMap.getClass(null, null, null);
		assertEquals(null, entClass);
	}
	/**
	 * Tests for getGate.
	 */
	@Test
	public final void testGetGate() {
		Class< ? extends IEntity> entClass = EntityMap.getClass("gate",
				null, null);
		assertEquals(Gate.class, entClass);
		entClass = EntityMap.getClass(null, null, null);
		assertEquals(null, entClass);
	}
}
