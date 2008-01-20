package games.stendhal.server.entity.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import marauroa.common.Log4J;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

/**
 * Test the UpdateConverter class (was previous UnderScoreConverter).
 *
 * @author Martin Fuchs
 */
public class UpdateConverterTest {

	@BeforeClass
	public static void setupClass() {
		Log4J.init();
	}

	@Test
	public void testUnderscoreConversion() {
		assertEquals(null, UpdateConverter.transformItemName(null));
		assertEquals("", UpdateConverter.transformItemName(""));
		assertEquals(" ", UpdateConverter.transformItemName(" "));
		assertEquals(" ", UpdateConverter.transformItemName("_"));
		assertEquals("x ", UpdateConverter.transformItemName("x_"));
		assertEquals(" x", UpdateConverter.transformItemName("_x"));
		assertEquals("abc 1", UpdateConverter.transformItemName("abc_1"));
		assertEquals("abc def", UpdateConverter.transformItemName("abc def"));
		assertEquals("abc def ghi", UpdateConverter.transformItemName("abc_def ghi"));
		assertEquals("abc def ghi", UpdateConverter.transformItemName("abc_def_ghi"));
		assertEquals("abc def ghi", UpdateConverter.transformItemName("abc def ghi"));
		assertEquals("chicken", UpdateConverter.transformItemName("chicken"));
	}

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
		Player player = PlayerTestHelper.createPlayer("player");

		RPSlot killSlot = player.getSlot("!kills");
		RPObject obj = killSlot.getFirst();

		obj.put("name", "solo");
		obj.put("monster", "shared");
		obj.put("cave_rat", "solo");

		UpdateConverter.updatePlayerRPObject(player);

		assertTrue(player.hasKilled("name"));
		assertTrue(player.hasKilled("monster"));
		assertFalse(player.hasKilled("cave_rat"));
		assertTrue(player.hasKilled("cave rat"));
	}

	/**
	 * Tests the new killings slot functionality.
	 */ 
	@Test
	public void testKillingRecords() {
		Player player = PlayerTestHelper.createPlayer("player");

		player.setSoloKill("name");
		player.setSharedKill("monster");
		player.setSoloKill("cave rat");

		UpdateConverter.updatePlayerRPObject(player);

		assertTrue(player.hasKilled("name"));
		assertTrue(player.hasKilled("monster"));
		assertTrue(player.hasKilled("cave rat"));
	}

}
