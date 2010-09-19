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
package games.stendhal.server.entity.trade;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.trade.Offer;
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

import static org.hamcrest.CoreMatchers.is;

import static org.junit.Assert.assertThat;

public class OfferTest {
	
	@BeforeClass
	public static void setUp() {
		MockStendlRPWorld.get();
	}

	/**
	 * Tests for offer.
	 */
	@Test
	public void testOffer() throws Exception {
		Item item = SingletonRepository.getEntityManager().getItem("money");
		Integer price = Integer.valueOf(1);
		String offererName = "george";
		Player george = PlayerTestHelper.createPlayer(offererName);
		Offer o = new Offer(item, price, george);
		assertThat(o.getOfferer(), is(offererName));
		assertThat(o.getInt("price"), is(price.intValue()));
		assertThat((Item) o.getSlot("item").getFirst(), is(item));
		Offer offerFromRPObject = new Offer(o);
		assertThat(offerFromRPObject.getPrice(), is(price));
		assertThat(offerFromRPObject.getOfferer(), is(offererName));
		assertThat(offerFromRPObject.getItem(), is(item));
	}
}

