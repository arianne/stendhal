package games.stendhal.server.maps.quests.logic;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ItemCollectorDataTest {

	@Test
	public void testSubtractAmount() {
		int needed = 20;
		ItemCollectorData id = (ItemCollectorData) new ItemCollectorData().item("name").pieces(needed)
				.bySaying("prefix %s suffix");

		id.subtractAmount("5");

		assertEquals(15, id.getStillNeeded());
		assertEquals(needed, id.getRequiredAmount());
		assertEquals(5, id.getAlreadyBrought());
		assertEquals("prefix 15 names suffix", id.getAnswer());
	}
}
