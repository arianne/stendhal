// $Id$
package games.stendhal.server.actions.equip;

import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test cases for drop
 *
 * @author hendrik
 */
public class EquipmentActionTest {
	private static final String ZONE_NAME = "0_semos_city";

	private static final String ZONE_CONTENT = "Level 0/semos/city.tmx";

	/**
	 * A mock player used for testing.
	 *
	 * @author hendrik
	 */
	private static class MockPlayer extends Player {
		private String privateText;

		/**
		 * Creates a new mock player and puts it into the world
		 */
		public MockPlayer() {
			super(new RPObject());
			setPosition(10, 5);
			StendhalRPWorld.get().getRPZone(ZONE_NAME).assignRPObjectID(this);
			StendhalRPWorld.get().getRPZone(ZONE_NAME).add(this);
		}

		@Override
		public void sendPrivateText(String text) {
			this.privateText = text;
		}

		/**
		 * gets the last private message
		 *
		 * @return last private message
		 */
		@Override
		public String getPrivateText() {
			return privateText;
		}
	}

	/**
	 * initialize the world
	 *
	 * @throws Exception
	 */
	@BeforeClass
	public static void buildWorld() throws Exception {
		StendhalRPWorld world = StendhalRPWorld.get();
		world.addArea(ZONE_NAME, ZONE_CONTENT);
		Player.generateRPClass();
	}

	@Test
	public void testDropInvalidSourceSlot() {

		MockPlayer player = new MockPlayer();
		player.setName("george");

		RPAction drop = new RPAction();
		drop.put("type", "drop");
		drop.put("baseobject", player.getID().getObjectID());
		drop.put("baseslot", "nonExistingSlotXXXXXX");
		drop.put("baseitem", -1);

		EquipmentAction action = new EquipmentAction();
		action.onAction(player, drop);
		Assert.assertTrue("error message on invalid slot", player
				.getPrivateText() != null);
	}

	@Test
	public void testDrop() {

		MockPlayer player = new MockPlayer();
		player.addSlot(new RPSlot("bag"));
		player.setName("bob");
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
		System.err.println(player.getPrivateText());

		Assert.assertTrue("error message on invalid item", player
				.getPrivateText() != null);
	}
}
