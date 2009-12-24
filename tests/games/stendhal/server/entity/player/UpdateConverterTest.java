package games.stendhal.server.entity.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import marauroa.common.Log4J;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

/**
 * Test the UpdateConverter class.
 *
 * @author Martin Fuchs
 */
public class UpdateConverterTest {

	@BeforeClass
	public static void setupClass() {
		Log4J.init();
	}

	/**
	 * Tests for transformString.
	 */
	@Test
	public void testTransformString() {
		assertEquals(null, UpdateConverter.updateItemName(null));
		assertEquals("", UpdateConverter.updateItemName(""));
		assertEquals("chicken", UpdateConverter.updateItemName("chicken"));
		assertEquals("enhanced chainmail", UpdateConverter.updateItemName("chain_armor_+1"));
		assertEquals("enhanced lion shield", UpdateConverter.updateItemName("lion_shield_+1"));
		assertEquals("enhanced lion shield", UpdateConverter.updateItemName("enhanced_lion_shield"));
		assertEquals("black book", UpdateConverter.updateItemName("black_book"));
		assertEquals("black book", UpdateConverter.updateItemName("book_black"));
		assertEquals("black book", UpdateConverter.updateItemName("book black"));
	}

	/**
	 * Tests the killing slot upgrade transformation.
	 */
	@Test
	public void testTransformKillSlot() {
		final Player player = PlayerTestHelper.createPlayer("player");

		RPSlot killSlot = player.getSlot("!kills");
		RPObject killStore = killSlot.getFirst();

		killStore.put("name", "solo");
		killStore.put("monster", "shared");
		killStore.put("cave_rat", "solo");

		final String oldID = killStore.get("id");

		UpdateConverter.updatePlayerRPObject(player);

		killSlot = player.getSlot("!kills");
		killStore = killSlot.getFirst();

		final String idDot = killStore.get(oldID + ".id");
		assertEquals(null, idDot);

		assertTrue(player.hasKilled("name"));
		assertTrue(player.hasKilled("monster"));
		assertFalse(player.hasKilled("cave_rat"));
		assertTrue(player.hasKilled("cave rat"));
	}

	/**
	 * Tests the new killings slot functionality in conjunction with updatePlayerRPObject().
	 */ 
	@Test
	public void testKillingRecords() {
		final Player player = PlayerTestHelper.createPlayer("player");

		player.setSoloKill("name");
		player.setSharedKill("monster");
		player.setSoloKill("cave rat");

		RPSlot killSlot = player.getSlot("!kills");
		RPObject killStore = killSlot.getFirst();
		final String oldID = killStore.get("id");

		UpdateConverter.updatePlayerRPObject(player);

		killSlot = player.getSlot("!kills");
		killStore = killSlot.getFirst();

		final String idDot = killStore.get(oldID + ".id");
		assertEquals(null, idDot);

		assertTrue(player.hasKilled("name"));
		assertTrue(player.hasKilled("monster"));
		assertTrue(player.hasKilled("cave rat"));
	}

	/**
	 * Tests for renameQuest.
	 */
	@Test
	public void testRenameQuest() {
		final Player player = PlayerTestHelper.createPlayer("player");

		// First we use only the old quest slot name.
		player.setQuest("Valo_concoct_potion", "3;mega potion;1200000000000");
		UpdateConverter.updateQuests(player);
		assertNull(player.getQuest("Valo_concoct_potion"));
		assertEquals("3;mega potion;1200000000000", player.getQuest("valo_concoct_potion"));

		// Now add the old name to the existing new one and see if they are accumulated correct.
		player.setQuest("Valo_concoct_potion", "8;mega potion;1300000000000");
		UpdateConverter.updateQuests(player);
		assertNull(player.getQuest("Valo_concoct_potion"));
		assertEquals("11;mega potion;1200000000000", player.getQuest("valo_concoct_potion"));
	}

}
