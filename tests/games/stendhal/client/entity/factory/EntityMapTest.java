package games.stendhal.client.entity.factory;

import static org.junit.Assert.assertEquals;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.GoldSource;
import games.stendhal.client.entity.Player;

import org.junit.Test;

public class EntityMapTest {

	@Test
	public final void testGetClassStringString() {
		Class< ? extends Entity> entClass = EntityMap.getClass("player", null);
		assertEquals(Player.class, entClass);
		entClass = EntityMap.getClass(null, null);
		assertEquals(null, entClass);
	}

	@Test
	public final void testGetClassGoldsource() {
		Class< ? extends Entity> entClass = EntityMap.getClass("gold_source",
				null);
		assertEquals(GoldSource.class, entClass);
		entClass = EntityMap.getClass(null, null);
		assertEquals(null, entClass);
	}
}
