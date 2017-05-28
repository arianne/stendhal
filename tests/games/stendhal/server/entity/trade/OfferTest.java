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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import utilities.PlayerTestHelper;

public class OfferTest {

	@BeforeClass
	public static void setUp() {
		MockStendlRPWorld.get();
	}

	@AfterClass
	public static void tearDownAfterClass() {
		PlayerTestHelper.removePlayer(OFFERER_NAME);
		MockStendlRPWorld.reset();
	}

	private static final String OFFERER_NAME = "george";

	/**
	 * Tests for offer.
	 *
	 * @throws Exception
	 */
	@Test
	public void testOffer() throws Exception {
		Item item = SingletonRepository.getEntityManager().getItem("money");
		Integer price = Integer.valueOf(1);
		Player offerer = PlayerTestHelper.createPlayer(OFFERER_NAME);
		Offer o = new Offer(item, price, offerer);
		assertThat(o.getOfferer(), is(OFFERER_NAME));
		assertThat(o.getInt("price"), is(price.intValue()));
		assertThat((Item) o.getSlot("item").getFirst(), is(item));
		Offer offerFromRPObject = new Offer(o);
		assertThat(offerFromRPObject.getPrice(), is(price));
		assertThat(offerFromRPObject.getOfferer(), is(OFFERER_NAME));
		assertThat(offerFromRPObject.getItem(), is(item));
	}

	/**
	 * Tests for equals of Offers
	 *
	 * @throws Exception
	 */
	@Test
	public void testEquals() throws Exception {
		Item item = SingletonRepository.getEntityManager().getItem("money");
		Integer price = Integer.valueOf(1);
		Player offerer = PlayerTestHelper.createPlayer(OFFERER_NAME);
		Offer o = new Offer(item, price, offerer);
		assertThat(o, is(o));
		assertThat(o.getOfferer(), is(OFFERER_NAME));
		assertThat(o.getInt("price"), is(price.intValue()));
		assertThat((Item) o.getSlot("item").getFirst(), is(item));
		Offer offerFromRPObject = new Offer(o);
		assertThat(offerFromRPObject.getPrice(), is(price));
		assertThat(offerFromRPObject.getOfferer(), is(OFFERER_NAME));
		assertThat(offerFromRPObject.getItem(), is(item));
		assertThat(offerFromRPObject, is(o));
		assertThat(o, is(offerFromRPObject));
	}

}
