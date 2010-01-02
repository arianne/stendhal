package games.stendhal.server.entity.trade;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import games.stendhal.server.actions.CIDSubmitAction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class TradeTest {

	@BeforeClass
	public static void beforeClass() {
		MockStendlRPWorld.get();
	}

	@AfterClass
	public static void afterClass() {
		MockStendlRPWorld.reset();
	}
	
	@Before
	public void before() {
		CIDSubmitAction.nameList.clear();
	}
	
	/**
	 * Tests for createOffer.
	 */
	@Test
	public void testCreateOffer() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market edeka = Market.createShop();
		zone.add(edeka);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item);
		
		Offer offer = edeka.createOffer(bob, item, 10, 1);
		
		assertTrue(edeka.getOffers().contains(offer));
		assertNull(bob.getFirstEquipped("axe"));
	}

	/**
	 * Tests for successfullTrade.
	 */
	@Test
	public void testSuccessfullTrade() {
		Player george = PlayerTestHelper.createPlayer("george");
		PlayerTestHelper.registerPlayer(george);
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market edeka = Market.createShop();
		zone.add(edeka);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		StackableItem erniesMoney = (StackableItem) SingletonRepository
				.getEntityManager().getItem("money");
		Integer price = Integer.valueOf(10);
		erniesMoney.setQuantity(price);
		george.equipToInventoryOnly(item);
		Integer number = Integer.valueOf(1);
		Offer offer = edeka.createOffer(george, item, price, number);
		assertThat(offer.getItem(), is(item));
		assertThat(offer.getPrice(), is(price));
		assertThat(Boolean.valueOf(george.isEquipped(item.getName())),
				is(Boolean.FALSE));
		Player ernie = PlayerTestHelper.createPlayer("ernie");
		ernie.equipToInventoryOnly(erniesMoney);
		assertThat(ernie.isEquipped("money", price), is(Boolean.TRUE));
		edeka.acceptOffer(offer, ernie);
		assertThat(Boolean.valueOf(ernie.isEquipped("axe")), is(Boolean.TRUE));
		assertThat(ernie.isEquipped("money", price), is(Boolean.FALSE));
		assertThat(Boolean.valueOf(george.isEquipped("money")), is(Boolean.FALSE));
		edeka.fetchEarnings(george);
		assertThat(Boolean.valueOf(george.isEquipped("money", price.intValue())),
				is(Boolean.TRUE));
	}
	
	/**
	 * Tests that fetching earnings are calculated properly
	 */
	@Test
	public void testFetchEarnings() {
		Player george = PlayerTestHelper.createPlayer("george");
		PlayerTestHelper.registerPlayer(george);
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market edeka = Market.createShop();
		zone.add(edeka);
		
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		george.equipToInventoryOnly(item);
		Offer offer = edeka.createOffer(george, item, 10, 1);
		
		item = SingletonRepository.getEntityManager().getItem("carrot");
		george.equipToInventoryOnly(item);
		Offer offer2 = edeka.createOffer(george, item, 11, 1);
		
		Player ernie = PlayerTestHelper.createPlayer("ernie");
		StackableItem money = (StackableItem) SingletonRepository.getEntityManager().getItem("money");
		money.setQuantity(21);
		ernie.equipToInventoryOnly(money);
		edeka.acceptOffer(offer, ernie);
		edeka.acceptOffer(offer2, ernie);
		
		edeka.fetchEarnings(george);
		// Total earnings should be 21
		int total = 0;
		for (Item gMoney : george.getAllEquipped("money")) {
			total += ((StackableItem) gMoney).getQuantity();
		}
		assertThat(total, is(21));
		
		// Check that the earnings have been removed
		total = 0;
		assertThat(edeka.fetchEarnings(george).size(), is(0));
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
		Market edeka = Market.createShop();
		zone.add(edeka);
		Player george = PlayerTestHelper.createPlayer("george");
		Offer offer = edeka.createOffer(george, null, 42, 1);
		assertNull("Creating offers for non existing items should fail", offer);
	}
	
	/**
	 * Tests for createOfferForBoundItem.
	 */
	@Test
	public void testCreateOfferForBoundItem() {
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market edeka = Market.createShop();
		zone.add(edeka);
		Player george = PlayerTestHelper.createPlayer("george");
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		item.setBoundTo("george");
		george.equipToInventoryOnly(item);
		
		Offer offer = edeka.createOffer(george, item, 42, 1);
		assertNull("Creating offers for non bound items should fail", offer);
		assertThat(george.isEquipped("axe"), is(Boolean.TRUE));
	}

	/**
	 * Tests for nonExistingOffer.
	 */
	@Test
	public void testNonExistingOffer() {
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market edeka = Market.createShop();
		zone.add(edeka);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		StackableItem erniesMoney = (StackableItem) SingletonRepository
				.getEntityManager().getItem("money");
		Integer price = Integer.valueOf(10);
		erniesMoney.setQuantity(price);
		Player george = PlayerTestHelper.createPlayer("george");
		Offer offer = new Offer(item, price, george);

		Player ernie = PlayerTestHelper.createPlayer("ernie");
		ernie.equipToInventoryOnly(erniesMoney);
		assertThat(ernie.isEquipped("money", price), is(Boolean.TRUE));
		edeka.acceptOffer(offer, ernie);
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
		Market edeka = Market.createShop();
		zone.add(edeka);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		StackableItem erniesMoney = (StackableItem) SingletonRepository
				.getEntityManager().getItem("money");
		Integer price = Integer.valueOf(10);
		Integer tooFewMoney = price - 1;
		erniesMoney.setQuantity(tooFewMoney);
		bob.equipToInventoryOnly(item);
		Offer offer = edeka.createOffer(bob, item, price, Integer.valueOf(1));

		Player ernie = PlayerTestHelper.createPlayer("ernie");
		ernie.equipToInventoryOnly(erniesMoney);
		
		assertThat(ernie.isEquipped("money", price), is(Boolean.FALSE));
		edeka.acceptOffer(offer, ernie);
		assertThat(Boolean.valueOf(ernie.isEquipped("axe")), is(Boolean.FALSE));
		assertThat(ernie.isEquipped("money", tooFewMoney), is(Boolean.TRUE));
		assertThat(Boolean.valueOf(bob.isEquipped("money")), is(Boolean.FALSE));
		edeka.fetchEarnings(bob);
		assertThat(Boolean.valueOf(bob.isEquipped("money", tooFewMoney.intValue())),
				is(Boolean.FALSE));
		assertThat(Boolean.valueOf(bob.isEquipped("money", price.intValue())),
				is(Boolean.FALSE));
	}
	
	@Test
	public void testMultipleItems() {
		Player george = PlayerTestHelper.createPlayer("george");
		PlayerTestHelper.registerPlayer(george);
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market edeka = Market.createShop();
		zone.add(edeka);
		Item item = SingletonRepository.getEntityManager().getItem("greater potion");
		((StackableItem) item).setQuantity(6);
		StackableItem erniesMoney = (StackableItem) SingletonRepository
				.getEntityManager().getItem("money");
		Integer price = Integer.valueOf(10);
		erniesMoney.setQuantity(price);
		george.equipToInventoryOnly(item);
		Integer number = Integer.valueOf(5);
		Offer offer = edeka.createOffer(george, item, price, number);
		assertThat(offer.getItem(), is(item));
		assertThat(offer.getPrice(), is(price));
		assertThat(Boolean.valueOf(george.isEquipped(item.getName(), 1)),
				is(Boolean.TRUE));
		Player ernie = PlayerTestHelper.createPlayer("ernie");
		ernie.equipToInventoryOnly(erniesMoney);
		assertThat(ernie.isEquipped("money", price), is(Boolean.TRUE));
		edeka.acceptOffer(offer, ernie);
		assertThat(Boolean.valueOf(ernie.isEquipped("greater potion", 5)), is(Boolean.TRUE));
		assertThat(ernie.isEquipped("money", price), is(Boolean.FALSE));
		assertThat(Boolean.valueOf(george.isEquipped("money")), is(Boolean.FALSE));
		edeka.fetchEarnings(george);
		assertThat(Boolean.valueOf(george.isEquipped("money", price.intValue())),
				is(Boolean.TRUE));
	}

	/**
	 * Tests for expireOffer.
	 */
	@Test
	public void testExpireOffer() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market edeka = Market.createShop();
		zone.add(edeka);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item);
		
		Offer offer = edeka.createOffer(bob, item, 10, 1);
		
		edeka.expireOffer(offer);
		assertFalse(edeka.getOffers().contains(offer));
		assertTrue(edeka.getExpiredOffers().contains(offer));
	}
	
	/**
	 * Tests for removeExpiredOffer.
	 */
	@Test
	public void testRemoveExpiredOffer() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market edeka = Market.createShop();
		zone.add(edeka);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item);
		
		Offer offer = edeka.createOffer(bob, item, 10, 1);
		
		edeka.expireOffer(offer);
		edeka.removeExpiredOffer(offer);
		assertFalse(edeka.getOffers().contains(offer));
		assertFalse(edeka.getExpiredOffers().contains(offer));
	}
	
	/**
	 * Tests for removeOffer.
	 */
	@Test
	public void testRemoveOffer() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market edeka = Market.createShop();
		zone.add(edeka);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item);
		
		Offer offer = edeka.createOffer(bob, item, 10, 1);
		
		edeka.removeOffer(offer, bob);
		assertFalse(edeka.getOffers().contains(offer));
		assertFalse(edeka.getExpiredOffers().contains(offer));
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
		Market edeka = Market.createShop();
		zone.add(edeka);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item);
		
		Offer offer = edeka.createOffer(bob, item, 10, 1);
		
		edeka.expireOffer(offer);
		edeka.removeOffer(offer, bob);
		
		assertFalse(edeka.getOffers().contains(offer));
		assertFalse(edeka.getExpiredOffers().contains(offer));
		assertTrue(bob.getFirstEquipped("axe") != null);
	}
	
	/**
	 * Tests for removeNonExistingOffer.
	 */
	@Test
	public void testRemoveNonExistingOffer() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market edeka = Market.createShop();
		zone.add(edeka);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item);
		
		// make an offer to the shop and make it disappear
		Offer offer = edeka.createOffer(bob, item, 10, 1);
		edeka.expireOffer(offer);
		edeka.removeExpiredOffer(offer);
		
		edeka.removeOffer(offer, bob);
		bob.drop(item);
		
		assertFalse(edeka.getOffers().contains(offer));
		assertFalse(edeka.getExpiredOffers().contains(offer));
		assertNull(bob.getFirstEquipped("axe"));
	}
	
	/**
	 * Tests for getOffersOlderThan.
	 */
	@Test
	public void testGetOffersOlderThan() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market edeka = Market.createShop();
		zone.add(edeka);
		
		assertTrue(edeka.getOffersOlderThan(10000).size() == 0);
		
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item);
		Offer offer = edeka.createOffer(bob, item, 10, 1);
		offer.put("timestamp", "0");
		
		Item item2 = SingletonRepository.getEntityManager().getItem("cheese");
		bob.equipToInventoryOnly(item2);
		Offer offer2 = edeka.createOffer(bob, item2, 10, 1);
		
		// large numbers on purpose trying to overflow int
		assertTrue(edeka.getOffersOlderThan(1000000000).contains(offer));
		assertFalse(edeka.getOffersOlderThan(1000000000).contains(offer2));
	}
	
	/**
	 * Tests for getExpiredOffersOlderThan.
	 */
	@Test
	public void testGetExpiredOffersOlderThan() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market edeka = Market.createShop();
		zone.add(edeka);
		
		assertTrue(edeka.getExpiredOffersOlderThan(10000).size() == 0);
		
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item);
		Offer offer = edeka.createOffer(bob, item, 10, 1);
		offer.put("timestamp", "0");
		edeka.expireOffer(offer);
		
		Item item2 = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item2);
		Offer offer2 = edeka.createOffer(bob, item2, 11, 1);
		edeka.expireOffer(offer2);
		
		// large numbers on purpose trying to overflow int
		assertTrue(edeka.getExpiredOffersOlderThan(1000000000).contains(offer));
		assertFalse(edeka.getExpiredOffersOlderThan(1000000000).contains(offer2));
	}
	
	/**
	 * Tests that getEarningsOlderThan works as intended
	 */
	@Test
	public void testGetEarningsOlderThan() {
		Player george = PlayerTestHelper.createPlayer("george");
		PlayerTestHelper.registerPlayer(george);
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market edeka = Market.createShop();
		zone.add(edeka);
		
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		george.equipToInventoryOnly(item);
		Offer offer = edeka.createOffer(george, item, 10, 1);
		
		item = SingletonRepository.getEntityManager().getItem("carrot");
		george.equipToInventoryOnly(item);
		Offer offer2 = edeka.createOffer(george, item, 11, 1);
		
		Player ernie = PlayerTestHelper.createPlayer("ernie");
		StackableItem money = (StackableItem) SingletonRepository.getEntityManager().getItem("money");
		money.setQuantity(21);
		ernie.equipToInventoryOnly(money);
		
		assertThat(edeka.getEarningsOlderThan(0).size(), is(0));
		edeka.acceptOffer(offer, ernie);
		assertThat(edeka.getEarningsOlderThan(-10).size(), is(1));
		Earning earning1 = edeka.getEarningsOlderThan(-1).get(0);
		edeka.acceptOffer(offer2, ernie);
		assertThat(edeka.getEarningsOlderThan(-1).size(), is(2));
		Earning earning2 = edeka.getEarningsOlderThan(-1).get(1);
		earning1.put("timestamp", "0");
		
		// large numbers on purpose trying to overflow int
		assertTrue(edeka.getEarningsOlderThan(1000000000).contains(earning1));
		assertFalse(edeka.getEarningsOlderThan(1000000000).contains(earning2));
	}

	
	/**
	 * Tests for prolongActive.
	 */
	@Test
	public void testProlongActive() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market edeka = Market.createShop();
		zone.add(edeka);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item);
		
		Offer offer = edeka.createOffer(bob, item, 10, 1);
		offer.put("timestamp", "0");
		
		edeka.prolongOffer(bob, offer);
		assertTrue(edeka.getOffersOlderThan(1000).size() == 0);
		assertTrue(edeka.getOffers().size() == 1);
	}
	
	/**
	 * Tests for prolongExpired.
	 */
	@Test
	public void testProlongExpired() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market edeka = Market.createShop();
		zone.add(edeka);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item);
		
		Offer offer = edeka.createOffer(bob, item, 10, 1);
		offer.put("timestamp", "0");
		edeka.expireOffer(offer);
		
		edeka.prolongOffer(bob, offer);
		assertTrue(edeka.getOffersOlderThan(1000).size() == 0);
		assertTrue(edeka.getOffers().size() == 1);
		assertTrue(edeka.getExpiredOffers().size() == 0);
	}
	
	/**
	 * Tests for prolongCompletelyExpired.
	 */
	@Test
	public void testProlongCompletelyExpired() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market edeka = Market.createShop();
		zone.add(edeka);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item);
		
		Offer offer = edeka.createOffer(bob, item, 10, 1);
		offer.put("timestamp", "0");
		edeka.expireOffer(offer);
		edeka.removeExpiredOffer(offer);
		
		edeka.prolongOffer(bob, offer);
		
		assertTrue(edeka.getOffers().size() == 0);
		assertTrue(edeka.getExpiredOffers().size() == 0);
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
		Market edeka = Market.createShop();
		zone.add(edeka);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		StackableItem erniesMoney = (StackableItem) SingletonRepository
				.getEntityManager().getItem("money");
		Integer price = Integer.valueOf(10);
		erniesMoney.setQuantity(price);
		george.equipToInventoryOnly(item);
		
		// ensure different CIDs
		CIDSubmitAction.nameList.put("george", "georgescid");
		CIDSubmitAction.nameList.put("ernie", "erniescid");
		
		Offer offer = edeka.createOffer(george, item, price, 1);
		Player ernie = PlayerTestHelper.createPlayer("ernie");
		ernie.equipToInventoryOnly(erniesMoney);
		
		assertThat(ernie.getTradescore(), is(0));
		assertThat(george.getTradescore(), is(0));
		
		edeka.acceptOffer(offer, ernie);
		
		assertThat(ernie.getTradescore(), is(1));
		assertThat(george.getTradescore(), is(0));
		
		edeka.fetchEarnings(george);
		
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
		Market edeka = Market.createShop();
		zone.add(edeka);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		StackableItem erniesMoney = (StackableItem) SingletonRepository
				.getEntityManager().getItem("money");
		Integer price = Integer.valueOf(10);
		erniesMoney.setQuantity(price);
		george.equipToInventoryOnly(item);
		
		CIDSubmitAction.nameList.put("ernie", "erniescid");
		
		Offer offer = edeka.createOffer(george, item, price, 1);
		Player ernie = PlayerTestHelper.createPlayer("ernie");
		ernie.equipToInventoryOnly(erniesMoney);
		
		assertThat(ernie.getTradescore(), is(0));
		assertThat(george.getTradescore(), is(0));
		
		edeka.acceptOffer(offer, ernie);
		
		assertThat(ernie.getTradescore(), is(0));
		assertThat(george.getTradescore(), is(0));
		
		edeka.fetchEarnings(george);
		
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
		Market edeka = Market.createShop();
		zone.add(edeka);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		StackableItem erniesMoney = (StackableItem) SingletonRepository
				.getEntityManager().getItem("money");
		Integer price = Integer.valueOf(10);
		erniesMoney.setQuantity(price);
		george.equipToInventoryOnly(item);
		
		CIDSubmitAction.nameList.put("george", "georgescid");
		
		Offer offer = edeka.createOffer(george, item, price, 1);
		Player ernie = PlayerTestHelper.createPlayer("ernie");
		ernie.equipToInventoryOnly(erniesMoney);
		
		assertThat(ernie.getTradescore(), is(0));
		assertThat(george.getTradescore(), is(0));
		
		edeka.acceptOffer(offer, ernie);
		
		assertThat(ernie.getTradescore(), is(0));
		assertThat(george.getTradescore(), is(0));
		
		edeka.fetchEarnings(george);
		
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
		Market edeka = Market.createShop();
		zone.add(edeka);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		StackableItem money = (StackableItem) SingletonRepository
				.getEntityManager().getItem("money");
		Integer price = Integer.valueOf(10);
		money.setQuantity(price);
		george.equipToInventoryOnly(item);
		george.equipToInventoryOnly(money);
		
		CIDSubmitAction.nameList.put("george", "georgescid");
		
		Offer offer = edeka.createOffer(george, item, price, Integer.valueOf(1));
		
		assertThat(george.getTradescore(), is(0));
		
		// switch cid in between
		CIDSubmitAction.nameList.put("george", "georgesfakecid");
		edeka.acceptOffer(offer, george);
		
		assertThat(george.getTradescore(), is(0));
		
		edeka.fetchEarnings(george);
		
		assertThat(george.getTradescore(), is(0));
	}
}
