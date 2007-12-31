// $Id$
package games.stendhal.server.actions.equip;

import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.PrivateTextMockingTestPlayer;

/**
 * Test cases for drop.
 *
 * @author hendrik
 */
public class EquipmentActionTest {
	private static final String ZONE_NAME = "0_semos_city";

	private static final String ZONE_CONTENT = "Level 0/semos/city.tmx";

	/**
	 * initialize the world.
	 *
	 * @throws Exception
	 */
	@BeforeClass
	public static void buildWorld() throws Exception {
		StendhalRPWorld world = StendhalRPWorld.get();
		world.addArea(ZONE_NAME, ZONE_CONTENT);
		Player.generateRPClass();
	}

	/**
	 * Create player and put it into the world.
	 * @param name
	 * @return
	 */
	private PrivateTextMockingTestPlayer createTestPlayer(String name) {
		PrivateTextMockingTestPlayer player = PlayerTestHelper.createPrivateTextMockingTestPlayer(name);

		player.setPosition(10, 5);
		StendhalRPWorld.get().getRPZone(ZONE_NAME).assignRPObjectID(player);
		StendhalRPWorld.get().getRPZone(ZONE_NAME).add(player);

		return player;
	}

	@Test
	public void testDropInvalidSourceSlot() {
		PrivateTextMockingTestPlayer player = createTestPlayer("george");

		RPAction drop = new RPAction();
		drop.put("type", "drop");
		drop.put("baseobject", player.getID().getObjectID());
		drop.put("baseslot", "nonExistingSlotXXXXXX");
		drop.put("baseitem", -1);

		EquipmentAction action = new EquipmentAction();
		action.onAction(player, drop);
		Assert.assertTrue("error message on invalid slot", player.getPrivateTextString().length()>0);
	}

	@Test
	public void testDrop() {
		PrivateTextMockingTestPlayer player = createTestPlayer("bob");

		RPAction drop = new RPAction();
		drop.put("type", "drop");
		drop.put("baseobject", player.getID().getObjectID());
		drop.put("baseslot", "bag");
		drop.put("x", player.getX());
		drop.put("y", player.getY() + 1);
		drop.put("quantity", "1");
		drop.put("baseitem", -1);

		EquipmentAction action = new EquipmentAction();
		action.onAction(player, drop);
		System.err.println(player.getPrivateTextString());

		Assert.assertTrue("error message on invalid item", player.getPrivateTextString().length()>0);
	}
}
