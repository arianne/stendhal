// $Id$
package games.stendhal.server.actions.equip;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.game.RPAction;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.PrivateTextMockingTestPlayer;
import utilities.RPClass.CreatureTestHelper;
import utilities.RPClass.PortalTestHelper;
import utilities.RPClass.SheepFoodTestHelper;
import utilities.RPClass.SheepTestHelper;

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
		
		MockStendlRPWorld.get();
		StendhalRPWorld world = SingletonRepository.getRPWorld();
		world.addArea(ZONE_NAME, ZONE_CONTENT);
	
	}

	/**
	 * Create player and put it into the world.
	 * @param name
	 * @return a Player where the  privateTexts are captured
	 */
	private PrivateTextMockingTestPlayer createTestPlayer(String name) {
		PrivateTextMockingTestPlayer player = PlayerTestHelper.createPrivateTextMockingTestPlayer(name);

		player.setPosition(10, 5);
		SingletonRepository.getRPWorld().getRPZone(ZONE_NAME).assignRPObjectID(player);
		SingletonRepository.getRPWorld().getRPZone(ZONE_NAME).add(player);

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
		Assert.assertEquals("Source nonExistingSlotXXXXXX does not exist", player.getPrivateTextString());
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
		Assert.assertEquals("There is no such item in the bag of bob", player.getPrivateTextString());
		}
}
