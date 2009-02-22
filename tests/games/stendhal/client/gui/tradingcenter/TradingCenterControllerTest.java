package games.stendhal.client.gui.tradingcenter;

import static org.junit.Assert.*;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.common.game.RPObject.ID;

import org.junit.Ignore;
import org.junit.Test;

public class TradingCenterControllerTest {

	@Ignore
	@Test
	public void testAddToZone() throws Exception {
		TradingCenterController controller = new TradingCenterController();
		RPObject object = new RPObject();
		ID arg0 = new ID(1,"zone");
		object.setID(arg0 );
		object.addSlot(new RPSlot("goods"));
		object.put("price", 5);
		assertFalse(controller.contains(object));
		controller.onAdded(object);
		assertTrue(controller.contains(object));
	}
}
