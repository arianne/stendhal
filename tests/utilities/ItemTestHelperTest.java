package utilities;

import static org.junit.Assert.assertEquals;
import games.stendhal.server.entity.item.Item;

public class ItemTestHelperTest {

	@org.junit.Test
	public void testcreateItem() throws Exception {
		ItemTestHelper.createItem();
		Item item = ItemTestHelper.createItem("blabla");
		assertEquals("blabla", item.getName());

	}
}
