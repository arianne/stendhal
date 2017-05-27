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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.actions.CStatusAction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.engine.transformer.OfferTransformer;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import marauroa.common.game.RPObject.ID;
import marauroa.server.game.db.DatabaseFactory;
import utilities.PlayerTestHelper;

public class MarketTest {

	@BeforeClass
	public static void beforeClass() {
		Log4J.init();
		new DatabaseFactory().initializeDatabase();
		MockStendlRPWorld.get();
	}

	@AfterClass
	public static void afterClass() {
		MockStendlRPWorld.reset();
		MockStendhalRPRuleProcessor.get().clearPlayers();
	}

	@Before
	public void before() {
		CStatusAction.nameList.clear();
	}

	/**
	 * Tests for createOffer.
	 */
	@Test
	public void testCreateOffer() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market market = Market.createShop();
		zone.add(market);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item);
		assertEquals(bob, item.getContainer());
		Offer offer = market.createOffer(bob, item, 10, 1);

		assertTrue(market.contains(offer));
		assertFalse(offer.getItem().getContainer().equals(bob));
		assertTrue(offer.getItem().getContainer().equals(offer));
		assertNull(bob.getFirstEquipped("axe"));
		Offer object = (Offer) new OfferTransformer().transform(offer);
		assertEquals(offer, object);
		assertFalse(object.getItem().getContainer().equals(bob));
		assertTrue(object.getItem().getContainer().equals(offer));
	}

	/**
	 * Tests for successfullTrade.
	 */
	@Test
	public void testSuccessfullTrade() {
		Player george = PlayerTestHelper.createPlayer("george");
		PlayerTestHelper.registerPlayer(george);
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market market = Market.createShop();
		zone.add(market);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		StackableItem erniesMoney = (StackableItem) SingletonRepository
				.getEntityManager().getItem("money");
		Integer price = Integer.valueOf(10);
		erniesMoney.setQuantity(price);
		george.equipToInventoryOnly(item);
		Integer number = Integer.valueOf(1);
		Offer offer = market.createOffer(george, item, price, number);
		assertThat(offer.getItem(), is(item));
		assertThat(offer.getPrice(), is(price));
		assertThat(Boolean.valueOf(george.isEquipped(item.getName())),
				is(Boolean.FALSE));
		Player ernie = PlayerTestHelper.createPlayer("ernie");
		ernie.equipToInventoryOnly(erniesMoney);
		assertThat(ernie.isEquipped("money", price), is(Boolean.TRUE));
		market.acceptOffer(offer, ernie);
		assertThat(Boolean.valueOf(ernie.isEquipped("axe")), is(Boolean.TRUE));
		assertThat(ernie.isEquipped("money", price), is(Boolean.FALSE));
		assertThat(Boolean.valueOf(george.isEquipped("money")), is(Boolean.FALSE));
		assertThat(market.hasEarningsFor(george), is(Boolean.TRUE));
		market.fetchEarnings(george);
		assertThat(Boolean.valueOf(george.isEquipped("money", price.intValue())),
				is(Boolean.TRUE));
		assertThat(market.hasEarningsFor(george), is(Boolean.FALSE));
	}

	/**
	 * Tests that fetching earnings are calculated properly
	 */
	@Test
	public void testFetchEarnings() {
		Player george = PlayerTestHelper.createPlayer("george");
		PlayerTestHelper.registerPlayer(george);
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market market = Market.createShop();
		zone.add(market);

		Item item = SingletonRepository.getEntityManager().getItem("axe");
		george.equipToInventoryOnly(item);
		Offer offer = market.createOffer(george, item, 10, 1);

		item = SingletonRepository.getEntityManager().getItem("carrot");
		george.equipToInventoryOnly(item);
		Offer offer2 = market.createOffer(george, item, 11, 1);

		Player ernie = PlayerTestHelper.createPlayer("ernie");
		StackableItem money = (StackableItem) SingletonRepository.getEntityManager().getItem("money");
		money.setQuantity(21);
		ernie.equipToInventoryOnly(money);
		market.acceptOffer(offer, ernie);
		market.acceptOffer(offer2, ernie);

		assertThat(market.hasEarningsFor(george), is(Boolean.TRUE));
		market.fetchEarnings(george);
		assertThat(market.hasEarningsFor(george), is(Boolean.FALSE));
		// Total earnings should be 21
		int total = 0;
		for (Item gMoney : george.getAllEquipped("money")) {
			total += ((StackableItem) gMoney).getQuantity();
		}
		assertThat(total, is(21));

		// Check that the earnings have been removed
		total = 0;
		assertThat(market.fetchEarnings(george).size(), is(0));
		for (Item gMoney : george.getAllEquipped("money")) {
			total += ((StackableItem) gMoney).getQuantity();
		}
		assertThat(total, is(21));
	}


	/**
	 * Tests for createNonExistingOffer.
	 */
	@Test
	public void testCreateNonExistingOffer() {
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market market = Market.createShop();
		zone.add(market);
		Player george = PlayerTestHelper.createPlayer("george");
		Offer offer = market.createOffer(george, null, 42, 1);
		assertNull("Creating offers for non existing items should fail", offer);
	}

	/**
	 * Tests for createOfferForBoundItem.
	 */
	@Test
	public void testCreateOfferForBoundItem() {
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market market = Market.createShop();
		zone.add(market);
		Player george = PlayerTestHelper.createPlayer("george");
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		item.setBoundTo("george");
		george.equipToInventoryOnly(item);

		Offer offer = market.createOffer(george, item, 42, 1);
		assertNull("Creating offers for non bound items should fail", offer);
		assertThat(george.isEquipped("axe"), is(Boolean.TRUE));
	}

	/**
	 * Tests for nonExistingOffer.
	 */
	@Test
	public void testNonExistingOffer() {
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market market = Market.createShop();
		zone.add(market);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		StackableItem erniesMoney = (StackableItem) SingletonRepository
				.getEntityManager().getItem("money");
		Integer price = Integer.valueOf(10);
		erniesMoney.setQuantity(price);
		Player george = PlayerTestHelper.createPlayer("george");
		Offer offer = new Offer(item, price, george);
		offer.setID(new ID(1, "shop"));

		Player ernie = PlayerTestHelper.createPlayer("ernie");
		ernie.equipToInventoryOnly(erniesMoney);
		assertThat(ernie.isEquipped("money", price), is(Boolean.TRUE));
		market.acceptOffer(offer, ernie);
		assertThat(Boolean.valueOf(ernie.isEquipped("axe")), is(Boolean.FALSE));
		assertThat(ernie.isEquipped("money", price), is(Boolean.TRUE));
	}


	/**
	 * Tests for poorBuyer.
	 */
	@Test
	public void testPoorBuyer() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market market = Market.createShop();
		zone.add(market);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		StackableItem erniesMoney = (StackableItem) SingletonRepository
				.getEntityManager().getItem("money");
		Integer price = Integer.valueOf(10);
		Integer tooFewMoney = price - 1;
		erniesMoney.setQuantity(tooFewMoney);
		bob.equipToInventoryOnly(item);
		Offer offer = market.createOffer(bob, item, price, Integer.valueOf(1));

		Player ernie = PlayerTestHelper.createPlayer("ernie");
		ernie.equipToInventoryOnly(erniesMoney);

		assertThat(ernie.isEquipped("money", price), is(Boolean.FALSE));
		market.acceptOffer(offer, ernie);
		assertThat(Boolean.valueOf(ernie.isEquipped("axe")), is(Boolean.FALSE));
		assertThat(ernie.isEquipped("money", tooFewMoney), is(Boolean.TRUE));
		assertThat(Boolean.valueOf(bob.isEquipped("money")), is(Boolean.FALSE));
		market.fetchEarnings(bob);
		assertThat(Boolean.valueOf(bob.isEquipped("money", tooFewMoney.intValue())),
				is(Boolean.FALSE));
		assertThat(Boolean.valueOf(bob.isEquipped("money", price.intValue())),
				is(Boolean.FALSE));
	}

	/**
	 * Check that accepting a free offer succeeds.
	 */
	@Test
	public void testAcceptFreeOffer() {
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market market = Market.createShop();
		zone.add(market);
		Player george = PlayerTestHelper.createPlayer("george");
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		george.equipToInventoryOnly(item);
		Offer offer = market.createOffer(george, item, 0, 1);

		Player ernie = PlayerTestHelper.createPlayer("ernie");
		market.acceptOffer(offer, ernie);
		assertThat(ernie.isEquipped("axe"), is(Boolean.TRUE));
	}

	@Test
	public void testMultipleItems() {
		Player george = PlayerTestHelper.createPlayer("george");
		PlayerTestHelper.registerPlayer(george);
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market market = Market.createShop();
		zone.add(market);
		Item item = SingletonRepository.getEntityManager().getItem("greater potion");
		((StackableItem) item).setQuantity(6);
		StackableItem erniesMoney = (StackableItem) SingletonRepository
				.getEntityManager().getItem("money");
		Integer price = Integer.valueOf(10);
		erniesMoney.setQuantity(price);
		george.equipToInventoryOnly(item);
		Integer number = Integer.valueOf(5);
		Offer offer = market.createOffer(george, item, price, number);
		assertThat(offer.getItem().getName(), is("greater potion"));
		assertThat(((StackableItem) offer.getItem()).getQuantity(), is(5));
		assertThat(offer.getPrice(), is(price));
		assertThat(Boolean.valueOf(george.isEquipped(item.getName(), 1)),
				is(Boolean.TRUE));
		assertThat(Boolean.valueOf(george.isEquipped(item.getName(), 2)),
				is(Boolean.FALSE));
		Player ernie = PlayerTestHelper.createPlayer("ernie");
		ernie.equipToInventoryOnly(erniesMoney);
		assertThat(ernie.isEquipped("money", price), is(Boolean.TRUE));
		market.acceptOffer(offer, ernie);
		assertThat(Boolean.valueOf(ernie.isEquipped("greater potion", 5)), is(Boolean.TRUE));
		assertThat(ernie.isEquipped("money", price), is(Boolean.FALSE));
		assertThat(Boolean.valueOf(george.isEquipped("money")), is(Boolean.FALSE));
		market.fetchEarnings(george);
		assertThat(Boolean.valueOf(george.isEquipped("money", price.intValue())),
				is(Boolean.TRUE));
	}

	/**
	 * Test creating with stackable items that have been dropped
	 */
	@Test
	public void testCheatWithStacks() {
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market market = Market.createShop();
		zone.add(market);
		Player george = PlayerTestHelper.createPlayer("george");
		StackableItem item = (StackableItem) SingletonRepository.getEntityManager().getItem("meat");
		// ensure the item gets an id
		george.equipToInventoryOnly(item);
		george.drop(item);

		Offer offer = market.createOffer(george, item, 42, 1);
		assertNull("Creating offers for items that are not with the player should fail", offer);
	}

	/**
	 * Tests for expireOffer.
	 */
	@Test
	public void testExpireOffer() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market market = Market.createShop();
		zone.add(market);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item);

		Offer offer = market.createOffer(bob, item, 10, 1);

		market.expireOffer(offer);
		assertFalse(market.contains(offer));
		assertTrue(market.getExpiredOffers().contains(offer));
	}

	/**
	 * Tests for removeExpiredOffer.
	 */
	@Test
	public void testRemoveExpiredOffer() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market market = Market.createShop();
		zone.add(market);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item);

		Offer offer = market.createOffer(bob, item, 10, 1);

		market.expireOffer(offer);
		market.removeExpiredOffer(offer);
		assertFalse(market.contains(offer));
		assertFalse(market.getExpiredOffers().contains(offer));
	}

	/**
	 * Tests for removeOffer.
	 */
	@Test
	public void testRemoveOffer() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market market = Market.createShop();
		zone.add(market);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item);

		Offer offer = market.createOffer(bob, item, 10, 1);

		market.removeOffer(offer, bob);
		assertFalse(market.contains(offer));
		assertFalse(market.getExpiredOffers().contains(offer));
		assertTrue(bob.getFirstEquipped("axe") != null);
	}

	// returning the item to player from an offer that has expired
	/**
	 * Tests for removeExpiredOffer2.
	 */
	@Test
	public void testRemoveExpiredOffer2() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market market = Market.createShop();
		zone.add(market);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item);

		Offer offer = market.createOffer(bob, item, 10, 1);

		market.expireOffer(offer);
		market.removeOffer(offer, bob);

		assertFalse(market.contains(offer));
		assertFalse(market.getExpiredOffers().contains(offer));
		assertTrue(bob.getFirstEquipped("axe") != null);
	}

	/**
	 * Tests for removeNonExistingOffer.
	 */
	@Test
	public void testRemoveNonExistingOffer() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market market = Market.createShop();
		zone.add(market);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item);

		// make an offer to the shop and make it disappear
		Offer offer = market.createOffer(bob, item, 10, 1);
		market.expireOffer(offer);
		market.removeExpiredOffer(offer);

		market.removeOffer(offer, bob);
		bob.drop(item);

		assertFalse(market.contains(offer));
		assertFalse(market.getExpiredOffers().contains(offer));
		assertNull(bob.getFirstEquipped("axe"));
	}

	/**
	 * Tests for getOffersOlderThan.
	 */
	@Test
	public void testGetOffersOlderThan() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market market = Market.createShop();
		zone.add(market);

		assertTrue(market.getOffersOlderThan(10000).size() == 0);

		Item item = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item);
		Offer offer = market.createOffer(bob, item, 10, 1);
		offer.put("timestamp", "0");

		Item item2 = SingletonRepository.getEntityManager().getItem("cheese");
		bob.equipToInventoryOnly(item2);
		Offer offer2 = market.createOffer(bob, item2, 10, 1);

		// large numbers on purpose trying to overflow int
		List<Offer> offersOlderThan = market.getOffersOlderThan(1000000000);
		assertTrue(offersOlderThan.contains(offer));
		assertThat(offersOlderThan.contains(offer2), is(Boolean.FALSE));
	}

	/**
	 * Tests for getExpiredOffersOlderThan.
	 */
	@Test
	public void testGetExpiredOffersOlderThan() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market market = Market.createShop();
		zone.add(market);

		assertTrue(market.getExpiredOffersOlderThan(10000).size() == 0);

		Item item = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item);
		Offer offer = market.createOffer(bob, item, 10, 1);
		offer.put("timestamp", "0");
		market.expireOffer(offer);

		Item item2 = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item2);
		Offer offer2 = market.createOffer(bob, item2, 11, 1);
		market.expireOffer(offer2);

		// large numbers on purpose trying to overflow int
		assertTrue(market.getExpiredOffersOlderThan(1000000000).contains(offer));
		assertFalse(market.getExpiredOffersOlderThan(1000000000).contains(offer2));
	}

	/**
	 * Tests that getEarningsOlderThan works as intended
	 */
	@Test
	public void testGetEarningsOlderThan() {
		Player george = PlayerTestHelper.createPlayer("george");
		PlayerTestHelper.registerPlayer(george);
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market market = Market.createShop();
		zone.add(market);

		Item item = SingletonRepository.getEntityManager().getItem("axe");
		george.equipToInventoryOnly(item);
		Offer offer = market.createOffer(george, item, 10, 1);

		item = SingletonRepository.getEntityManager().getItem("carrot");
		george.equipToInventoryOnly(item);
		Offer offer2 = market.createOffer(george, item, 11, 1);

		Player ernie = PlayerTestHelper.createPlayer("ernie");
		StackableItem money = (StackableItem) SingletonRepository.getEntityManager().getItem("money");
		money.setQuantity(21);
		ernie.equipToInventoryOnly(money);

		assertThat(market.getEarningsOlderThan(0).size(), is(0));
		market.acceptOffer(offer, ernie);
		assertThat(market.getEarningsOlderThan(-10).size(), is(1));
		Earning earning1 = market.getEarningsOlderThan(-1).get(0);
		market.acceptOffer(offer2, ernie);
		assertThat(market.getEarningsOlderThan(-1).size(), is(2));
		Earning earning2 = market.getEarningsOlderThan(-1).get(1);
		earning1.put("timestamp", "0");

		// large numbers on purpose trying to overflow int
		assertTrue(market.getEarningsOlderThan(1000000000).contains(earning1));
		assertFalse(market.getEarningsOlderThan(1000000000).contains(earning2));
	}


	/**
	 * Tests for prolongActive.
	 */
	@Test
	public void testProlongActive() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market market = Market.createShop();
		zone.add(market);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item);

		Offer offer = market.createOffer(bob, item, 10, 1);
		offer.put("timestamp", "0");

		market.prolongOffer(offer);
		assertTrue(market.getOffersOlderThan(1000).size() == 0);
		assertTrue(market.countOffersOfPlayer(bob) == 1);
	}

	/**
	 * Tests for prolongExpired.
	 */
	@Test
	public void testProlongExpired() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market market = Market.createShop();
		zone.add(market);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item);

		Offer offer = market.createOffer(bob, item, 10, 1);
		offer.put("timestamp", "0");
		market.expireOffer(offer);

		market.prolongOffer(offer);
		assertTrue(market.getOffersOlderThan(1000).size() == 0);
		assertTrue(market.countOffersOfPlayer(bob) == 1);
		assertTrue(market.getExpiredOffers().size() == 0);
	}

	/**
	 * Tests for prolongCompletelyExpired.
	 */
	@Test
	public void testProlongCompletelyExpired() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market market = Market.createShop();
		zone.add(market);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item);

		Offer offer = market.createOffer(bob, item, 10, 1);
		offer.put("timestamp", "0");
		market.expireOffer(offer);
		market.removeExpiredOffer(offer);

		market.prolongOffer(offer);

		assertTrue(market.countOffersOfPlayer(bob) == 0);
		assertTrue(market.getExpiredOffers().size() == 0);
	}

	@Test
	public void testExpireEarnings() {
		Player george = PlayerTestHelper.createPlayer("george");
		PlayerTestHelper.registerPlayer(george);
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market market = Market.createShop();
		zone.add(market);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		StackableItem erniesMoney = (StackableItem) SingletonRepository
				.getEntityManager().getItem("money");
		Integer price = Integer.valueOf(10);
		erniesMoney.setQuantity(price);
		george.equipToInventoryOnly(item);
		Integer number = Integer.valueOf(1);
		Offer offer = market.createOffer(george, item, price, number);
		assertThat(offer.getItem(), is(item));
		assertThat(offer.getPrice(), is(price));
		assertThat(Boolean.valueOf(george.isEquipped(item.getName())),
				is(Boolean.FALSE));
		Player ernie = PlayerTestHelper.createPlayer("ernie");
		ernie.equipToInventoryOnly(erniesMoney);
		assertThat(ernie.isEquipped("money", price), is(Boolean.TRUE));
		market.acceptOffer(offer, ernie);
		assertThat(Boolean.valueOf(ernie.isEquipped("axe")), is(Boolean.TRUE));
		assertThat(ernie.isEquipped("money", price), is(Boolean.FALSE));
		assertThat(Boolean.valueOf(george.isEquipped("money")), is(Boolean.FALSE));
		List<Earning> earningsOlderThan = market.getEarningsOlderThan(-1);
		assertThat(Boolean.valueOf(earningsOlderThan.isEmpty()), is(Boolean.FALSE));
		market.removeEarnings(earningsOlderThan);
		assertThat(Boolean.valueOf(market.getEarningsOlderThan(-1).isEmpty()), is(Boolean.TRUE));
	}

	/**
	 * Tests that the trading score of 2 different players gets
	 * incremented when they have normal, unequal CIDs.
	 */
	@Test
	public void testIncreaseScore() {
		Player george = PlayerTestHelper.createPlayer("george");
		PlayerTestHelper.registerPlayer(george);
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market market = Market.createShop();
		zone.add(market);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		StackableItem erniesMoney = (StackableItem) SingletonRepository
				.getEntityManager().getItem("money");
		Integer price = Integer.valueOf(10);
		erniesMoney.setQuantity(price);
		george.equipToInventoryOnly(item);

		// ensure different CIDs
		CStatusAction.nameList.put("george", "georgescid");
		CStatusAction.nameList.put("ernie", "erniescid");

		Offer offer = market.createOffer(george, item, price, 1);
		Player ernie = PlayerTestHelper.createPlayer("ernie");
		ernie.equipToInventoryOnly(erniesMoney);

		assertThat(ernie.getTradescore(), is(0));
		assertThat(george.getTradescore(), is(0));

		market.acceptOffer(offer, ernie);

		assertThat(ernie.getTradescore(), is(1));
		assertThat(george.getTradescore(), is(0));

		market.fetchEarnings(george);

		assertThat(ernie.getTradescore(), is(1));
		assertThat(george.getTradescore(), is(1));
	}

	/**
	 * Tests that the trading score of 2 different players does not
	 * get incremented when seller does not have a CID.
	 */
	@Test
	public void testIncreaseScoreNoSellerCID() {
		Player george = PlayerTestHelper.createPlayer("george");
		PlayerTestHelper.registerPlayer(george);
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market market = Market.createShop();
		zone.add(market);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		StackableItem erniesMoney = (StackableItem) SingletonRepository
				.getEntityManager().getItem("money");
		Integer price = Integer.valueOf(10);
		erniesMoney.setQuantity(price);
		george.equipToInventoryOnly(item);

		CStatusAction.nameList.put("ernie", "erniescid");

		Offer offer = market.createOffer(george, item, price, 1);
		Player ernie = PlayerTestHelper.createPlayer("ernie");
		ernie.equipToInventoryOnly(erniesMoney);

		assertThat(ernie.getTradescore(), is(0));
		assertThat(george.getTradescore(), is(0));

		market.acceptOffer(offer, ernie);

		assertThat(ernie.getTradescore(), is(0));
		assertThat(george.getTradescore(), is(0));

		market.fetchEarnings(george);

		assertThat(ernie.getTradescore(), is(0));
		assertThat(george.getTradescore(), is(0));
	}

	/**
	 * Tests that the trading score of 2 different players does not
	 * get incremented when the buyer does not have a CID.
	 */
	@Test
	public void testIncreaseScoreNoBuyerCID() {
		Player george = PlayerTestHelper.createPlayer("george");
		PlayerTestHelper.registerPlayer(george);
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market market = Market.createShop();
		zone.add(market);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		StackableItem erniesMoney = (StackableItem) SingletonRepository
				.getEntityManager().getItem("money");
		Integer price = Integer.valueOf(10);
		erniesMoney.setQuantity(price);
		george.equipToInventoryOnly(item);

		CStatusAction.nameList.put("george", "georgescid");

		Offer offer = market.createOffer(george, item, price, 1);
		Player ernie = PlayerTestHelper.createPlayer("ernie");
		ernie.equipToInventoryOnly(erniesMoney);

		assertThat(ernie.getTradescore(), is(0));
		assertThat(george.getTradescore(), is(0));

		market.acceptOffer(offer, ernie);

		assertThat(ernie.getTradescore(), is(0));
		assertThat(george.getTradescore(), is(0));

		market.fetchEarnings(george);

		assertThat(ernie.getTradescore(), is(0));
		assertThat(george.getTradescore(), is(0));
	}

	/**
	 * Tests that the trading score does not change when a player
	 * buys from himself.
	 */
	@Test
	public void testIncreaseScoreSamePlayer() {
		Player george = PlayerTestHelper.createPlayer("george");
		PlayerTestHelper.registerPlayer(george);
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market market = Market.createShop();
		zone.add(market);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		StackableItem money = (StackableItem) SingletonRepository
				.getEntityManager().getItem("money");
		Integer price = Integer.valueOf(10);
		money.setQuantity(price);
		george.equipToInventoryOnly(item);
		george.equipToInventoryOnly(money);

		CStatusAction.nameList.put("george", "georgescid");

		Offer offer = market.createOffer(george, item, price, Integer.valueOf(1));

		assertThat(george.getTradescore(), is(0));

		// switch cid in between
		CStatusAction.nameList.put("george", "georgesfakecid");
		market.acceptOffer(offer, george);

		assertThat(george.getTradescore(), is(0));

		market.fetchEarnings(george);

		assertThat(george.getTradescore(), is(0));
	}
}
