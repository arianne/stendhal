// $Id$
package games.stendhal.server.actions.equip;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.player.Player;

import java.io.IOException;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;


/**
 * Test cases for drop
 *
 * @author hendrik
 */
public class EquipmentActionTest {
	private static final String ZONE_NAME = "0_semos_city"; 

	/**
	 * A mock player used for testing.
	 *
	 * @author hendrik
	 */
	private static class MockPlayer extends Player {
		private String privateText = null;

		/**
		 * Creates a new mock player and puts it into the world
		 */
		public MockPlayer() {
			super(new RPObject());
			setX(10);
			setY(5);
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
		public String getPrivateText() {
			return privateText;
		}
	}

	/**
	 * initialize the world
	 *
	 * @throws SAXException on an invalid zones.xml configuration file
	 * @throws IOException on an input / output error
	 */
	@BeforeClass
	public static void buildWorld() throws SAXException, IOException {
		// TODO: Check what happens if this has already been done by some other test. 
		StendhalRPWorld world = StendhalRPWorld.get();
		world.addArea(ZONE_NAME);
		Player.generateRPClass();
	}

	@Test
	public void testDropInvalidSourceSlot() {

		MockPlayer player = new MockPlayer();

		RPAction drop = new RPAction();
		drop.put("type", "drop");
		drop.put("baseobject", player.getID().getObjectID());
		drop.put("baseslot", "nonExistingSlotXXXXXX");
		drop.put("baseitem", -1);
		
		EquipmentAction action = new EquipmentAction();
		action.onAction(player, drop);
		Assert.assertTrue("error message on invalid slot", player.getPrivateText() != null);
	}

}
