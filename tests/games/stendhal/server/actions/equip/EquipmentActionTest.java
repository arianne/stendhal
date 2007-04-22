// $Id$
package games.stendhal.server.actions.equip;

import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;
import marauroa.common.game.RPObject;

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
			setX(10);
			setY(5);
			setID(new ID(1, "0_semos_city"));
		}
	}

	@Test
	public void testDropInvalidBaseItem() {
		
		Player player = new MockPlayer();
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
	}
}
