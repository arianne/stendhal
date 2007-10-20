package games.stendhal.server.entity.slot;

import static org.junit.Assert.fail;
import games.stendhal.server.entity.Entity;
import marauroa.common.game.RPObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class LootableSlotTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	@Ignore
	public final void testIsReachableForTakingThingsOutOfBy() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public final void testLootableSlot() {
		fail("Not yet implemented");
	}

	@Test
	public final void testAddRPObject() {
		LootableSlot ls = new LootableSlot(new Entity(){});
		ls.add(new RPObject()); // NPE caused by not assigned Owner
		ls.add(new RPObject());
		ls.add(new RPObject());
		ls.add(new RPObject());
		ls.add(new RPObject());
	}

}
