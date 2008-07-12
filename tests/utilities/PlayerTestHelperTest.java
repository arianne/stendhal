package utilities;

import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PlayerTestHelperTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testAddEmptySlots() {
		PlayerTestHelper.generatePlayerRPClasses();
		final Player bob = new Player(new RPObject());
		PlayerTestHelper.addEmptySlots(bob);
		
		assertTrue(bob.hasSlot("bag"));
		assertTrue(bob.hasSlot("lhand"));
		assertTrue(bob.hasSlot("rhand"));
		assertTrue(bob.hasSlot("armor"));
		assertTrue(bob.hasSlot("head"));
		assertTrue(bob.hasSlot("legs"));
		assertTrue(bob.hasSlot("feet"));
		assertTrue(bob.hasSlot("finger"));
		assertTrue(bob.hasSlot("cloak"));
		assertTrue(bob.hasSlot("keyring"));
		assertTrue(bob.hasSlot("!quests"));
		assertTrue(bob.hasSlot("!kills"));
		assertTrue(bob.hasSlot("!tutorial"));
		assertTrue(bob.hasSlot("!visited"));

	}

}
