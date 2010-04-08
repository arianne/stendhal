package games.stendhal.server.maps.semos.tavern.market;

import games.stendhal.server.entity.item.Item;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

import utilities.RPClass.ItemTestHelper;

import static org.junit.Assert.*;

public class PrepareOfferHandlerTest {

	@Test
	public void testBuildTweetMessage() {
		PrepareOfferHandler handler = new PrepareOfferHandler();
		Item item = ItemTestHelper.createItem("axe");
		int price = 10;
		assertThat(handler.buildTweetMessage(item, price),is("New offer for axe at 10 money. "));
		item.put("atk",1);
		assertThat(handler.buildTweetMessage(item, price),is("New offer for axe at 10 money. Stats are (ATK: 1)."));
		item.put("rate",1);
		assertThat(handler.buildTweetMessage(item, price),is("New offer for axe at 10 money. Stats are (ATK: 1 RATE: 1)."));
		item.put("def",1);
		assertThat(handler.buildTweetMessage(item, price),is("New offer for axe at 10 money. Stats are (ATK: 1 DEF: 1 RATE: 1)."));
	}

}
