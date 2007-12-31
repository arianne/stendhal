package games.stendhal.server.entity.mapstuff.source;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import marauroa.common.game.RPObject.ID;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.PrivateTextMockingTestPlayer;

public class FishSourceTest {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		assertTrue(MockStendhalRPRuleProcessor.get() instanceof MockStendhalRPRuleProcessor);
		MockStendlRPWorld.get();
	}

	@Test
	public void testOnUsed() {
		FishSource fs = new FishSource("somefish");
		PrivateTextMockingTestPlayer player = PlayerTestHelper.createPrivateTextMockingTestPlayer("bob");

		fs.onUsed(player);
		assertEquals("You need a fishing rod for fishing.",
				player.getPrivateTextString());
		player.resetPrivateTextString();
		StackableItem fishingRod = new StackableItem("fishing_rod", "", "",
				null);
		fishingRod.setQuantity(1);
		fishingRod.setID(new ID(2, "testzone"));
		player.getSlot("bag").add(fishingRod);
		assertTrue(player.isEquipped("fishing_rod"));
		fs.onUsed(player);
		assertEquals("You have started fishing.", player.getPrivateTextString());
		player.resetPrivateTextString();
		fs.onUsed(player);
		assertFalse(player.has("private_text"));
		PrivateTextMockingTestPlayer player2 = PlayerTestHelper.createPrivateTextMockingTestPlayer("bob");

		player2.getSlot("bag").add(fishingRod);
		fs.onUsed(player2);
		assertEquals("You have started fishing.", player2.getPrivateTextString());
	}

}
