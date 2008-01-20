package games.stendhal.server.core.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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
		assertEquals("abc def", UnderscoreConverter.transform("abc def"));
		assertEquals("abc def ghi", UnderscoreConverter.transform("abc_def ghi"));
		assertEquals("abc def ghi", UnderscoreConverter.transform("abc_def_ghi"));
		assertEquals("abc def ghi", UnderscoreConverter.transform("abc def ghi"));
	}

	@Test
	public void testTransformName() {
		RPObject obj = new RPObject();

		obj.put("name", "abc_123");
		obj.put("name2", "abc_123");

		UnderscoreConverter.transformNames(obj);

		assertEquals("name should be transformed", "abc 123", obj.get("name"));	
		assertEquals("no change expected", "abc_123", obj.get("name2"));	
	}

	@Test
	public void testTransformPlayer() {
		Player player = PlayerTestHelper.createPlayer("player_1");

		assertEquals("player_1", player.get("name"));
		assertEquals("player_1", player.getName());

		UnderscoreConverter.transformNames(player);

		assertEquals("no change expected", "player_1", player.get("name"));	
		assertEquals("player_1", player.getName());
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

		assertEquals("name should be transformed", "abc 123", item1.get("name"));	
		assertEquals("no change expected", "abc_123", item1.get("name2"));
	}

	@Test
	public void testTransformationOfTheKillingRecordSlot() {
		Player player = PlayerTestHelper.createPlayer("player");

		player.setSoloKill("name");
		player.setSharedKill("monster");
		player.setSoloKill("cave_rat");

		UnderscoreConverter.transformNames(player);

		assertTrue(player.hasKilled("name"));
		assertTrue(player.hasKilled("monster"));
		assertFalse(player.hasKilled("cave_rat"));
		assertTrue(player.hasKilled("cave rat"));
	}
}
