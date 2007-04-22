// $Id$
package games.stendhal.server.actions.equip;

import games.stendhal.client.entity.User;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

import org.junit.Ignore;
import org.junit.Test;


/**
 * Test cases for drop
 *
 * @author hendrik
 */
public class EquipmentActionTest {

	private class MockPlayer extends Player {

		public MockPlayer() {
			super(new RPObject());
		}
	}

	@Test
	@Ignore // not working yet
	public void testDropInvalidBaseItem() {
		
		Player player = new MockPlayer();
		RPAction drop = new RPAction();
		drop.put("type", "drop");
		drop.put("baseobject", player.getID().getObjectID());
		drop.put("baseslot", "bag");
		drop.put("x", (int)User.get().getX());
		drop.put("y", (int)User.get().getY() + 1);
		drop.put("quantity", "1");
		drop.put("baseitem", -1);
		
		
		EquipmentAction action = new EquipmentAction();
		action.onAction(player, drop);
	}
}
