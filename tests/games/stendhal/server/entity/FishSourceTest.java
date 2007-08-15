package games.stendhal.server.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.PlayerHelper;
import marauroa.common.Log4J;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPObject.ID;

import org.junit.BeforeClass;
import org.junit.Test;

public class FishSourceTest {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		MockStendhalRPRuleProcessor.get();
		MockStendlRPWorld.get();
	}

	@Test
	public void testOnUsed() {

		FishSource fs = new FishSource("somefish");
		Player player = new Player(new RPObject());
		player.setName("bob");
		PlayerHelper.addEmptySlots(player);
		fs.onUsed(player);
		assertEquals("You need a fishing rod for fishing.", player
				.get("private_text"));
		player.remove("private_text");
		StackableItem fishingRod = new StackableItem("fishing_rod", "", "",
				null);
		fishingRod.setQuantity(1);
		fishingRod.setID(new ID(2, "testzone"));
		player.getSlot("bag").add(fishingRod);
		assertTrue(player.isEquipped("fishing_rod"));
		fs.onUsed(player);
		assertEquals("You have started fishing.", player.get("private_text"));
		player.remove("private_text");
		fs.onUsed(player);
		assertFalse(player.has("private_text"));
		Player player2 = new Player(new RPObject());
		player2.setName("bob");
		PlayerHelper.addEmptySlots(player2);
		player2.getSlot("bag").add(fishingRod);
		fs.onUsed(player2);
		assertEquals("You have started fishing.", player2.get("private_text"));

	}

}
