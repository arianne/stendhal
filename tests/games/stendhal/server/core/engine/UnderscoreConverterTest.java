package games.stendhal.server.core.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import games.stendhal.server.entity.player.Player;
import marauroa.common.Log4J;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

/**
 * Test the UnderscoreConverter class.
 *
 * @author Martin Fuchs
 */
public class UnderscoreConverterTest {
	@BeforeClass
	public static void setupClass() {
		Log4J.init();
	}

	@Test
	public void testTransformString() {
		assertNull(UnderscoreConverter.transform(null));
		assertEquals("", UnderscoreConverter.transform(""));
		assertEquals(" ", UnderscoreConverter.transform(" "));
		assertEquals(" ", UnderscoreConverter.transform("_"));
		assertEquals("x ", UnderscoreConverter.transform("x_"));
		assertEquals(" x", UnderscoreConverter.transform("_x"));
		assertEquals("abc 1", UnderscoreConverter.transform("abc_1"));
		assertEquals("abc def", UnderscoreConverter.transform("abc_def"));
		assertEquals("abc def", UnderscoreConverter.transform("abc def"));
		assertEquals("abc def ghi", UnderscoreConverter.transform("abc_def_ghi"));
		assertEquals("abc def ghi", UnderscoreConverter.transform("abc def ghi"));
	}

	@Test
	public void testTransformName() {
		RPObject obj = new RPObject();

		obj.put("name", "abc_123");
		obj.put("name2", "abc_123");
		UnderscoreConverter.transformNames(obj);
		assertEquals("abc 123", obj.get("name"));	// name should be transformed
		assertEquals("abc_123", obj.get("name2"));	// no change expected
	}

	@Test
	public void testTransformPlayer() {
		Player player = PlayerTestHelper.createPlayer("playername");
		player.setRPClass("player");

		player.put("name", "player_1");
		UnderscoreConverter.transformNames(player);
		assertEquals("player_1", player.get("name"));	// no change expected
	}

	@Test
	public void testTransformItems() {
		RPObject obj = new RPObject();

		RPSlot slot = new RPSlot("slot1");
		obj.addSlot(slot);

		RPObject item1 = new RPObject();
		slot.add(item1);
		item1.put("name", "abc_123");
		item1.put("name2", "abc_123");

		UnderscoreConverter.transformNames(obj);

		assertEquals("abc 123", item1.get("name"));	// name should be transformed
		assertEquals("abc_123", item1.get("name2"));	// no change expected
	}
}
