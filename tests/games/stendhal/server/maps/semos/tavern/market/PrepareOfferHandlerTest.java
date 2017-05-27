/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.semos.tavern.market;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import games.stendhal.server.entity.item.Item;
import utilities.RPClass.ItemTestHelper;

/**
 * Test class for {@link PrepareOfferHandler}
 *
 * @author madmetzger
 */
public class PrepareOfferHandlerTest {

	/**
	 * tests if the tweet message is built correct
	 */
	@Test
	public void testBuildTweetMessage() {
		PrepareOfferHandler handler = new PrepareOfferHandler();
		Item item = ItemTestHelper.createItem("axe");
		int price = 10;
		assertThat(handler.buildTweetMessage(item, 1, price),is("New offer for an axe at 10 money. "));
		item.put("atk",1);
		assertThat(handler.buildTweetMessage(item, 1, price),is("New offer for an axe at 10 money. Stats are (ATK: 1)."));
		item.put("rate",1);
		assertThat(handler.buildTweetMessage(item, 1, price),is("New offer for an axe at 10 money. Stats are (ATK: 1 RATE: 1)."));
		item.put("def",1);
		assertThat(handler.buildTweetMessage(item, 1, price),is("New offer for an axe at 10 money. Stats are (ATK: 1 DEF: 1 RATE: 1)."));
		item.put("description","Some weird description to check if stats are extracted right!");
		assertThat(handler.buildTweetMessage(item, 1, price),is("New offer for an axe at 10 money. Stats are (ATK: 1 DEF: 1 RATE: 1)."));
	}

}
