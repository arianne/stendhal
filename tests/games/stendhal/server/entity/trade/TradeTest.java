package games.stendhal.server.entity.trade;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.AfterClass;
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
	
	@Test
	public void testCreateOffer() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market edeka = Market.createShop();
		zone.add(edeka);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item);
		
		Offer offer = edeka.createOffer(bob, item, 10);
		
		assertTrue(edeka.getOffers().contains(offer));
		assertNull(bob.getFirstEquipped("axe"));
	}

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
		Offer offer = edeka.createOffer(george, item, price);
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
	
	@Test
	public void testCreateNonExistingOffer() {
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market edeka = Market.createShop();
		zone.add(edeka);
		Player george = PlayerTestHelper.createPlayer("george");
		Offer offer = edeka.createOffer(george, null, 42);
		assertNull("Creating offers for non existing items should fail", offer);
	}
	
	@Test
	public void testCreateOfferForBoundItem() {
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market edeka = Market.createShop();
		zone.add(edeka);
		Player george = PlayerTestHelper.createPlayer("george");
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		item.setBoundTo("george");
		george.equipToInventoryOnly(item);
		
		Offer offer = edeka.createOffer(george, item, 42);
		assertNull("Creating offers for non bound items should fail", offer);
		assertThat(george.isEquipped("axe"), is(Boolean.TRUE));
	}

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
		Offer offer = new Offer(item, price, george.getName() );

		Player ernie = PlayerTestHelper.createPlayer("ernie");
		ernie.equipToInventoryOnly(erniesMoney);
		assertThat(ernie.isEquipped("money", price), is(Boolean.TRUE));
		edeka.acceptOffer(offer, ernie);
		assertThat(Boolean.valueOf(ernie.isEquipped("axe")), is(Boolean.FALSE));
		assertThat(ernie.isEquipped("money", price), is(Boolean.TRUE));
	}
	
	
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
		Offer offer = edeka.createOffer(bob, item, price);

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
	public void testExpireOffer() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market edeka = Market.createShop();
		zone.add(edeka);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item);
		
		Offer offer = edeka.createOffer(bob, item, 10);
		
		edeka.expireOffer(offer);
		assertFalse(edeka.getOffers().contains(offer));
		assertTrue(edeka.getExpiredOffers().contains(offer));
	}
	
	@Test
	public void testRemoveExpiredOffer() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market edeka = Market.createShop();
		zone.add(edeka);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item);
		
		Offer offer = edeka.createOffer(bob, item, 10);
		
		edeka.expireOffer(offer);
		edeka.removeExpiredOffer(offer);
		assertFalse(edeka.getOffers().contains(offer));
		assertFalse(edeka.getExpiredOffers().contains(offer));
	}
	
	@Test
	public void testRemoveOffer() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market edeka = Market.createShop();
		zone.add(edeka);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item);
		
		Offer offer = edeka.createOffer(bob, item, 10);
		
		edeka.removeOffer(offer, bob);
		assertFalse(edeka.getOffers().contains(offer));
		assertFalse(edeka.getExpiredOffers().contains(offer));
		assertTrue(bob.getFirstEquipped("axe") != null);
	}
	
	// returning the item to player from an offer that has expired
	@Test
	public void testRemoveExpiredOffer2() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market edeka = Market.createShop();
		zone.add(edeka);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item);
		
		Offer offer = edeka.createOffer(bob, item, 10);
		
		edeka.expireOffer(offer);
		edeka.removeOffer(offer, bob);
		
		assertFalse(edeka.getOffers().contains(offer));
		assertFalse(edeka.getExpiredOffers().contains(offer));
		assertTrue(bob.getFirstEquipped("axe") != null);
	}
	
	@Test
	public void testRemoveNonExistingOffer() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market edeka = Market.createShop();
		zone.add(edeka);
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item);
		
		// make an offer to the shop and make it disappear
		Offer offer = edeka.createOffer(bob, item, 10);
		edeka.expireOffer(offer);
		edeka.removeExpiredOffer(offer);
		
		edeka.removeOffer(offer, bob);
		bob.drop(item);
		
		assertFalse(edeka.getOffers().contains(offer));
		assertFalse(edeka.getExpiredOffers().contains(offer));
		assertNull(bob.getFirstEquipped("axe"));
	}
	
	@Test
	public void testGetOffersOlderThan() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market edeka = Market.createShop();
		zone.add(edeka);
		
		assertTrue(edeka.getOffersOlderThan(10000).size() == 0);
		
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item);
		Offer offer = edeka.createOffer(bob, item, 10);
		offer.put("timestamp", "0");
		
		Item item2 = SingletonRepository.getEntityManager().getItem("cheese");
		bob.equipToInventoryOnly(item2);
		Offer offer2 = edeka.createOffer(bob, item2, 10);
		
		// large numbers on purpose trying to overflow int
		assertTrue(edeka.getOffersOlderThan(1000000000).contains(offer));
		assertFalse(edeka.getOffersOlderThan(1000000000).contains(offer2));
	}
	
	@Test
	public void testGetExpiredOffersOlderThan() {
		Player bob = PlayerTestHelper.createPlayer("bob");
		StendhalRPZone zone = new StendhalRPZone("shop");
		Market edeka = Market.createShop();
		zone.add(edeka);
		
		assertTrue(edeka.getExpiredOffersOlderThan(10000).size() == 0);
		
		Item item = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item);
		Offer offer = edeka.createOffer(bob, item, 10);
		offer.put("timestamp", "0");
		edeka.expireOffer(offer);
		
		Item item2 = SingletonRepository.getEntityManager().getItem("axe");
		bob.equipToInventoryOnly(item2);
		Offer offer2 = edeka.createOffer(bob, item2, 11);
		edeka.expireOffer(offer2);
		
		// large numbers on purpose trying to overflow int
		assertTrue(edeka.getExpiredOffersOlderThan(1000000000).contains(offer));
		assertFalse(edeka.getExpiredOffersOlderThan(1000000000).contains(offer2));
	}
}
